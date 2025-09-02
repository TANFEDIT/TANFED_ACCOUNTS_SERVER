package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.entity.JournalVoucher;
import com.tanfed.accounts.entity.SalesJvTable;
import com.tanfed.accounts.response.DataForSalesJv;

public interface JournalVoucherService {

	public ResponseEntity<String> saveJournalVoucher(JournalVoucher obj, String jwt) throws Exception;

	public ResponseEntity<String> editJournalVoucher(JournalVoucher obj, String jwt) throws Exception;
	
	public JournalVoucher getJvByJvNo(String jvNo) throws Exception;
	
	public List<JournalVoucher> getJvByOfficeName(String officeName) throws Exception;
	
	public List<String> getJvNoByOfficeName(String officeName) throws Exception;
	
	public List<JournalVoucher> getJvDataByFilter(String officeName, String month) throws Exception;
	
	public DataForSalesJv getDataForSalesJv(String officeName, String activity, String firmType, String productCategory, 
			LocalDate fromDate, LocalDate toDate, String jwt) throws Exception;

	public ResponseEntity<String> saveSalesJv(SalesJvTable obj, String jwt) throws Exception;

	public List<SalesJvTable> fetchSalesJvByOfficeName(String officeName) throws Exception;
	
	public void revertSupplierAdvanceJv(JournalVoucher obj, String jwt) throws Exception;
}
