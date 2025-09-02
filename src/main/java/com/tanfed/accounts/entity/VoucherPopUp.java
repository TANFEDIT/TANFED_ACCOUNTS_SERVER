package com.tanfed.accounts.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class VoucherPopUp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String voucherRelatedTo;
	private Double gstAmount;
	private Double tdsAmount;
	private Double tcsAmount;

}
