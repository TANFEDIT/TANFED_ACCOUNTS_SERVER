package com.tanfed.accounts.response;

import java.util.List;
import java.util.Set;

import com.tanfed.accounts.entity.SupplierAdvance;
import com.tanfed.accounts.model.PurchaseTermsPricingTPM;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataForSupplierAdvance {

	private Set<String> supplierNameList;
	private List<String> productNameList;
	private String productCategory;
	private String productGroup;
	private String supplierGst;
	private String hsnCode;
	private String packing;
	private String standardUnits;
	private List<String> termsMonthList;
	private List<String> termsNoList;
	private PurchaseTermsPricingTPM purchasePricing;
	private Long accountsNo;
	private List<String> advanceMonthList;
	private List<SupplierAdvance> sagData;
}
