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
public class OpeningBalance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String officeName;
	
	@Column
	private List<String> empId;
	
	@Column
	private List<String> designation;
	
	private String voucherStatus = "Pending";
	
	private LocalDate approvedDate;
	
	
	private String openingBalanceFor;

	private LocalDate opDate;
	
	
	
	
	private LocalDate bankBalanceDate;
	
	private String bankName;
	
	private String branchName;
	
	
	private String accountType;
	
	private Long accountNumber;

	private Double passbookAmount;

	private Double dayBookAmount;

	
	
	
	
	private LocalDate cashBalanceDate;

	private Double amount;
	
	private Integer fiveHundred;
	
	private Integer twoHundred;
	
	private Integer oneHundred;
	
	private Integer fifty;
	
	private Integer twenty;
	
	private Integer ten;
	
	private Integer coins;
	
	
	
	private LocalDate createdAt = LocalDate.now();
	
}
