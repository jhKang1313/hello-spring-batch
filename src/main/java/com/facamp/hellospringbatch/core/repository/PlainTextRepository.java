package com.facamp.hellospringbatch.core.repository;

import com.facamp.hellospringbatch.core.domain.PlainText;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlainTextRepository extends JpaRepository<PlainText, Integer> {
  Page<PlainText> findBy(Pageable pageable);
}
