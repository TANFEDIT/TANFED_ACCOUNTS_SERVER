package com.tanfed.accounts.entity;

import java.time.LocalDate;
import java.util.List;

import com.tanfed.accounts.model.SalesJvTableData;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class SalesJvTable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "s_jv")
	private List<JournalVoucher> jvList;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "s_jv")
	private List<SalesJvTableData> tableData;
	
	private String activity;
	private String firmType;
	private String productCategory;
	private List<String> empId;
	private LocalDate createdAt = LocalDate.now();
	private String officeName;
}
