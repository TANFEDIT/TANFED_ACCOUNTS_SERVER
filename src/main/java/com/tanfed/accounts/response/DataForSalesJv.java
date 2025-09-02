package com.tanfed.accounts.response;

import java.util.List;
import java.util.Set;

import com.tanfed.accounts.model.SalesJvTableData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataForSalesJv {

	private Set<String> productCategoryList;
	private List<SalesJvTableData> tableData;
	private Double totalQty;
	private Double totalNet;
	private Double totalMargin;
	private Double totalGst;
}
