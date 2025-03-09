package com.facamp.hellospringbatch.job.player;

import com.facamp.hellospringbatch.dto.PlayerDto;
import com.facamp.hellospringbatch.dto.PlayerSalaryDto;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

@Configuration
@AllArgsConstructor
public class FlatFileJobConfig {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job flatFileJob(Step flatFileStep){
    return jobBuilderFactory.get("flatFileJob")
        .incrementer(new RunIdIncrementer())
        .start(flatFileStep)
        .build();
  }

  @JobScope
  @Bean
  public Step flatFileStep(FlatFileItemReader playerDtoFlatFileInteReader){
    return stepBuilderFactory.get("flatFileStep")
        .<PlayerDto, PlayerDto>chunk(5)
        .reader(playerDtoFlatFileInteReader)
        .writer(new ItemWriter<PlayerDto>() {
          @Override
          public void write(List<? extends PlayerDto> list) throws Exception {
            list.forEach(System.out::println);
          }
        })
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<PlayerDto> playerDtoFlatFileInteReader(){
    return new FlatFileItemReaderBuilder<PlayerDto>()
        .name("playerFileItemReader")
        .lineTokenizer(new DelimitedLineTokenizer())
        .linesToSkip(1)
        .fieldSetMapper(new PlayerFieldSetMapper())
        .resource(new FileSystemResource("player-list.txt"))
        .build();

  }
}
