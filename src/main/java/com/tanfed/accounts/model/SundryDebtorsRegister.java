package com.tanfed.accounts.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SundryDebtorsRegister {

	private LocalDate date;
	private String particular;
	private Double debit;
	private Double credit;
}
