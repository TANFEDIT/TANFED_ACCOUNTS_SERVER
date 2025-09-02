package com.tanfed.accounts.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContraEntry {

	private String contraFor;
	private LocalDate date;
	private String narration;
	
	private String fromNo;
	private String fromAccType;
	private Long fromAccNo;
	private Double fromAmount;
	private String fromMainHead;
	private String fromSubHead;
	
	private String toNo;
	private String toAccType;
	private Long toAccNo;
	private Double toAmount;
	private String toMainHead;
	private String toSubHead;
}
