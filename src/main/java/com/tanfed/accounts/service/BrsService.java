package com.tanfed.accounts.service;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.dto.DataForBrs;
import com.tanfed.accounts.entity.BRS;

public interface BrsService {

	public ResponseEntity<String> saveBrsEntity(BRS obj, String jwt) throws Exception;

	public DataForBrs fetchDataForBrs(String officeName, String accountType, String accountNo, String branchName,
			String jwt, LocalDate reconciliationDate) throws Exception;

}
