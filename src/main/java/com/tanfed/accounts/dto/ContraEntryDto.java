package com.tanfed.accounts.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContraEntryDto {

	private LocalDate date;
	private String contraBetween;
	private String officeName;

	private Double amount;
	private String mainHead;
	
	private String pvType;
	private String paidTo;
	private String paymentAccType;
	private Long paymentAccountNo;
	private String paymentBranchName;
	private String paymentRemarks;
	private String paymentSubHead;
	
	private String receivedFrom;
	private String receiptAccType;
	private Long receiptAccountNo;
	private String receiptBranchName;
	private String receiptRemarks;
	private String receiptSubHead;
	private String receiptMode;
	private Long utrChequeNoDdNo;
	private LocalDate docDate;
	private String issuingBank;
	
	private String subHead;
}
