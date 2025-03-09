package com.facamp.hellospringbatch.job.player;

import com.facamp.hellospringbatch.core.service.PlayerSalaryService;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
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
  public Step flatFileStep(FlatFileItemReader playerDtoFlatFileInteReader,
                           //ItemProcessor playerSalaryItemProcessor,
                           ItemProcessorAdapter<PlayerDto, PlayerSalaryDto> playSalaryItemProcessorAdapter
                           ){
    return stepBuilderFactory.get("flatFileStep")
        .<PlayerDto, PlayerSalaryDto>chunk(5)
        .reader(playerDtoFlatFileInteReader)
        //.processor(playerSalaryItemProcessor)
        .processor(playSalaryItemProcessorAdapter)
        .writer(new ItemWriter<PlayerSalaryDto>() {
          @Override
          public void write(List<? extends PlayerSalaryDto> list) throws Exception {
            list.forEach(System.out::println);
          }
        })
        .build();
  }

  @Bean
  @StepScope
  public ItemProcessor<PlayerDto, PlayerSalaryDto> playerSalaryItemProcessor(
      PlayerSalaryService playerSalaryService
  ){
    return new ItemProcessor<PlayerDto, PlayerSalaryDto>() {
      @Override
      public PlayerSalaryDto process(PlayerDto playerDto) throws Exception {
        return playerSalaryService.calcSalary(playerDto);
      }
    };
  }

  @StepScope
  @Bean
  public ItemProcessorAdapter playSalaryItemProcessorAdapter(PlayerSalaryService playerSalaryService){
    ItemProcessorAdapter<PlayerDto, PlayerSalaryDto> adapter = new ItemProcessorAdapter<>();
    adapter.setTargetObject(playerSalaryService);
    adapter.setTargetMethod("calcSalary");
    return adapter;
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
