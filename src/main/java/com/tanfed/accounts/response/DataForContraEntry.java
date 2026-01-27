package com.tanfed.accounts.response;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataForContraEntry {

	private Set<String> paymentAccountTypeList;
	private Set<String> receiptAccountTypeList;
	private List<String> officeNameList;
	private List<Long> paymentAccNoList;
	private String PaymentBranchName;
	private List<Long> receiptAccNoList;
	private String receiptBranchName;
	private Double balance;
}
