package com.tanfed.accounts.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.entity.CashReceiptVoucher;

public interface CashReceiptVoucherService {

	public ResponseEntity<String> saveCashReceiptVoucher(CashReceiptVoucher obj, String jwt) throws Exception;

	public ResponseEntity<String> editCashReceiptVoucher(CashReceiptVoucher obj, String jwt) throws Exception;
	
	public CashReceiptVoucher getCashReceiptVoucherByVoucherNo(String voucherNo) throws Exception;
	
//	public CashReceiptVoucher getCashReceiptVoucherByContraId(String contraId) throws Exception;
	
	public List<CashReceiptVoucher> getVouchersByOfficeName(String officeName) throws Exception;
	
	public void updateClosingBalance(CashReceiptVoucher obj) throws Exception;
}
