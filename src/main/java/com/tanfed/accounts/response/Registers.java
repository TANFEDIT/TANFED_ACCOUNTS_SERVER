package com.tanfed.accounts.response;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Registers {

	private List<CashChittaTable> cashChitta;
	private List<CashChittaTable> sundryDebitorsRegister;
	private List<CashChittaTable> sundryCreditorsRegister;
	private List<CashChittaTable> supplierAdvanceRegister;
	private List<CashDayBookTable> cashDayBook;
	private List<CashDayBookTable> bankDayBook;
	private List<Long> accountNo;
	private List<JournalRegisterTable> journalRegister;
	private List<CashChittaTable> subsidyLedger;
	private List<CashChittaTable> generalLedger;
	private Double ob;
	private Double obSa;
	private Double obCa;
	private Double obNpa;
	private List<String> subHeadList;
	private Set<String> districtList;
	private List<String> ifmsIdList;
	private List<String> nameList;
}
