package com.tanfed.accounts.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tanfed.accounts.dto.DataForBrs;
import com.tanfed.accounts.entity.BRS;
import com.tanfed.accounts.service.BrsService;

@RestController
@RequestMapping("/api/accounts/brs")
public class BrsHandler {

	@Autowired
	private BrsService brsService;

	@PostMapping("/savebrs")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ACCADMIN', 'ROLE_ACCUSER', 'ROLE_ROADMIN', 'ROLE_ROUSER')")
	public ResponseEntity<String> saveBrsEntityHandler(@RequestBody BRS obj, @RequestHeader("Authorization") String jwt)
			throws Exception {
		return brsService.saveBrsEntity(obj, jwt);
	}

	@GetMapping("/getdataforbrs")
	public DataForBrs fetchDataForBrsHandler(@RequestParam String officeName, @RequestParam String accountType,
			@RequestParam LocalDate reconciliationDate, @RequestParam String accountNo, @RequestParam String branchName,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return brsService.fetchDataForBrs(officeName, accountType, accountNo, branchName, jwt, reconciliationDate);
	}
}
