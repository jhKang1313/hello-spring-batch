package com.facamp.hellospringbatch.job.validator;

import lombok.AllArgsConstructor;
import org.hibernate.internal.build.AllowPrintStacktrace;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@AllArgsConstructor
public class LocalDateParameterValidator implements JobParametersValidator {
  private String parameterName;
  @Override
  public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
    String localDate = jobParameters.getString(parameterName);

    if(!StringUtils.hasText(localDate)){
      throw new JobParametersInvalidException(parameterName + " is a reserved date");
    }
    try{
      LocalDate.parse(localDate);
    } catch (DateTimeParseException e) {
      throw new JobParametersInvalidException(parameterName + " is a reserved date");
    }

  }
}
