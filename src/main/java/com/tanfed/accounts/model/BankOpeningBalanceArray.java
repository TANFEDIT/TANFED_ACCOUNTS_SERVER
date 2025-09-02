package com.tanfed.accounts.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankOpeningBalanceArray {

	private LocalDate bankBalanceDate;
	private String bankName;
	private String branchName;
	private String accountType;
	private Long accountNumber;
	private Double passbookAmount;
	private Double dayBookAmount;
}
