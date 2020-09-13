package ca.louie.spotify;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JobCompletionNotificationListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("Started job: " + jobExecution.getJobConfigurationName());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		log.info("Finished job: " + jobExecution.getJobConfigurationName());
	}
}
