package com.tanfed.accounts.model;

import java.util.List;

import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IcmObject {

	private AdjustmentReceiptVoucher adjData;
	private List<InvoiceCollectionObject> invoices;
}
