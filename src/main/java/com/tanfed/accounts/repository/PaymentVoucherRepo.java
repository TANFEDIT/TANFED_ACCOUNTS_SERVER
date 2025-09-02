package com.tanfed.accounts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.PaymentVoucher;
@Repository
public interface PaymentVoucherRepo extends JpaRepository<PaymentVoucher, Long> {

	public Optional<PaymentVoucher> findByVoucherNo(String voucherNo);
	
	public List<PaymentVoucher> findByOfficeName(String officeName);
	
	@Query("SELECT e FROM PaymentVoucher e WHERE (e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified') AND e.officeName =:officeName")
	public List<PaymentVoucher> findPendingDataByOfficeName(@Param("officeName") String officeName);
	
	@Query("SELECT e FROM PaymentVoucher e WHERE e.voucherStatus = 'Approved' AND e.officeName =:officeName")
	public List<PaymentVoucher> findApprovedDataByOfficeName(@Param("officeName") String officeName);
	
}
