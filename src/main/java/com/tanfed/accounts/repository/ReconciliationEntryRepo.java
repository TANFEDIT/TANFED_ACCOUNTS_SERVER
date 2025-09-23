package com.tanfed.accounts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.ReconciliationEntry;

@Repository
public interface ReconciliationEntryRepo extends JpaRepository<ReconciliationEntry, Long> {

	public List<ReconciliationEntry> findByOfficeName(String officeName);

	@Query("SELECT e FROM ReconciliationEntry e WHERE (e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified') AND e.officeName =:officeName")
	public List<ReconciliationEntry> findPendingDataByOfficeName(@Param("officeName") String officeName);
	
	@Query("SELECT e FROM ReconciliationEntry e WHERE e.voucherStatus = 'Approved' AND e.officeName =:officeName")
	public List<ReconciliationEntry> findApprovedDataByOfficeName(@Param("officeName") String officeName);
}
