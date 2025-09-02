package com.tanfed.accounts.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
public class AdjustmentReceiptVoucher{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
		
	private String voucherNo;
	
	private String pvVoucherNo;

	private String officeName;
	
	@Column
	private List<String> empId;
	
	private String voucherStatus;
	
	@Column
	private List<String> designation;
	private LocalDate approvedDate;
	
	
	private LocalDate date;
	
	private String receivedFrom;
	private String ifmsIdNo;
	private String icmInvNo;
	private String voucherFor;
	private Double receivedAmount;
	
	private String receiptMode;
	
	private Long utrChequeNoDdNo;
	
	private LocalDate docDate;
	
	private String issuingBank;
	
	private String mainHead;
	
	private String subHead;
	
	private String narration;
	private String contraNarration;
	private String contraEntry;
	private String contraId;
	private LocalDate createdAt = LocalDate.now();
	
	
	
	private String accountType;
	
	private Long accountNo;
	
	private String branchName;
	
	private LocalDate depositDate;
	
	
	
	
	private Double bankCharges;
	
	private LocalDate dateOfCollection;
	
	
	
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "popup_value_id", referencedColumnName = "id")
	@JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
	private VoucherPopUp voucherBreakUp;
}
