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

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ReconciliationEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private List<String> empId;
	private LocalDate createdAt = LocalDate.now();
	private String voucherStatus = "Pending";
	private LocalDate approvedDate;
	private List<String> designation;
	
	private String formType;
	private String officeName;
	
	private String idNo;
	private String ifmsId;
	
	private LocalDate date;
	private Double amount;
	private String mainHead;
	private String subHead;
	private String remarks;
	private String gstNo;
	private String nameOfInstitution;
	private String district;
	private String taluk;
	private String block;
	private String village;
	private String address;
}
