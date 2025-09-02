package com.tanfed.accounts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.OpeningBalance;
@Repository
public interface OpeningBalanceRepo extends JpaRepository<OpeningBalance, Long> {

	public List<OpeningBalance> findByOfficeName(String officeName);
	
	public List<OpeningBalance> findByOfficeNameAndOpeningBalanceFor(String officeName, String openingBalanceFor);
	
	@Query("SELECT e FROM OpeningBalance e WHERE (e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified') AND e.officeName =:officeName")
	public List<OpeningBalance> findPendingDataByOfficeName(@Param("officeName") String officeName);
	
	@Query("SELECT e FROM OpeningBalance e WHERE e.voucherStatus = 'Approved' AND e.officeName =:officeName")
	public List<OpeningBalance> findApprovedDataByOfficeName(@Param("officeName") String officeName);
}
