package com.tanfed.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTermsAndConditions {
	
	private Long id;
	
	private PmodeAndValue incentivePaccs;
	
	private PmodeAndValue salesmanIncentive;
	
	private PmodeAndValue secretoryIncentive;
	
	private String selectedProductName;
	private Double qty; 
}
