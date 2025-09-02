package com.tanfed.accounts.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TaxInfo {

	private Long id;
	
	private String gstNo;
	
	private String panNo;
	
	private String tinNo;
	
	private String TanNo;
	
	private String gstCategory;
	
	private Double gstRate;
	
	private Double cgstRate;
	
	private Double sgstRate;
	
	private Double igstRate;

	private Double rcmRate;
	
	private LocalDate date = LocalDate.now();
	
	private List<String> empId; 
}
