package com.tanfed.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherApproval {
	private String voucherStatus;
	private String id;
	private String formType;
	private String adjNo;
}
