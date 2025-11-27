package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.List;

import com.tanfed.accounts.model.CollectionRegisterTable;
import com.tanfed.accounts.response.CashChittaTable;
import com.tanfed.accounts.response.CashDayBookTable;
import com.tanfed.accounts.response.JournalRegisterTable;

public interface RegisterService {

	public List<CashChittaTable> fetchCashChittaData(String officeName, LocalDate fromDate, LocalDate toDate)
			throws Exception;

	public List<CashDayBookTable> fetchCashDayBookData(String officeName, String month) throws Exception;

	public List<CashDayBookTable> fetchBankDayBookData(String officeName, String jwt, String month) throws Exception;

	public List<JournalRegisterTable> fetchJournalRegisterData(String officeName, String month) throws Exception;

	public List<CashChittaTable> fetchSubsidyLedgerData(String officeName, String month, String subHead)
			throws Exception;

	public List<CashChittaTable> fetchGeneralLedgerData(String officeName, String month, String jwt) throws Exception;

	public List<CashChittaTable> fetchSundryDebtorsData(String officeName, String month, String subHead, String ifmsId,
			String firmType, String jwt) throws Exception;

	public List<CashChittaTable> fetchSundryCreditorsData(String officeName, String month, String subHead, String supplierName)
			throws Exception;

	public List<CashChittaTable> fetchSupplierAdvanceData(String month, String supplierName) throws Exception;

	public List<CollectionRegisterTable> fetchChequeCollectionData(String month, String officeName) throws Exception;
	
	public List<CollectionRegisterTable> fetchChequeIssueData(String month, String officeName) throws Exception;
}
