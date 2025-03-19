package com.facamp.hellospringbatch.core.service;

import com.facamp.hellospringbatch.dto.PlayerDto;
import com.facamp.hellospringbatch.dto.PlayerSalaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerSalaryServiceTest {
  private PlayerSalaryService playerSalaryService;

  @BeforeEach
  public void setup(){
    playerSalaryService = new PlayerSalaryService();
  }

  @Test
  public void calcSalay(){
    //Given
    Year mockYear = mock(Year.class);
    when(mockYear.getValue()).thenReturn(2025);
    Mockito.mockStatic(Year.class).when(Year::now).thenReturn(mockYear);

    PlayerDto mockPlayer = mock(PlayerDto.class);
    when(mockPlayer.getBirthYear()).thenReturn(1992);

    //When
    PlayerSalaryDto result = playerSalaryService.calcSalary(mockPlayer);

    //Then
    assertEquals(result.getSalary(), 33000000);
  }

}