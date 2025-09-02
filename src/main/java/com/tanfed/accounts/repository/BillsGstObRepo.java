package com.tanfed.accounts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.BillsGstOb;
@Repository
public interface BillsGstObRepo extends JpaRepository<BillsGstOb, Long> {

	public List<BillsGstOb> findByOfficeName(String officeName);
	
	@Query("SELECT e FROM BillsGstOb e WHERE (e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified') AND e.officeName =:officeName")
	public List<BillsGstOb> findPendingDataByOfficeName(@Param("officeName") String officeName);
	
	@Query("SELECT e FROM BillsGstOb e WHERE e.voucherStatus = 'Approved' AND e.officeName =:officeName")
	public List<BillsGstOb> findApprovedDataByOfficeName(@Param("officeName") String officeName);
}
