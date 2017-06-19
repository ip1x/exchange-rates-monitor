package com.mrip1x.exchangeratesmonitor.repository;

import com.mrip1x.exchangeratesmonitor.model.ExchangeRate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    List<ExchangeRate> findByDateBetween(LocalDate start, LocalDate end, Sort sort);

}
