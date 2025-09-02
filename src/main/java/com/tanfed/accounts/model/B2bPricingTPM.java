package com.tanfed.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class B2bPricingTPM {
	
	private Double b2bBasicPrice;
	
	private Double b2bCgst;
	
	private Double b2bSgst;
	
	private Double b2bMrp;
	
	private Double b2bNetTotal;
	
	private Double marginToPaccs;
	
	private Double paccsMarginGst;
}
