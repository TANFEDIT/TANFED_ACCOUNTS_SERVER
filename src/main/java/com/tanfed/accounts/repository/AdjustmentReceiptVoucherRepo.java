package com.tanfed.accounts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
@Repository
public interface AdjustmentReceiptVoucherRepo extends JpaRepository<AdjustmentReceiptVoucher, Long> {

	public Optional<AdjustmentReceiptVoucher> findByVoucherNo(String voucherNo);
	
	public List<AdjustmentReceiptVoucher> findByOfficeName(String officeName);
	
	@Query("SELECT e FROM AdjustmentReceiptVoucher e WHERE (e.voucherStatus = 'Pending' OR e.voucherStatus = 'Verified') AND e.officeName =:officeName")
	public List<AdjustmentReceiptVoucher> findPendingDataByOfficeName(@Param("officeName") String officeName);
	
	@Query("SELECT e FROM AdjustmentReceiptVoucher e WHERE e.voucherStatus = 'Approved' AND e.officeName =:officeName")
	public List<AdjustmentReceiptVoucher> findApprovedDataByOfficeName(@Param("officeName") String officeName);

//	public AdjustmentReceiptVoucher findByContraId(String contraId);
	
	
}
