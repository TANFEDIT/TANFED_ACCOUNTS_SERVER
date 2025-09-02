package com.tanfed.accounts.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashDayBookTable {
	
	private String voucherNo;
	private LocalDate date;
	private String mainHead;
	private String subHead;
	private String remarks;
	private Double receivedAmount;
	private Double paidAmount;
	private String contra;
	private String accType;
}
