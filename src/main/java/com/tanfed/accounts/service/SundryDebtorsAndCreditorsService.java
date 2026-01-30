package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.entity.ReconciliationEntry;
import com.tanfed.accounts.model.DataForIC;
import com.tanfed.accounts.model.IcmObject;
import com.tanfed.accounts.model.InvoiceCollectionObject;
import com.tanfed.accounts.model.VoucherApproval;
import com.tanfed.accounts.response.DataForSundryDebtor;

public interface SundryDebtorsAndCreditorsService {

	public DataForSundryDebtor getDataForSundryBills(String jwt, String ifmsId, String idNo, String officeName,
			String formType) throws Exception;

	public ResponseEntity<String> saveSundryDebtorsAndCreditors(ReconciliationEntry obj, String jwt) throws Exception;

	public List<ReconciliationEntry> fetchReconciliationEntriesByOfficeName(String officeName) throws Exception;

	public Double calculateSDrObValue(String month, String officeName, String buyerName);

	public Double calculateSCrObValue(String month, String officeName);

	public DataForIC fetchDataForIC(String officeName, String activity, String collectionProcess, String jwt,
			LocalDate fromDate, LocalDate toDate, String ccbBranch, LocalDate ackEntryDate, LocalDate dueDate,
			LocalDate addedToPresentDate, String icmNo) throws Exception;

	public ResponseEntity<String> updateICData(List<InvoiceCollectionObject> obj, String jwt) throws Exception;

	public ResponseEntity<String> saveAdjReceiptForIcmInvoices(IcmObject obj, String jwt, String type) throws Exception;

	public void updateFundTransfered(List<String> invoiceNoList) throws Exception;

	public void revertFundTransfered(List<String> invoiceNoList) throws Exception;

	public String updateAplStatusInvoiceCollection(VoucherApproval obj, String jwt) throws Exception;
}
