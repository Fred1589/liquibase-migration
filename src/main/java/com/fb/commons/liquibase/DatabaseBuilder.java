package com.fb.commons.liquibase;

import java.security.GeneralSecurityException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.fb.commons.crypt.Crypt;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;

public final class DatabaseBuilder {

	private DatabaseBuilder() {
		// empty constructor
	}

	/**
	 * Create database connection to configured database. Actually there are two
	 * database types supported (Oracle, PostgreSQL).
	 *
	 * @param properties   - database connection parameters url, user, password
	 * @param databaseType - Database to connect to
	 * @param privateKey   - to decrypt the password to connect to database
	 * @return
	 */
	public static Database buildDatabase(final StageProperties properties, final DatabaseType databaseType,
			final byte[] privateKey) {
		try {
			return buildDatabase(properties, databaseType, privateKey, false);
		} catch (Exception e) {
			throw new MigrationException("Cannot open connection to database", e);
		}
	}

	/**
	 * Create database connection to given diff database. Actually there are two
	 * database types supported (Oracle, PostgreSQL).
	 *
	 * @param properties   - database connection parameters url, user, password
	 * @param databaseType - Database to connect to
	 * @param privateKey   - to decrypt the password to connect to database
	 * @return
	 */
	public static Database buildDiffDatabase(final StageProperties properties, final DatabaseType databaseType,
			final byte[] privateKey) {
		try {
			return buildDatabase(properties, databaseType, privateKey, true);
		} catch (Exception e) {
			throw new MigrationException("Cannot open connection to diff database", e);
		}
	}

	private static Database buildDatabase(final StageProperties properties, final DatabaseType databaseType,
			final byte[] privateKey, boolean diff) throws SQLException, GeneralSecurityException, DatabaseException {
		JdbcConnection connection = null;
		if (diff) {
			connection = new JdbcConnection(DriverManager.getConnection(properties.getDiffUrl(),
					properties.getDiffUserName(), Crypt.decrypt(privateKey, properties.getDiffPassword())));
		} else {
			connection = new JdbcConnection(DriverManager.getConnection(properties.getUrl(), properties.getUserName(),
					Crypt.decrypt(privateKey, properties.getPassword())));
		}

		checkConnection(connection, databaseType);
		Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
		database.setDefaultSchemaName(diff ? properties.getDiffSchemaName() : properties.getSchemaName());

		return database;
	}

	private static void checkConnection(JdbcConnection connection, DatabaseType databaseType) {
		try (Statement stmt = connection.createStatement()) {
			switch (databaseType) {
			case ORACLE:
				stmt.execute("SELECT 1 FROM DUAL");
				break;
			case POSTGRESQL:
				stmt.execute("SELECT 1");
				break;
			default:
				throw new IllegalArgumentException("Unknown database type: " + databaseType);
			}
		} catch (SQLException | DatabaseException e) {
			throw new MigrationException("Could not validate datasource", e);
		}
	}
}
