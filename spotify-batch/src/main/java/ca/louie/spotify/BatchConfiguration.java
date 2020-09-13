package ca.louie.spotify;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import ca.louie.spotify.service.DataScraping;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public FlatFileItemReader<String> reader() {
		// @formatter:off
		return new FlatFileItemReaderBuilder<String>()
				.name("spotifyIdItemReader")
				.resource(new FileSystemResource("data-short.txt"))
				.lineMapper(new PassThroughLineMapper())
				.build(); 
		// @formatter:on
	}

	@Bean
	public ArtistSpotifyIdItemProcessor processor(DataScraping scraper) {
		return new ArtistSpotifyIdItemProcessor(scraper);
	}

	@Bean
	public ItemWriter<String> writer() {
		return new ItemWriter<String>() {

			@Override
			public void write(List<? extends String> items) throws Exception {
				for (String string : items) {
					log.info("Step writer called: " + string);
				}
			}
		};
	}

	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor("spring_batch");
	}

	@Bean
	public Job importArtistsJob(JobCompletionNotificationListener listener, Step step1) {
		// @formatter:off
		return jobBuilderFactory
				.get("importArtistsJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1)
				.end()
				.build();
		// @formatter:on
	}

	@Bean
	public Step step1(TaskExecutor taskExecutor, DataScraping scraper) {
		// @formatter:off
		return stepBuilderFactory
				.get("step1")
				.<String, String> chunk(10)
				.reader(reader())
				.processor(processor(scraper))
				.writer(writer())
				.taskExecutor(taskExecutor)
				.throttleLimit(20)
				.build();
		// @formatter:on
	}

}
