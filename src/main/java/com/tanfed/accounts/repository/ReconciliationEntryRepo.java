package com.tanfed.accounts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.ReconciliationEntry;
@Repository
public interface ReconciliationEntryRepo extends JpaRepository<ReconciliationEntry, Long> {

	public List<ReconciliationEntry> findByOfficeName(String officeName);
}
