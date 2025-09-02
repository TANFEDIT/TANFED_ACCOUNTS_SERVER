package com.tanfed.accounts.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.entity.DebitOrCreditNote;

public interface DebitCreditNoteService {

	public ResponseEntity<String> saveDebitCreditNote(DebitOrCreditNote obj, String jwt) throws Exception;
	
	public List<DebitOrCreditNote> findDrCrNoteByOfficeName(String officeName) throws Exception;
	
	public DebitOrCreditNote fetchDebitOrCreditNoteByVoucherNo(String drCrNo) throws Exception;
}
