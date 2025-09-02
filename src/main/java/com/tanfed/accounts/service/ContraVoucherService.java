package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.model.ContraEntry;
import com.tanfed.accounts.response.DataForContraEntry;

public interface ContraVoucherService {

	public DataForContraEntry getDataForContraEntry(String officeName, String contraType, LocalDate fromDate, LocalDate toDate, String pvNo, String cashAdjNo) throws Exception;
	
	public ResponseEntity<String> updateContraEntry(String fromNo, String toNo, String narration) throws Exception;
	
	public List<ContraEntry> getContraEntryData(String officeName) throws Exception;

	public ResponseEntity<String> rejectContraEntry(String fromNo, String toNo) throws Exception;
}
