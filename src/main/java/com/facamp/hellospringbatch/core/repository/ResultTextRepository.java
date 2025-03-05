package com.facamp.hellospringbatch.core.repository;

import com.facamp.hellospringbatch.core.domain.PlainText;
import com.facamp.hellospringbatch.core.domain.ResultText;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultTextRepository extends JpaRepository<ResultText, Integer> {
  Page<ResultText> findBy(Pageable pageable);
}
