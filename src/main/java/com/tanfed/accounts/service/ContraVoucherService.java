package com.tanfed.accounts.service;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.dto.ContraEntryDto;
import com.tanfed.accounts.entity.ContraEntry;
import com.tanfed.accounts.entity.PaymentVoucher;
import com.tanfed.accounts.response.DataForContraEntry;

public interface ContraVoucherService {

	public DataForContraEntry getDataForContraEntry(String officeName, String jwt, String paymentAccType, String pvType,
			String contraBetween, String receiptAccType, LocalDate date, String paymentAccountNo,
			String receiptAccountNo, String paidTo) throws Exception;

	public ResponseEntity<String> saveContraEntry(ContraEntryDto obj, String jwt) throws Exception;


	public ContraEntry getContraById(String contraId) throws Exception;
//	public ResponseEntity<String> updateContraEntry(String fromNo, String toNo, String narration) throws Exception;

	public void updateVoucherStatusForContra(PaymentVoucher pv, String jwt) throws Exception;

//	public List<ContraEntry> getContraEntryData(String officeName) throws Exception;

//	public ResponseEntity<String> rejectContraEntry(String fromNo, String toNo) throws Exception;
}
