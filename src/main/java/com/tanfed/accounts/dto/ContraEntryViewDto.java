package com.tanfed.accounts.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContraEntryViewDto {

	private Long id;
	private String contraBetween;
	private String toRegion;
	
	private LocalDate date;
	
	private String paymentNo;
	private String paymentMainHead;
	private String paymentSubHead;
	private Double paymentAmount;
	
	private String receiptNo;
	private String receiptMainHead;
	private String receiptSubHead;
	private Double receiptAmount;
	
	private String voucherStatus;
}
