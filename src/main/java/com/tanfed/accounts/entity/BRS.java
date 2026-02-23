package com.tanfed.accounts.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BRS {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private List<String> empId = new ArrayList<String>();

	private String officeName;

	private LocalDate createdAt = LocalDate.now();

	private List<String> designation = new ArrayList<String>();

	private String voucherStatus = "Pending";

	private LocalDate approvedDate;

	private LocalDate reconciliationDate;

	private String accountType;

	private Long accountNo;

	private String branchName;

	private Double passbookBalance;

	private Double daybookBalance;

	private Double daybookBalTotal;

	private Double passbookBalTotal;
	private Double afterDaybookBal;
	private Double afterPassbookBal;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "brs")
	private List<BrsParticulars> daybookTranscations;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "brs")
	private List<BrsParticulars> passbookTranscations;

}
