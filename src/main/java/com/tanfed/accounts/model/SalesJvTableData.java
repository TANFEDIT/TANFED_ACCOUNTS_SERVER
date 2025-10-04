package com.tanfed.accounts.model;


import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tanfed.accounts.entity.SalesJvTable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class SalesJvTableData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String invoiceNo;
	private LocalDate date;
	private String nameOfInstitution;
	private String ifmsId;
	private String district;
	private Double qty;
	private Double netInvoiceValue;
	private Double basicValue;
	private Double cgst;
	private Double sgst;
	private Double issueMargin;
	
	@ManyToOne
	@JoinColumn(name = "s_jv")
	@JsonIgnore
	private SalesJvTable s_jv;
}
