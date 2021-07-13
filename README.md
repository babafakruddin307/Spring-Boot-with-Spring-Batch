# Spring-Boot-with-Spring-Batch
Spring Batch processing

A job can have more than one steps – and every step typically follows the sequence of reading data, processing it and writing it.

Each Step has Item Reader, Item Processor, Item Writer.

StepBuilderFactory is created and Pass as a parameter to JobBulderFactory

**Job Listener class**
**Listener class will execute before and after execution of class**
```
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

```
**Read class**

```
package com.baba.boot.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class Reader implements ItemReader<String> {
	private String[] courses= {"java web services","spring boot","microservices"};
	private int count;
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		System.out.println("Inside Read Method	");
		if(count<courses.length) {
			return courses[count++];
		}else {
			count=0;
		}
		return null;
	}

}

```
**Processor class**

```
package com.baba.boot.batch;

import org.springframework.batch.item.ItemProcessor;

public class Processor implements ItemProcessor<String, String>{

	@Override
	public String process(String item) throws Exception {
		System.out.println("Inside Processor");
		return "PROCESSOR"+item.toUpperCase();
	}

}

```
**writer class**
```
package com.baba.boot.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class Writer implements ItemWriter<String> {

	@Override
	public void write(List<? extends String> items) throws Exception {
		System.out.println("Inside writer");
		System.out.println("writing data"+items);
	}

}

```
**Builder Config class:Step builder factory class is created and passed as a paraameter to Job builder factory**

```
package com.baba.boot.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfig {
	@Autowired
	private StepBuilderFactory sbf;
	@Autowired
	private JobBuilderFactory jbf;
	@Bean
	public Job job() {
		return jbf.get("job1")
				.incrementer(new RunIdIncrementer())
				.listener(listener())
				.start(step()).build();
	}
	@Bean
	public Step step() {
		return sbf.get("Step1")
				.<String,String>chunk(2)
				.reader(reader())
				.writer(writer())
				.processor(processor())
				.build();
	}
	@Bean
	public Reader reader() {
		return new Reader();
	}
	@Bean
	public Processor processor() {
		return new Processor();
	}
	@Bean
	public Writer writer() {
		return new Writer();
	}
	@Bean
	public MyJobListener listener() {
		return new MyJobListener();
	}
}

```

**Testing Batch application class**
```
package com.baba.boot.batch;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class SpringBatchApplicationTests {
	@Autowired
	JobLauncher launcher;
	@Autowired
	Job job;
	@Test
	public void testBatch() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		JobParameters jobParameter=new JobParametersBuilder().addLong("time",System.currentTimeMillis()).toJobParameters();
		launcher.run(job, jobParameter);
	}

}

```
