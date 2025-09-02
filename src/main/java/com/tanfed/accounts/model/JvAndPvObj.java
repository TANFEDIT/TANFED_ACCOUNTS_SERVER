package com.tanfed.accounts.model;

import com.tanfed.accounts.entity.JournalVoucher;
import com.tanfed.accounts.entity.PaymentVoucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JvAndPvObj {

	private JournalVoucher jv;
	private PaymentVoucher pv;
}
