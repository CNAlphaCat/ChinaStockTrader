package cn.alphacat.chinastocktrader.repository;

import cn.alphacat.chinastocktrader.entity.IndexPEEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IndexPERepository extends JpaRepository<IndexPEEntity, Long> {
  List<IndexPEEntity> findByDateIsGreaterThanEqual(LocalDate date);

  @Query("SELECT ipe.date FROM IndexPEEntity ipe ORDER BY ipe.date DESC LIMIT 1")
  Optional<LocalDate> findTop1DateOrderByDateDesc();
}
