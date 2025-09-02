package com.tanfed.accounts.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpeningBalanceDto {

	private Long id;
	private LocalDate opDate;
	private String openingBalanceFor;
	private String officeName;
	
	private List<BankOpeningBalanceArray> bankData;
	
	private LocalDate cashBalanceDate;
	private Double amount;
	private Integer fiveHundred;
	private Integer twoHundred;
	private Integer oneHundred;
	private Integer fifty;
	private Integer twenty;
	private Integer ten;
	private Integer coins;
}
