package com.tanfed.accounts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.SundryDrOb;

@Repository
public interface SundryDrObRepo extends JpaRepository<SundryDrOb, Long> {

	public List<SundryDrOb> findByOfficeName(String officeName);
	
	@Query("SELECT e FROM SundryDrOb e WHERE (e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified') AND e.officeName =:officeName")
	public List<SundryDrOb> findPendingDataByOfficeName(@Param("officeName") String officeName);
	
	@Query("SELECT e FROM SundryDrOb e WHERE e.voucherStatus = 'Approved' AND e.officeName =:officeName")
	public List<SundryDrOb> findApprovedDataByOfficeName(@Param("officeName") String officeName);
}
