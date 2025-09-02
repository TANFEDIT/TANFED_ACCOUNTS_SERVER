package com.tanfed.accounts.entity;

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
@Table
@NoArgsConstructor
@AllArgsConstructor

public class SundryDrCrTable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String formType;
	
	private String month;
	private String subHead;
	private String officeName;
	
	private Double debit;
	private Double otherDebit;
	
	private Double credit;
	private Double otherCredit;
	
	private Double cb;
}
