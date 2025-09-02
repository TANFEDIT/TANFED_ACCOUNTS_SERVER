package com.tanfed.accounts.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.ClosingBalanceTable;
@Repository
public interface ClosingBalanceRepo extends JpaRepository<ClosingBalanceTable, Long> {

	public List<ClosingBalanceTable> findByOfficeNameAndDate(String officeName, LocalDate date);

}
