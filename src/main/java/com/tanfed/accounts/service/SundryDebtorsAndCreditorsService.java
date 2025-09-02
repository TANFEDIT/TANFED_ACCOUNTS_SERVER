package com.tanfed.accounts.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
import com.tanfed.accounts.entity.JournalVoucher;
import com.tanfed.accounts.entity.PaymentVoucher;
import com.tanfed.accounts.entity.ReconciliationEntry;
import com.tanfed.accounts.response.DataForSundryDebtor;

public interface SundryDebtorsAndCreditorsService {

	public DataForSundryDebtor getDataForSundryBills(String jwt, String ifmsId, String idNo, String officeName, String month,
			String formType) throws Exception;
	
	public ResponseEntity<String> saveSundryDebtorsAndCreditors(ReconciliationEntry obj, String jwt) throws Exception;

	public List<ReconciliationEntry> fetchReconciliationEntriesByOfficeName(String officeName) throws Exception;
	
	public void updateSdrJV(JournalVoucher jv, String jwt, String drCr) throws Exception;

	public void updateSdrAdjReceipt(AdjustmentReceiptVoucher arv, String jwt) throws Exception;

	public void updateSdrReconEntry(ReconciliationEntry reconEntry, String jwt) throws Exception;

	public void updateScrPv(PaymentVoucher pv, String jwt) throws Exception;

	public Double calculateSDrObValue(String month, String subHead, String officeName);

	public Double calculateSCrObValue(String month, String subHead, String officeName);

//	public ResponseEntity<String> updateIfmsIdAccVouchers(String jwt, String idNo, String ifmsId) throws Exception;
//	
//	public void updateJvSundryDebtors(JournalVoucher obj, String jwt) throws Exception;
//
//	public void updateAdjReceiptSundryDebtors(AdjustmentReceiptVoucher obj) throws Exception;
//	
//	public List<SundryDebtorsSubHeadTable> mapSdrSubHeadTableData(String jwt, String officeName, String month) throws Exception;
//
//	public void updateJvSundryCreditors(JournalVoucher obj, String jwt) throws Exception;
//
//	public void updatePvSundryCreditors(PaymentVoucher obj) throws Exception;
//
//	public List<SundryDebtorsSubHeadTable> mapSCrSubHeadTableData(String jwt, String officeName, String month)
//			throws Exception;
}
