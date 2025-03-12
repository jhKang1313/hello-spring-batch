package com.facamp.hellospringbatch.core.repository;

import com.facamp.hellospringbatch.core.domain.PlainText;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlainTextRepository extends JpaRepository<PlainText, Integer> {
  Page<PlainText> findBy(Pageable pageable);

  Page<PlainText> findByRfltYn(String rfltYn, Pageable pageable);

  List<PlainText> findByText(String text);

  @Modifying
  @Query("update PlainText a set a.rfltYn = :rfltYn where a.text = :text")
  int updateState(@Param("rfltYn") String rfltYn,
                  @Param("text") String text);
}
