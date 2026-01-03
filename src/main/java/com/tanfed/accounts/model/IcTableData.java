package com.tanfed.accounts.model;

import java.time.LocalDate;

import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IcTableData {

	private String invoiceNo;
	private LocalDate date;
	private String ifmsId;
	private String nameOfInstitution;
	private String district;
	private Double amount;
	private Double qty;
	private String ccbBranch;
	private LocalDate dueDate;
	
	private AdjustmentReceiptVoucher adjData;

}
