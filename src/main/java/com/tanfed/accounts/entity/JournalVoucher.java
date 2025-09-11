package com.tanfed.accounts.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class JournalVoucher {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private List<String> empId;
	
	private String officeName;
	
	private String voucherStatus = "Pending";
	
	private List<String> designation;
	private LocalDate approvedDate;
	private LocalDate createdAt = LocalDate.now();
	
	private LocalDate jvDate;
	
	private String jvMonth;
	
	private String financialYear;

	private String jvNo;
	
	private String jvType;
	
	private String jvFor;
	private String idNo;
	private List<String> ifmsId;
	private String narration;
	private String supplierName;
	private Double debit;
	
	private Double totalDr;
	
	private Double totalCr;
	
	private Double derivedQty;
	
	private Double derivedTotal;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<JV_Array_Data> rows;
}
