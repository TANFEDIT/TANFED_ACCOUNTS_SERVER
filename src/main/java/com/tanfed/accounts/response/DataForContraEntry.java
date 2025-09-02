package com.tanfed.accounts.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataForContraEntry {

	private List<String> fromNoList;
	private List<String> toNoList;
	
	private Double receiptAmount;
	private Double paymentAmount;
	
	private String receiptAccType;
	private String paymentAccType;
	
	private Long receiptAccNo;
	private Long paymentAccNo;
}
