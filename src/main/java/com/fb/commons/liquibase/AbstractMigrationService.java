package com.fb.commons.liquibase;

import java.security.GeneralSecurityException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.fb.commons.crypt.Crypt;

import liquibase.database.Database;

public abstract class AbstractMigrationService {

	protected static final Logger LOG = Logger.getLogger(AbstractMigrationService.class.getName());

	// Init version
	protected static final String START_VERSION = "1.0.0";

	// Options
	private ProgramOptions options;
	// Environment Properties and System Properties
	private StageProperties properties;
	// Data source for migration
	private Database database;
	// Database for diff
	private Database diffDatabase;
	// The database type
	private DatabaseType databaseType;

	static {
		try {
			LogManager.getLogManager().readConfiguration();
		} catch (Exception e) {
			java.util.logging.Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			java.util.logging.Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}

	public void init(final String[] args, final byte[] privateKey) {
		final ProgrammOptionsParser parser = new ProgrammOptionsParser();

		LOG.fine("Parse commandline arguments");
		options = parser.parseOptions(args);

		// encrypt value given in the enrypt option
		if (options.getValue() != null) {
			try {
				LOG.info("Encrypted value = " + Crypt.encrypt(privateKey, options.getValue()));
			} catch (GeneralSecurityException e) {
				LOG.severe("Failed to encrypt");
				System.exit(1);
			}
			System.exit(0);
		}

		LOG.info("Read properties for project: " + getOptions().getProject() + ", stage: " + getOptions().getStage());
		properties = StagePropertiesReader.build().readEnviromentProperties(getOptions());
		properties.logAllProperties();
		properties.verify();

		if (properties.getUrl().startsWith("jdbc:oracle")) {
			LOG.info("ORACLE: synonyms and Oracle JDBC Data Source");
			databaseType = DatabaseType.ORACLE;
		}

		if (properties.getUrl().startsWith("jdbc:postgresql")) {
			LOG.info("PostgreSQL: no synonyms and  PostgreSQL JDBC Data Source");
			databaseType = DatabaseType.POSTGRESQL;
		}

		if (databaseType == null) {
			throw new IllegalArgumentException("Cannot determine database type from JDBC url!");
		}

		LOG.info("Create database for migration");
		database = DatabaseBuilder.buildDatabase(getProperties(), databaseType, privateKey);

		if (properties.getDiffUrl() != null) {
			diffDatabase = DatabaseBuilder.buildDiffDatabase(getProperties(), databaseType, privateKey);
		}
	}

	public abstract void execute();

	protected ProgramOptions getOptions() {
		return options;
	}

	protected StageProperties getProperties() {
		return properties;
	}

	public Database getDatabase() {
		return database;
	}

	public Database getDiffDatabase() {
		return diffDatabase;
	}

	protected DatabaseType getDatabaseType() {
		return databaseType;
	}
}
