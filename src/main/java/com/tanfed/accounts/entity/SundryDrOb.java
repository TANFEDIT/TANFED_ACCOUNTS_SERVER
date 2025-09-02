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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class SundryDrOb {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String activity;
	private String ifmsId;
	private String nameOfInstitution;
	private String district;
	private String invoiceNo;
	private LocalDate invoiceDate;
	private String status;
	private Double qty;
	private Double amount;
	
	private String officeName;
	private List<String> empId;
	private String voucherStatus;
	private List<String> designation;
	private LocalDate createdAt = LocalDate.now();
	private LocalDate approvedDate;
	private String mainHead;
	private String subHead;
}
