package com.tanfed.accounts.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountsMaster {
	
	private Long id;
	private String AccGroup;
	private String mainHead;
	private String subHead;
	private String groupCode;
	private String mainHeadCode;
	private String subHeadCode;
	private String associatedWith;
	private LocalDate date = LocalDate.now();
	private List<String> empId; 
}
