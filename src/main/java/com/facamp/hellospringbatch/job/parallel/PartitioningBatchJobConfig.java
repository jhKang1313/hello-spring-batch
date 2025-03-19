package com.facamp.hellospringbatch.job.parallel;


import com.facamp.hellospringbatch.core.domain.PlainText;
import com.facamp.hellospringbatch.core.domain.ResultText;
import com.facamp.hellospringbatch.core.repository.PlainTextRepository;
import com.facamp.hellospringbatch.core.repository.ResultTextRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import javax.xml.transform.Result;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PartitioningBatchJobConfig {
  private static final Logger log = LoggerFactory.getLogger(PartitioningBatchJobConfig.class);
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;


  private final PlainTextRepository srcRepo;
  private final ResultTextRepository trgRepo;

  @Bean
  public Job simpleJob(Step simpleStep) {
    return jobBuilderFactory.get("simpleJob")
        .incrementer(new RunIdIncrementer())
        .start(simpleStep)
        .build();
  }

  @JobScope
  @Bean
  public Step simpleStep(ItemReader<PlainText> simpleReader,
                         ItemReadListener<PlainText> itemReadListener,
                         ItemProcessor<PlainText, List<ResultText>> simpleProcessor,
                         ItemWriter<List<ResultText>> simpleWriter,
                         ItemWriteListener<List<ResultText>> simpleWriterListener){
    return stepBuilderFactory.get("simpleStep")
        .<PlainText, List<ResultText>>chunk(10)
        .reader(simpleReader)
        .listener(itemReadListener)
        .processor(simpleProcessor)
        .writer(simpleWriter)
        .listener(simpleWriterListener)
        .build();
  }

  @Bean
  @StepScope
  public ItemReadListener<PlainText> itemReadListener(){
    return new ItemReadListener<PlainText>() {
      @Override
      public void beforeRead() {

      }
      @Override
      public void afterRead(PlainText plainText) {
        srcRepo.updateState("P", plainText.getText());
      }
      @Override
      public void onReadError(Exception e) {

      }
    };
  }

  @StepScope
  @Bean
  public ItemReader<PlainText> simpleReader() {
    return new RepositoryItemReaderBuilder()
        .name("simpleReader")
        .repository(srcRepo)
        .methodName("findByRfltYn")
        .pageSize(10)
        .arguments(List.of("N"))
        .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
        .build();
  }
  @StepScope
  @Bean
  public ItemProcessor<PlainText, List<ResultText>> simpleProcessor(){
    return item -> {
      List<ResultText> resultList = new ArrayList<>();
      List<PlainText> srcList = srcRepo.findByText(item.getText());

      srcList.forEach(srcItem -> {
        resultList.add(new ResultText(srcItem.getId(), srcItem.getText()));
      });
      return resultList;
    };
  }

  @StepScope
  @Bean
  public ItemWriter<List<ResultText>> simpleWriter(){
    return items -> {
      items.forEach(item -> trgRepo.saveAll(item));
    };
  }

  @StepScope
  @Bean
  public ItemWriteListener<List<ResultText>> simpleWriterListener(){
    return new ItemWriteListener<List<ResultText>>() {
      @Override
      public void beforeWrite(List<? extends List<ResultText>> list) {

      }

      @Override
      public void afterWrite(List<? extends List<ResultText>> list) {
        log.info("#### After Write ####");
        list.get(0);
        srcRepo.updateState("F", list.get(0).get(0).getText());

      }

      @Override
      public void onWriteError(Exception e, List<? extends List<ResultText>> list) {

      }
    };
  }
}
