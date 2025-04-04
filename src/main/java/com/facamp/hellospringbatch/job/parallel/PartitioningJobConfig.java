package com.facamp.hellospringbatch.job.parallel;

/*
* 단일 프로세스에서 master step 과 work step 을 두고, master step 에서 생성해준 partition 단위로 step을 병렬 처리
* */

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
@AllArgsConstructor
public class PartitioningJobConfig {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private static final int PARTITION_SIZE = 100;

  @Bean
  public Job partitioningJob(Step masterStep){
    return jobBuilderFactory.get("partitioningJob")
        .incrementer(new RunIdIncrementer())
        .start(masterStep)
        .build();
  }

  @Bean
  @JobScope
  public Step masterStep(Partitioner partitioner,
                         TaskExecutorPartitionHandler partitionHandler) {
    return stepBuilderFactory.get("masterStep")
        .partitioner("anotherStep", partitioner)
        .partitionHandler(partitionHandler)
        .build();
  }

  @StepScope
  @Bean
  public Partitioner partitioner(){
    SimplePartitioner partitioner = new SimplePartitioner();
    partitioner.partition(PARTITION_SIZE);
    return partitioner;

  }

  @StepScope
  @Bean
  public TaskExecutorPartitionHandler partitionHandler(Step anotherStep,
                                                       TaskExecutor taskExecutor){
    TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
    partitionHandler.setStep(anotherStep);
    partitionHandler.setGridSize(PARTITION_SIZE);
    partitionHandler.setTaskExecutor(taskExecutor);
    return partitionHandler;
  }
}
