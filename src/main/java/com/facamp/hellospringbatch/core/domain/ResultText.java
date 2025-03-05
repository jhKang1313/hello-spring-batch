package com.facamp.hellospringbatch.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "RESULT_TEXT")
@AllArgsConstructor
@NoArgsConstructor
public class ResultText {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String text;
}
