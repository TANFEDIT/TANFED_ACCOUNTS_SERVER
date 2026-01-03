package com.tanfed.accounts.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataForIC {

	private Integer totalNoOfInvoices;
	private Double totalValueOfInvoices;
	private Integer totalNoOfInvoicesAcklgd;
	private Integer totalNoOfInvoicesrmng;
	private Integer NoOfAvlAckInvoices;
	
	private Set<String> ccbBranchLst;
	private Set<LocalDate> ackEntryDate;
	
	private List<IcTableData> tableData;
}
