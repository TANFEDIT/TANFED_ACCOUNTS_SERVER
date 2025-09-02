package com.tanfed.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QtyRebate {

	private Long id;
	private Double fromQty;
	private Double toQty;
	private Double amount;
}
