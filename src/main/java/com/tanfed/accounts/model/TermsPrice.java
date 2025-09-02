package com.tanfed.accounts.model;

import java.time.LocalDate;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsPrice {

	private Long id;
	
	private LocalDate createdAt = LocalDate.now();
	
	private LocalDate approvedDate;
	private LocalDate extentionDate;
	private List<String> empId;
	
	private String termsNo;
	
	private String circularNo;
	
	private String voucherStatus = "Pending";
	
	private List<String> designation;
	
	private MasterDataTPM masterData;
	
	private PurchaseTermsConditionsTPM purchaseTermsAndCondition;
	
	private PurchaseTermsPricingTPM purchaseTermsPricing;
	
	private B2bTermsConditionsTPM b2bTermsAndConditions;
	
	private B2bPricingTPM b2bPrice;
	
	private B2cPricingTPM b2cPrice;
	
	private List<QtyRebate> inputs;
	
	private List<TermsData> purchaseDataDirect;
	
	private List<TermsData> purchaseDataBuffer;
	
	private List<TermsData> purchaseDataGeneral;
}
