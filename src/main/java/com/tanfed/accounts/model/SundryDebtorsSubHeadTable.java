package com.tanfed.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SundryDebtorsSubHeadTable {

	private String subHead;
	private Double openingBalance;
	private Double debit;
	private Double otherDebit;
	private Double total;
	private Double credit;
	private Double otherCredit;
	private Double closingBalance;
}
