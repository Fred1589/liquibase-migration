package com.fb.commons.liquibase;

import liquibase.Liquibase;
import liquibase.configuration.ConfigurationContainer;
import liquibase.configuration.GlobalConfiguration;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.database.Database;
import liquibase.logging.core.DefaultLoggerConfiguration;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

public class LiquibaseBuilder {

	private Database database;
	private ResourceAccessor resourceAccessor;
	private String rootChangeLog;
	private String changeLogTableName;

	public LiquibaseBuilder withDatabase(final Database database) {
		this.database = database;
		return this;
	}

	public LiquibaseBuilder withResourceAccessor() {
		this.resourceAccessor = new ClassLoaderResourceAccessor();
		return this;
	}

	public LiquibaseBuilder withDatabaseChangeLog(final String location, final String changeLogName) {
		this.rootChangeLog = location + changeLogName;
		return this;
	}

	public LiquibaseBuilder withChangeLogTableName(final String changeLogTableName) {
		this.changeLogTableName = changeLogTableName;
		return this;
	}

	/**
	 * Build Liquibase instance
	 * 
	 * @return Liquibase
	 */
	public Liquibase build() {
		Liquibase liquibase = new Liquibase(rootChangeLog, resourceAccessor, database);
		ConfigurationContainer configurationContainer = LiquibaseConfiguration.getInstance()
				.getConfiguration(GlobalConfiguration.class);
		configurationContainer.setValue(GlobalConfiguration.DATABASECHANGELOG_TABLE_NAME, changeLogTableName);
		configurationContainer.setValue(GlobalConfiguration.DATABASECHANGELOGLOCK_TABLE_NAME,
				changeLogTableName + "Lock");
		DefaultLoggerConfiguration config = LiquibaseConfiguration.getInstance()
				.getConfiguration(DefaultLoggerConfiguration.class);
		config.setLogLevel("info");
		return liquibase;
	}

}
