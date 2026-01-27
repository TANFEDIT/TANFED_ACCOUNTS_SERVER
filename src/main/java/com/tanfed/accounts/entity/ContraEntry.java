package com.tanfed.accounts.entity;

import java.time.LocalDate;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class ContraEntry {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDate createdAt = LocalDate.now();
	private String contraId;
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
}
