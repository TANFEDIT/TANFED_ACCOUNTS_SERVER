package com.tanfed.accounts.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataForBrs {

	public Set<String> accountTypeList;
	public Set<Long> accountNoList;
	public Set<String> branchNameList;
	public Double daybookBalance;
}
