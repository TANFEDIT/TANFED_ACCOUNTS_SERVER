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
public class DebitOrCreditNote {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDate createdAt = LocalDate.now();
	
	private List<String> empId;
	
	private String officeName;
	
	private LocalDate approvedDate;
	
	private String voucherStatus = "Pending";

	private List<String> designation;
	
	private String drCrNo;
	
	private String activity;
	
	private LocalDate date;
	
	private Double drCrNoteValue;
	
	private String gst;
	
	private String ifmsId;
	
	private LocalDate invoiceDate;
	
	private String invoiceNo;
	
	private Double invoiceValue;
	
	private String name;
	
	private String noteFor;
	
	private String remarks;
	
	private String mainHead;
	
	private String subHead;
	
}
