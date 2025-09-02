package com.tanfed.accounts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.SundryCrOb;
@Repository
public interface SundryCrObRepo extends JpaRepository<SundryCrOb, Long> {

	public List<SundryCrOb> findByOfficeName(String officeName);
	
	@Query("SELECT e FROM SundryCrOb e WHERE (e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified') AND e.officeName =:officeName")
	public List<SundryCrOb> findPendingDataByOfficeName(@Param("officeName") String officeName);
	
	@Query("SELECT e FROM SundryCrOb e WHERE e.voucherStatus = 'Approved' AND e.officeName =:officeName")
	public List<SundryCrOb> findApprovedDataByOfficeName(@Param("officeName") String officeName);
}
