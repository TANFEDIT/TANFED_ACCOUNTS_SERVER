package com.tanfed.accounts.response;

import java.time.LocalDate;
import java.util.List;

import com.tanfed.accounts.entity.JV_Array_Data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalRegisterTable {

	private String voucherNo;
	private LocalDate date;
	private List<JV_Array_Data> tableData;
	private Double debitAmount;
	private Double creditAmount;
	private String remarks;
}
