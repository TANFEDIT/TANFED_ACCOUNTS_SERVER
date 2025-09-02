package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.entity.JournalVoucher;
import com.tanfed.accounts.entity.PaymentVoucher;
import com.tanfed.accounts.entity.SupplierAdvance;
import com.tanfed.accounts.model.JvAndPvObj;
import com.tanfed.accounts.response.DataForSupplierAdvance;

public interface SupplierAdvanceService {

	public ResponseEntity<String> saveSupplierAdvance(SupplierAdvance obj, String jwt) throws Exception;
	
	public DataForSupplierAdvance getDataForSupplierAdvance(String officeName, String activity, String supplierName,
			String productName, String termsMonth, String termsNo, String advanceMonth, String jwt, LocalDate date) throws Exception;

	public ResponseEntity<String> updatePvAndJv(JvAndPvObj obj, String supplierAdvanceNo, String jwt) throws Exception;

	public List<SupplierAdvance> fetchOutstandingAdvancesByProduct(String productName) throws Exception;
	
	public void revertPvAndJv(SupplierAdvance obj, String jwt, PaymentVoucher pv, JournalVoucher jv) throws Exception;

	public void updateAvlQtyAndAmount(String supplierAdvanceNo, double qty, double amount) throws Exception;
	
}
