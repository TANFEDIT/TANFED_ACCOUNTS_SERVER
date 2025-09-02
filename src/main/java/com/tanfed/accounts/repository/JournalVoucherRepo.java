package com.tanfed.accounts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.JournalVoucher;
@Repository
public interface JournalVoucherRepo extends JpaRepository<JournalVoucher, Long> {

	public Optional<JournalVoucher> findByJvNo(String JvNo);
	
	public List<JournalVoucher> findByOfficeName(String officeName);

	public List<JournalVoucher> findByJvMonth(String jvMonth);
	
	@Query("SELECT e FROM JournalVoucher e WHERE (e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified') AND e.officeName =:officeName")
	public List<JournalVoucher> findPendingDataByOfficeName(@Param("officeName") String officeName);
	
	@Query("SELECT e FROM JournalVoucher e WHERE e.voucherStatus = 'Approved' AND e.officeName =:officeName")
	public List<JournalVoucher> findApprovedDataByOfficeName(@Param("officeName") String officeName);
}
