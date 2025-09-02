package com.tanfed.accounts.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class CashReceiptVoucher {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true,nullable =false)
	private String voucherNo;
	
	@Column
	private List<String> empId;
	
	private String officeName;
	private String ifmsIdNo;
	private String mainHead;
	
	private String subHead;
	
	private String remarks;
	private String contraNarration;
	
	private String receivedFrom;
	
	private Double receivedAmount;
	
	private LocalDate date;
	
	private LocalDate createdAt = LocalDate.now();
	private String contraEntry;
	private String contraId;
	private String voucherStatus = "Pending";
	private LocalDate approvedDate;
	@Column
	private List<String> designation;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "popup_value_id", referencedColumnName = "id")
	@JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
	private VoucherPopUp voucherBreakUp;
}
