package com.tanfed.accounts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.SupplierAdvance;
@Repository
public interface SupplierAdvanceRepo extends JpaRepository<SupplierAdvance, Long>{

	public Optional<SupplierAdvance> findBySupplierAdvanceNo(String supplierAdvanceNo);
	
	public List<SupplierAdvance> findByProductName(String productName);
	
	@Query("SELECT e FROM SupplierAdvance e WHERE e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified'")
	public List<SupplierAdvance> findPendingData();
	
	@Query("SELECT e FROM SupplierAdvance e WHERE e.voucherStatus = 'Approved'")
	public List<SupplierAdvance> findApprovedData();
}
