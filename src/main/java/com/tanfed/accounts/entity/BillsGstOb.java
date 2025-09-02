package com.tanfed.accounts.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table
public class BillsGstOb {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String officeName;
	
	private List<String> empId;
	
	private LocalDate createdAt = LocalDate.now();
	
	private LocalDate date;
	
	private String financialYear;
	
	private String gstReturnType;
	
	private String inputCredit;
	
	private String voucherStatus = "Pending";
	
	private LocalDate approvedDate;
	
	private List<String> designation;
}
