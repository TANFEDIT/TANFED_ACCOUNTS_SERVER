package com.tanfed.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class B2cPricingTPM {

	
	private Double b2cBasicPrice;
	
	private Double b2cCgst;
	
	private Double b2cSgst;
	
	private Double b2cMrp;
	
	private Double b2cNetTotal;
	
	private Double b2cDiscount;
}
