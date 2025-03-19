package com.facamp.hellospringbatch.job;

import com.facamp.hellospringbatch.BatchTestConfig;
import com.facamp.hellospringbatch.core.domain.PlainText;
import com.facamp.hellospringbatch.core.repository.PlainTextRepository;
import com.facamp.hellospringbatch.core.repository.ResultTextRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {PlainTextJobConfig.class, BatchTestConfig.class})
public class PlainTextJobConfigTest {
  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private PlainTextRepository plainTextRepository;

  @Autowired
  private ResultTextRepository resultTextRepository;

  @AfterEach
  public void tearDown() throws Exception {
    plainTextRepository.deleteAll();
    resultTextRepository.deleteAll();
  }

  @Test
  public void success_givenNoPlainText() throws Exception {
    //given
    // no plainText

    //when
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();

    //then
    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    assertEquals(resultTextRepository.count(), 0);
  }

  @Test
  public void success_givenPlainText() throws Exception {
    //given
    givenPlainText(12);

    //when
    JobExecution jobExecution = jobLauncherTestUtils.launchJob();

    //then
    assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    assertEquals(resultTextRepository.count(), 12);
  }

  private void givenPlainText(Integer count){
    IntStream.range(0, count).forEach(
        item -> plainTextRepository.save(new PlainText(null, "text" + item, "N"))
    );
  }
}
