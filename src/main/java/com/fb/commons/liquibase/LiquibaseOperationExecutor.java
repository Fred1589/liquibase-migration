package com.fb.commons.liquibase;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;

public class LiquibaseOperationExecutor {

	private static final Logger LOG = Logger.getLogger(LiquibaseOperationExecutor.class.getName());

	// Diff arguments
	private Database referenceDatabase;
	private Database targetDatabase;
	// Rollback arguments
	private Integer tag;

	public void processOperation(final LiquibaseOperation operation, final Liquibase liquibase) {

		try {
			switch (operation) {
			case DROPALL:
				LOG.info("Start database clean operation");
				System.out.print("Are you going to delete all database object?(Y/N): ");
				cleanAfterConfirmation(liquibase);
				return;
			case UPDATE:
				LOG.info("Start migration");
				liquibase.update(new Contexts());
				return;
			case ROLLBACK:
				LOG.info("Start rollback");
				liquibase.rollback(tag, "");
				return;
			case DIFF:
				LOG.info("Start create diff");
				DiffResult diff = liquibase.diff(referenceDatabase, targetDatabase, CompareControl.STANDARD);
				DiffToChangeLog diffChangeLog = new DiffToChangeLog(diff, new DiffOutputControl());
				diffChangeLog.setChangeSetAuthor("MigrationTool");
				diffChangeLog.print(System.out);
				return;
			case VALIDATE:
				LOG.info("Validate migration");
				liquibase.validate();
				return;
			default:
				throw new MigrationException("Unknown operation: " + operation);
			}
		} catch (LiquibaseException e) {
			throw new MigrationException("LiquibaseException occured", e);
		} catch (ParserConfigurationException | IOException e) {
			throw new MigrationException("Diff output cannot be written", e);
		}
	}

	/**
	 * Ask user to confirm cleanup operation and clean.
	 * 
	 * @param liquibase
	 */
	private void cleanAfterConfirmation(final Liquibase liquibase) {
		try (Scanner in = new Scanner(System.in)) {
			if ("Y".equalsIgnoreCase(in.nextLine())) {
				LOG.info("Clean operation was acknowledged");
				liquibase.dropAll();
			} else {
				LOG.info("Clean operation was canceled");
			}
		} catch (DatabaseException e) {
			throw new MigrationException("Failure while drop all from schema", e);
		}
	}

	public Database getReferenceDatabase() {
		return referenceDatabase;
	}

	public void setReferenceDatabase(Database referenceDatabase) {
		this.referenceDatabase = referenceDatabase;
	}

	public Database getTargetDatabase() {
		return targetDatabase;
	}

	public void setTargetDatabase(Database targetDatabase) {
		this.targetDatabase = targetDatabase;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}

}
