package com.facamp.hellospringbatch.core.service;

import com.facamp.hellospringbatch.dto.PlayerDto;
import com.facamp.hellospringbatch.dto.PlayerSalaryDto;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class PlayerSalaryService {
  public PlayerSalaryDto calcSalary(PlayerDto player){
    int salay = (Year.now().getValue() - player.getBirthYear()) * 1000000;
    return PlayerSalaryDto.of(player, salay);
  }
}
