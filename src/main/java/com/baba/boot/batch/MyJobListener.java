package com.baba.boot.batch;

import org.springframework.batch.core.JobExecutionListener;

public class MyJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(org.springframework.batch.core.JobExecution jobExecution) {
		System.out.println("Job started");
	}

	@Override
	public void afterJob(org.springframework.batch.core.JobExecution jobExecution) {
		System.out.println("Job ended"+jobExecution.getStatus().toString());
	}

	}
