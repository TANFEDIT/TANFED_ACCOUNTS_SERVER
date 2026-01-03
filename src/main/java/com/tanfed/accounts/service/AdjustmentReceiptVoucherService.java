package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;

public interface AdjustmentReceiptVoucherService {

	public ResponseEntity<String> saveAdjustmentReceiptVoucher(AdjustmentReceiptVoucher obj, String jwt) throws Exception;

	public ResponseEntity<String> editAdjustmentReceiptVoucher(AdjustmentReceiptVoucher obj, String jwt) throws Exception;
	
	public AdjustmentReceiptVoucher getVoucherByVoucherNo(String voucherNo) throws Exception;

//	public AdjustmentReceiptVoucher getAdjustmentReceiptVoucherByContraId(String contraId) throws Exception;
	
	public List<AdjustmentReceiptVoucher> getVoucherByOfficeName(String officeName) throws Exception;
	
	public List<AdjustmentReceiptVoucher> getVouchersForUpdate(String officeName, LocalDate fromDate, LocalDate toDate) throws Exception;
	
	public ResponseEntity<String> AdjustmentReceiptVoucherUpdate(List<AdjustmentReceiptVoucher> obj, String jwt) throws Exception;

	public void updateClosingBalance(AdjustmentReceiptVoucher obj) throws Exception;

}
