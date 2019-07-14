package com.fb.commons.liquibase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

public class StageProperties {
	private static final Logger LOG = Logger.getLogger(StageProperties.class.getName());

	private static final String URL_PROPERTY = "schema.url";
	private static final String USERNAME_PROPERTY = "schema.user";
	private static final String PASSWORD_PROPERTY = "schema.password";
	private static final String NAME_PROPERTY = "schema.name";
	private static final String DIFF_URL_PROPERTY = "diff.schema.url";
	private static final String DIFF_USERNAME_PROPERTY = "diff.schema.user";
	private static final String DIFF_PASSWORD_PROPERTY = "diff.schema.password";
	private static final String DIFF_NAME_PROPERTY = "diff.schema.name";
	private static final String CONNECT_USERNAME_PROPERTY = "connect.user";
	private static final String CONNECT_PASSWORD_PROPERTY = "connect.password";
	private static final String LOCATIONS_PROPERTY = "locations";
	private static final String VERSION_TABLE_PROPERTY = "table";
	private static final String ROOT_CHANGE_LOG_NAME = "changelogname";

	private final Properties properties;
	private final String projectName;

	public StageProperties(Properties properties, String projectName) {
		super();
		this.properties = properties;
		this.projectName = projectName;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public String getUrl() {
		return getProperty(URL_PROPERTY);
	}

	public String getUserName() {
		return getProperty(USERNAME_PROPERTY);
	}

	public String getPassword() {
		return getProperty(PASSWORD_PROPERTY);
	}

	public String getSchemaName() {
		return getOptionalProperty(NAME_PROPERTY);
	}

	public String getDiffUrl() {
		return getOptionalProperty(DIFF_URL_PROPERTY);
	}

	public String getDiffUserName() {
		return getOptionalProperty(DIFF_USERNAME_PROPERTY);
	}

	public String getDiffPassword() {
		return getOptionalProperty(DIFF_PASSWORD_PROPERTY);
	}

	public String getDiffSchemaName() {
		return getOptionalProperty(DIFF_NAME_PROPERTY);
	}

	public String getLocations() {
		return getOptionalProperty(LOCATIONS_PROPERTY);
	}

	public String getVersionTable() {
		return getOptionalProperty(VERSION_TABLE_PROPERTY);
	}

	public String getRootChangeLogName() {
		return getOptionalProperty(ROOT_CHANGE_LOG_NAME);
	}

	public String getConnectUserName() {
		return getProperty(CONNECT_USERNAME_PROPERTY);
	}

	public String getConnectPassword() {
		return getProperty(CONNECT_PASSWORD_PROPERTY);
	}

	/**
	 * Get property value from enviroment properties (Stage properties). Generate
	 * property name as "execution context . property constant" Check whether
	 * property is defined and throw an exception otherwise
	 * 
	 * @param options
	 * @param propertyName
	 * @param environmentProperties
	 * @return
	 */
	public String getProperty(final String propertyName) {
		final String fullProperty = projectName + "." + propertyName;
		final String value = properties.getProperty(fullProperty);
		if (value == null) {
			throw new MigrationException("Could not find environment property " + fullProperty);
		}
		return value;
	}

	/**
	 * like {@link #getProperty(String)}, but does not throw an exception if the
	 * property is not defined. Return an empty String
	 * 
	 * @param propertyName
	 * @return property value or &quot;&quot;
	 */
	public String getOptionalProperty(final String propertyName) {
		try {
			return getProperty(propertyName);
		} catch (MigrationException ex) {
			return "";
		}
	}

	public void verify() {
		if (isEmpty(getUrl())) {
			emptyError(URL_PROPERTY);
		}
	}

	public void logAllProperties() {
		LOG.info("All defined properties:");
		final List<String> entries = new ArrayList<String>();
		for (Entry<Object, Object> entry : properties.entrySet()) {
			final String value = entry.getKey() + "=" + entry.getValue();
			entries.add(value);
		}

		// Sort output
		Collections.sort(entries);

		// Print values
		for (String value : entries) {
			LOG.info(value);
		}
	}

	private void emptyError(String propertyName) {
		final String fullProperty = projectName + "." + propertyName;
		throw new MigrationException("Property undefined: " + fullProperty);
	}

	private boolean isEmpty(final String string) {
		if (string == null || string.trim().isEmpty()) {
			return true;
		}
		return false;
	}

}
