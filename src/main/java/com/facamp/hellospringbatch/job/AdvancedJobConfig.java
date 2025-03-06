package com.facamp.hellospringbatch.job;

import com.facamp.hellospringbatch.job.validator.LocalDateParameterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.build.AllowPrintStacktrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdvancedJobConfig {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean("advancedJob")
  public Job advancedJob(Step advancedStep){
    return jobBuilderFactory.get("advancedJob")
        .incrementer(new RunIdIncrementer())
        .validator(new LocalDateParameterValidator("targetDate"))
        .start(advancedStep)
        .build();
  }

  @JobScope
  @Bean("advancedStep")
  public Step advancedStep(Tasklet advancedTasklet){
    return stepBuilderFactory.get("advancedStep")
        .tasklet(advancedTasklet)
        .build();
  }
  @StepScope
  @Bean
  public Tasklet advancedTasklet(@Value("#{jobParameters['targetDate']}") String targetDate){
    return (cont, chunk) -> {
      log.info("advancedTasklet executed");
      LocalDate date = LocalDate.parse(targetDate); //파씽 안될때 오류 -> 실행 전 오류발생하도록 validator 사용

      log.info("targetDate: {}", targetDate);
      return RepeatStatus.FINISHED;
    };
  }
}
