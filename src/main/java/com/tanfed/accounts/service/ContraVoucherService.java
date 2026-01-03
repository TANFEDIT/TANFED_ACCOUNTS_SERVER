package com.tanfed.accounts.service;

import java.time.LocalDate;

import com.tanfed.accounts.response.DataForContraEntry;

public interface ContraVoucherService {

	public DataForContraEntry getDataForContraEntry(String officeName, String contraBetween, LocalDate contraFromDate,
			LocalDate contraToDate) throws Exception;

//	public ResponseEntity<String> updateContraEntry(String fromNo, String toNo, String narration) throws Exception;

//	public List<ContraEntry> getContraEntryData(String officeName) throws Exception;

//	public ResponseEntity<String> rejectContraEntry(String fromNo, String toNo) throws Exception;
}
