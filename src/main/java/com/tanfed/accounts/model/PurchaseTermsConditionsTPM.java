package com.tanfed.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseTermsConditionsTPM {
	
	private String purchaseCreditDays;
	
	private String purchaseModeofSupply;
	
	private String purchasePaymentMode;
	
	private String purchaseTermApplicableTo;
	
	private String rebateReceivableMode;
	
	private String incentiveToB2b;
	
	private String incentiveToB2c;
	
	private String incentiveToFirm;
	
	private String incentiveToTanfed;
}
