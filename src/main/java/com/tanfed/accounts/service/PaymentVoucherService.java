package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.entity.PaymentVoucher;
import com.tanfed.accounts.response.DataForPaymentVoucher;

public interface PaymentVoucherService {

	public ResponseEntity<String> savePaymentVoucher(PaymentVoucher obj, String jwt) throws Exception;

	public ResponseEntity<String> editPaymentVoucher(PaymentVoucher obj, String jwt) throws Exception;
	
	public PaymentVoucher getVoucherByVoucherNo(String voucherNo) throws Exception;
	
	public List<PaymentVoucher> getVoucherByOfficeName(String officeName) throws Exception;
	
	public DataForPaymentVoucher getDataForPaymentVoucher(String officeName, String accountType, String accountNo,
			String jwt, String mainHead, String paidTo, LocalDate date, String pvType) throws Exception;
	
	
	
	public List<PaymentVoucher> getVouchersForCashUpdate(String officeName, LocalDate fromDate, LocalDate toDate) throws Exception;
	
	public ResponseEntity<String> paymentVoucherCashUpdate(List<PaymentVoucher> obj, String jwt) throws Exception;
	
	

	public List<PaymentVoucher> getVouchersForOnlineUpdate(String officeName, LocalDate fromDate, LocalDate toDate) throws Exception;
	
	public ResponseEntity<String> paymentVoucherOnlineUpdate(List<PaymentVoucher> obj, String jwt) throws Exception;
	
	
	
	public List<PaymentVoucher> getVouchersForChequeUpdate(String officeName, LocalDate fromDate, LocalDate toDate) throws Exception;
	
	public ResponseEntity<String> paymentVoucherChequeUpdate(List<PaymentVoucher> obj, String jwt) throws Exception;
	
	public void updateClosingBalance(PaymentVoucher obj) throws Exception;
	
	public void revertSupplierAdvancePv(PaymentVoucher obj, String jwt) throws Exception;

	public void updateVoucherStatusForContra(PaymentVoucher pv, String jwt) throws Exception;
}
