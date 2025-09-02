package com.tanfed.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Office {

	private Long id;
	
	private String officeCode;
	
	private String officeType;
	
	private String officeName;
	
	private String code;
	
	private String activity;
}
