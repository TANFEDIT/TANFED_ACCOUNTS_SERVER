package com.tanfed.accounts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.CashReceiptVoucher;
@Repository
public interface CashReceiptRepo extends JpaRepository<CashReceiptVoucher, Long> {

	public Optional<CashReceiptVoucher> findByVoucherNo(String voucherNo);

	public CashReceiptVoucher findByContraId(String contraId);
	
	public List<CashReceiptVoucher> findByOfficeName(String officeName);
	
	@Query("SELECT e FROM CashReceiptVoucher e WHERE (e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified') AND e.officeName =:officeName")
	public List<CashReceiptVoucher> findPendingDataByOfficeName(@Param("officeName") String officeName);
	
	@Query("SELECT e FROM CashReceiptVoucher e WHERE e.voucherStatus = 'Approved' AND e.officeName =:officeName")
	public List<CashReceiptVoucher> findApprovedDataByOfficeName(@Param("officeName") String officeName);
}
