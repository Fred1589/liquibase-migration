package com.fb.commons.liquibase;

public class ProgramOptions {

	// Operation
	private LiquibaseOperation operation;
	// Project
	private String project;
	// Stage
	private String stage;

	// Value to encrypt
	private String value;

	private Integer tag;

	public LiquibaseOperation getOperation() {
		return operation;
	}

	public void setOperation(final LiquibaseOperation operation) {
		this.operation = operation;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(final String stage) {
		this.stage = stage;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}

}
