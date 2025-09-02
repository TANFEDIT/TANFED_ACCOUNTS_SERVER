package com.tanfed.accounts.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.tanfed.accounts.entity.BillsGstOb;
import com.tanfed.accounts.entity.OpeningBalance;
import com.tanfed.accounts.entity.SundryCrOb;
import com.tanfed.accounts.entity.SundryDrOb;
import com.tanfed.accounts.model.OpeningBalanceDto;

public interface OpeningBalanceService {

	public ResponseEntity<String> saveOpeningBalance(OpeningBalanceDto obj, String jwt) throws Exception;

	public ResponseEntity<String> saveBillsGstOpeningBalance(BillsGstOb obj, String jwt) throws Exception;
	
	public ResponseEntity<String> saveSundryCrOb(List<SundryCrOb> obj, String jwt) throws Exception;
	
	public ResponseEntity<String> editOpeningBalance(OpeningBalance obj) throws Exception;
	
	public List<OpeningBalance> getOpeningBalancesByOfficeName(String officeName) throws Exception;

	public ResponseEntity<String> saveSundryDrOb(List<SundryDrOb> obj, String jwt) throws Exception;

	public String accObValidate(String officeName) throws Exception;
	
	public String billsAccObValidate(String officeName) throws Exception;

	public void updateClosingBalance(OpeningBalance obj) throws Exception;
	
}
