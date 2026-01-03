package com.tanfed.accounts.response;

import java.util.List;

import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
import com.tanfed.accounts.entity.CashReceiptVoucher;
import com.tanfed.accounts.entity.PaymentVoucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataForContraEntry {

	private List<PaymentVoucher> paymentList;
	private List<AdjustmentReceiptVoucher> adjReceiptList;
	private List<CashReceiptVoucher> cashReceiptList;
	
	private Double receiptAmount;
	private Double paymentAmount;
	
	private String receiptAccType;
	private String paymentAccType;
	
	private Long receiptAccNo;
	private Long paymentAccNo;
}
