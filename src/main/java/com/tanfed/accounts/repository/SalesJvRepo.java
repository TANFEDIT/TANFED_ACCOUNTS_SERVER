package com.tanfed.accounts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.SalesJvTable;

@Repository
public interface SalesJvRepo extends JpaRepository<SalesJvTable, Long> {

	public List<SalesJvTable> findByOfficeName(String officeName);
}
