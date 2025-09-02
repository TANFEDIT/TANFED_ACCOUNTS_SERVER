package com.tanfed.accounts.response;

import java.time.LocalDate;
import java.util.List;

import com.tanfed.accounts.model.SundryDebtorsSubHeadTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DataForSundryDebtor {

	private LocalDate date;
	private Double amount;
	private String gstNo;
	private String nameOfInstitution;
	private String district;
	private String taluk;
	private String block;
	private String village;
	private String address;
	private String ifmsId;
	private String mainHead;
	private String subHead;
	private String remarks;

	private List<SundryDebtorsSubHeadTable> tableData;
}
