package com.tanfed.accounts.model;


import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
private Long id;
	
	private LocalDate createdAt = LocalDate.now();
	
	private List<String> empId;
	  
	private String officeName;
	  
	private List<String> designation;
	  
	private String voucherStatus;
	
	private LocalDate approvedDate;
	
	
//	@Embedded
//	private FormDataInvoice formData;
	
//	@Embedded
//	private IncentiveDataInvoice calData;
	
	private String invoiceNo;

	private String activity;

	private Double b2cDiscount;

	private String block;

	private String buyerGstNo;

	private String collectionMode;
	private String collectionMethod;
	private Integer creditDays;

	private LocalDate date;
	
	private String dcNo;

	private String district;

	private String ifOthers;
	  
	private String ifmsId;
	
	
	private String firmType;
	  
	private String nameOfInstitution;
	
	private String licenseNo;
	
	private Double netInvoiceAdjustment;

	private String supplyMode;
	
	private String supplyTo;
	
	private String salesType;
	
	private String taluk;
	
	private String tcHt;
	
	private Double totalBasicValue;
	
	private Double totalCgstValue;
	
	private Double totalGstOnMargin;
	
	private Double netInvoicePrice;

	private Double totalIncentive;
	
	private Double totalInvoiceValue;
	
	private Double totalMarginValue;
	
	private Integer totalNoOfBags;
	
	private Double totalQty;
	
	private Double totalSgstValue;
	
	private String village;

	private String despatchAdviceNo;
	
	private String godownName;
	
	private String licenseNoGodown;

	private List<String> adjReceiptNo;

	private List<String> adjReceiptStatus;

	
	  
	
	  
	
	
	private List<TableDataInvoice> tableData;
	
	private List<InvoiceTermsAndConditions> tcData;
	
	
	
	
	private Boolean isShort;
	
	
	
	
	
	
	private String secondPointIfmsid;
	 
	private LocalDate updateDate;
	 
	private LocalDate ackDate;
	 
	private String ifmsStatus;
	
	
	
	
	
	  
	
	private Double ackQty;
	
	private List<Double> collectionValue;
	
	private LocalDate ackEntryDate;
	
	private LocalDate addedToPresentDate;
	
	private LocalDate dateOfPresent;
	
	private LocalDate dueDate;
	
	private String icmNo;
	
	private List<LocalDate> dateOfCollectionFromCcb;
	  
	private String voucherStatusICP1;
	
	private List<String> designationICP1;
	  
	private String voucherStatusICP2;
	  
	private List<String> designationICP2;
	
	private String voucherStatusICP3;
	
	private List<String> designationICP3;
	
	
	private List<String> designationICP4;
	  
	private String ccbBranch;
	
	private Boolean transferDone;
}
