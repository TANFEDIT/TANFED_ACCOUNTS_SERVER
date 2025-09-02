package com.tanfed.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GstRateData {
	
	private Double cgstRate;
	private Double sgstRate;
	private Double igstRate;
	private Double rcmRate;
}
