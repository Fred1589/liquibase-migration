package com.fb.commons.liquibase;

public enum LiquibaseOperation {

	DROPALL,
	UPDATE,
	ROLLBACK,
	DIFF,
	SQL_OUTPUT,
	DBDOC,
	VALIDATE;
}
