package com.tanfed.accounts.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionRegisterTable {

	public String voucherNo;
	public LocalDate date;
	private String mainHead;
	private String subHead;
	
	
	private Double amount;

	private String receivedFrom;
	private String paidTo;
	
	private String chequeNo;
	public LocalDate chequeDate;
	private String issueBank;

	private String accType;
	private String branchName;
	private Long accNo;
	public LocalDate depositDate;
	public LocalDate dateOfCollection;
	private Double bankCharges;
}
