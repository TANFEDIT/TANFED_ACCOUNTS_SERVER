package com.tanfed.accounts.model;

import java.time.LocalDate;
import java.util.List;


import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductMaster {

	private Long id;
	
	private List<String> empId;
	private String activity;
	private String brandName;
	private String hsnCode;
	private String packing;
	private String productType;
	private String productCategory;
	private String productGroup;
	private String productName;
	private String productSupply;
	private String standardUnits;
	private String supplierName;
	private String supplierGst;
	private String usedAs;
	private String batchNo;
	private String certification;
	private LocalDate date = LocalDate.now();
	private String gstCategory;
	private Double gstRate;
	@Embedded
	private GstRateData gstData;
}
