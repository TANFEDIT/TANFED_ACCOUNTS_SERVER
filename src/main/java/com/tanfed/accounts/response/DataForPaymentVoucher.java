package com.tanfed.accounts.response;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataForPaymentVoucher {

	private List<Long> accountNumList;
	private Set<String> beneficiaryNameList;
	private Set<String> accountTypeList;
	private Set<String> branchNameList;
	private Long beneficiaryAccountNo;
	private Double balance;
	private Boolean prevVoucherNotApproved;
	private String page;
}
