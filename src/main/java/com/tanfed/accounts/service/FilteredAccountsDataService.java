package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.entity.*;
import com.tanfed.accounts.model.AccountsMaster;
import com.tanfed.accounts.model.BeneficiaryMaster;
import com.tanfed.accounts.model.ContraEntry;
import com.tanfed.accounts.model.TaxInfo;
import com.tanfed.accounts.model.Vouchers;
import com.tanfed.accounts.repository.*;

@Service
public class FilteredAccountsDataService {

	@Autowired
	private OpeningBalanceService openingBalanceService;

	@Autowired
	private OpeningBalanceRepo openingBalanceRepo;

	@Autowired
	private CashReceiptVoucherService cashReceiptVoucherService;

	@Autowired
	private CashReceiptRepo cashReceiptRepo;

	@Autowired
	private AdjustmentReceiptVoucherService adjustmentReceiptVoucherService;

	@Autowired
	private AdjustmentReceiptVoucherRepo adjustmentReceiptVoucherRepo;

	@Autowired
	private PaymentVoucherService paymentVoucherService;

	@Autowired
	private PaymentVoucherRepo paymentVoucherRepo;

	@Autowired
	private ContraVoucherService contraVoucherService;

	@Autowired
	private JournalVoucherService journalVoucherService;

	@Autowired
	private JournalVoucherRepo journalVoucherRepo;

	@Autowired
	private SupplierAdvanceService supplierAdvanceService;

	@Autowired
	private SupplierAdvanceRepo supplierAdvanceRepo;

	@Autowired
	private MasterService masterService;

	@Autowired
	private DebitOrCreditNoteRepo debitOrCreditNoteRepo;

	@Autowired
	private DebitCreditNoteService debitCreditNoteService;

	private static Logger logger = LoggerFactory.getLogger(FilteredAccountsDataService.class);

	public Vouchers getFilteredData(String formType, LocalDate fromDate, LocalDate toDate, String officeName,
			String voucherStatus, String voucherNo, String jwt) throws Exception {
		Vouchers data = new Vouchers();

		switch (formType) {
		case "accountsMaster": {
			List<AccountsMaster> accountsMasterListHandler = masterService.accountsMasterListHandler(jwt);
			data.setAccountsMaster(accountsMasterListHandler);
			return data;
		}
		case "taxInfo": {
			List<TaxInfo> taxInfoListHandler = masterService.findTaxInfoListHandler(jwt);
			data.setTaxInfo(taxInfoListHandler);
			return data;
		}
		case "beneficiaryMaster": {
			List<BeneficiaryMaster> beneficiaryListByOfficeName = masterService.getBeneficiaryListByOfficeName(jwt,
					officeName);
			data.setBeneficiaryMaster(beneficiaryListByOfficeName);
			return data;
		}
		case "openingBalance": {
			List<OpeningBalance> openingBalances = new ArrayList<OpeningBalance>();
			List<OpeningBalance> filteredLst = null;
			if (fromDate != null && toDate != null) {
				filteredLst = openingBalanceService.getOpeningBalancesByOfficeName(officeName).stream()
						.filter(item -> (item.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty())
								&& !item.getOpDate().isBefore(fromDate) && !item.getOpDate().isAfter(toDate))
						.collect(Collectors.toList());
			}
			if (voucherStatus.equals("Pending")) {
				if (fromDate == null && toDate == null) {
					openingBalances.addAll(openingBalanceRepo.findPendingDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					openingBalances.addAll(filteredLst);
				}
			}
			if (voucherStatus.equals("Approved")) {
				if (fromDate == null && toDate == null) {
					openingBalances.addAll(openingBalanceRepo.findApprovedDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					openingBalances.addAll(filteredLst);
				}
			}
			if (voucherStatus.isEmpty()) {
				if (fromDate == null && toDate == null) {
					openingBalances.addAll(openingBalanceRepo.findPendingDataByOfficeName(officeName));
					openingBalances.addAll(openingBalanceRepo.findApprovedDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					openingBalances.addAll(filteredLst);
				}
			}
			data.setOpeningBalance(openingBalances);
			return data;
		}
		case "cashReceiptVoucher": {
			List<CashReceiptVoucher> cashReceiptVouchers = new ArrayList<CashReceiptVoucher>();
			List<CashReceiptVoucher> filteredLst = null;
			if (fromDate != null && toDate != null) {
				filteredLst = cashReceiptVoucherService.getVouchersByOfficeName(officeName).stream()
						.filter(item -> (item.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty())
								&& !item.getDate().isBefore(fromDate) && !item.getDate().isAfter(toDate))
						.collect(Collectors.toList());
			}
			if (voucherNo != null && !voucherNo.isEmpty()) {
				logger.info(voucherNo);
				cashReceiptVouchers.add(cashReceiptRepo.findByVoucherNo(voucherNo).get());
			} else {
				if (voucherStatus.equals("Pending")) {
					if (fromDate == null && toDate == null) {
						cashReceiptVouchers.addAll(cashReceiptRepo.findPendingDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						cashReceiptVouchers.addAll(filteredLst);
					}
				}
				if (voucherStatus.equals("Approved")) {
					if (fromDate == null && toDate == null) {
						cashReceiptVouchers.addAll(cashReceiptRepo.findApprovedDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						cashReceiptVouchers.addAll(filteredLst);
					}
				}
				if (voucherStatus.isEmpty()) {
					if (fromDate == null && toDate == null) {
						cashReceiptVouchers.addAll(cashReceiptRepo.findPendingDataByOfficeName(officeName));
						cashReceiptVouchers.addAll(cashReceiptRepo.findApprovedDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						cashReceiptVouchers.addAll(filteredLst);
					}
				}

			}
			data.setCashReceiptVoucher(cashReceiptVouchers);
			return data;
		}
		case "adjustmentReceiptVoucher": {
			List<AdjustmentReceiptVoucher> adjustmentReceiptVouchers = new ArrayList<AdjustmentReceiptVoucher>();
			List<AdjustmentReceiptVoucher> filteredLst = null;
			if (fromDate != null && toDate != null) {
				filteredLst = adjustmentReceiptVoucherService.getVoucherByOfficeName(officeName).stream()
						.filter(item -> (item.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty())
								&& !item.getDate().isBefore(fromDate) && !item.getDate().isAfter(toDate))
						.collect(Collectors.toList());
			}
			if (voucherNo != null && !voucherNo.isEmpty()) {
				adjustmentReceiptVouchers.add(adjustmentReceiptVoucherRepo.findByVoucherNo(voucherNo).get());
			} else {
				if (voucherStatus.equals("Pending")) {
					if (fromDate == null && toDate == null) {
						adjustmentReceiptVouchers
								.addAll(adjustmentReceiptVoucherRepo.findPendingDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						adjustmentReceiptVouchers.addAll(filteredLst);
					}
				}
				if (voucherStatus.equals("Approved")) {
					if (fromDate == null && toDate == null) {
						adjustmentReceiptVouchers
								.addAll(adjustmentReceiptVoucherRepo.findApprovedDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						adjustmentReceiptVouchers.addAll(filteredLst);
					}
				}
				if (voucherStatus.isEmpty()) {
					if (fromDate == null && toDate == null) {
						adjustmentReceiptVouchers
								.addAll(adjustmentReceiptVoucherRepo.findPendingDataByOfficeName(officeName));
						adjustmentReceiptVouchers
								.addAll(adjustmentReceiptVoucherRepo.findApprovedDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						adjustmentReceiptVouchers.addAll(filteredLst);
					}
				}

			}
			data.setAdjustmentReceiptVoucher(adjustmentReceiptVouchers);
			return data;
		}
		case "paymentVoucher": {
			List<PaymentVoucher> paymentVouchers = new ArrayList<PaymentVoucher>();
			List<PaymentVoucher> filteredLst = null;
			if (fromDate != null && toDate != null) {
				filteredLst = paymentVoucherService.getVoucherByOfficeName(officeName).stream()
						.filter(item -> (item.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty())
								&& !item.getDate().isBefore(fromDate) && !item.getDate().isAfter(toDate))
						.collect(Collectors.toList());
			}
			if (voucherNo != null && !voucherNo.isEmpty()) {
				paymentVouchers.add(paymentVoucherRepo.findByVoucherNo(voucherNo).get());
			} else {
				if (voucherStatus.equals("Pending")) {
					if (fromDate == null && toDate == null) {
						paymentVouchers.addAll(paymentVoucherRepo.findPendingDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						paymentVouchers.addAll(filteredLst);
					}
				}
				if (voucherStatus.equals("Approved")) {
					if (fromDate == null && toDate == null) {
						paymentVouchers.addAll(paymentVoucherRepo.findApprovedDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						paymentVouchers.addAll(filteredLst);
					}
				}
				if (voucherStatus.isEmpty()) {
					if (fromDate == null && toDate == null) {
						paymentVouchers.addAll(paymentVoucherRepo.findPendingDataByOfficeName(officeName));
						paymentVouchers.addAll(paymentVoucherRepo.findApprovedDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						paymentVouchers.addAll(filteredLst);
					}
				}
			}
			data.setPaymentVoucher(paymentVouchers);
			return data;
		}
		case "contraEntry": {
			List<ContraEntry> contraEntryData = contraVoucherService.getContraEntryData(officeName);
			data.setContraEntry(contraEntryData);
			return data;
		}
		case "journalVoucher": {
			List<JournalVoucher> journalVouchers = new ArrayList<JournalVoucher>();
			List<JournalVoucher> filteredLst = null;
			if (fromDate != null && toDate != null) {
				filteredLst = journalVoucherService.getJvByOfficeName(officeName).stream()
						.filter(item -> (item.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty())
								&& !item.getJvDate().isBefore(fromDate) && !item.getJvDate().isAfter(toDate))
						.collect(Collectors.toList());
			}
			if (voucherNo != null && !voucherNo.isEmpty()) {

				journalVouchers.add(journalVoucherRepo.findByJvNo(voucherNo).get());
			} else {
				if (voucherStatus.equals("Pending")) {
					if (fromDate == null && toDate == null) {
						journalVouchers.addAll(journalVoucherRepo.findPendingDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						journalVouchers.addAll(filteredLst);
					}
				}
				if (voucherStatus.equals("Approved")) {
					if (fromDate == null && toDate == null) {
						journalVouchers.addAll(journalVoucherRepo.findApprovedDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						journalVouchers.addAll(filteredLst);
					}
				}
				if (voucherStatus.isEmpty()) {
					if (fromDate == null && toDate == null) {
						journalVouchers.addAll(journalVoucherRepo.findPendingDataByOfficeName(officeName));
						journalVouchers.addAll(journalVoucherRepo.findApprovedDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						journalVouchers.addAll(filteredLst);
					}
				}

			}
			data.setJournalVoucher(journalVouchers);
			return data;
		}
		case "drCrNote": {
			List<DebitOrCreditNote> DebitOrCreditNote = new ArrayList<DebitOrCreditNote>();
			List<DebitOrCreditNote> filteredLst = null;
			if (fromDate != null && toDate != null) {
				filteredLst = debitCreditNoteService.findDrCrNoteByOfficeName(officeName).stream()
						.filter(item -> (item.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty())
								&& !item.getDate().isBefore(fromDate) && !item.getDate().isAfter(toDate))
						.collect(Collectors.toList());
			}
			if (voucherNo != null && !voucherNo.isEmpty()) {
				DebitOrCreditNote.add(debitOrCreditNoteRepo.findByDrCrNo(voucherNo).get());
			} else {
				if (voucherStatus.equals("Pending")) {
					if (fromDate == null && toDate == null) {
						DebitOrCreditNote.addAll(debitOrCreditNoteRepo.findPendingDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						DebitOrCreditNote.addAll(filteredLst);
					}
				}
				if (voucherStatus.equals("Approved")) {
					if (fromDate == null && toDate == null) {
						DebitOrCreditNote.addAll(debitOrCreditNoteRepo.findApprovedDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						DebitOrCreditNote.addAll(filteredLst);
					}
				}
				if (voucherStatus.isEmpty()) {
					if (fromDate == null && toDate == null) {
						DebitOrCreditNote.addAll(debitOrCreditNoteRepo.findPendingDataByOfficeName(officeName));
						DebitOrCreditNote.addAll(debitOrCreditNoteRepo.findApprovedDataByOfficeName(officeName));
					} else if (fromDate != null && toDate != null) {
						DebitOrCreditNote.addAll(filteredLst);
					}
				}

			}
			data.setDrCrNote(DebitOrCreditNote);
			return data;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + formType);
		}

	}

	@Autowired
	private BillsGstObRepo billsGstObRepo;

	@Autowired
	private SundryCrObRepo sundryCrObRepo;

	@Autowired
	private SundryDrObRepo sundryDrObRepo;

	public Vouchers getBillsAccountsFilteredData(String formType, LocalDate fromDate, LocalDate toDate,
			String officeName, String voucherStatus, String jwt) throws Exception {
		Vouchers data = new Vouchers();

		switch (formType) {
		case "supplierAdvance": {
			List<SupplierAdvance> SupplierAdvances = new ArrayList<SupplierAdvance>();
			List<SupplierAdvance> filteredLst = null;

			if (fromDate != null && toDate != null) {
				filteredLst = supplierAdvanceService.fetchOutstandingAdvancesByProduct(null).stream()
						.filter(item -> (item.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty())
								&& !item.getDate().isBefore(fromDate) && !item.getDate().isAfter(toDate))
						.collect(Collectors.toList());
			}
			if (voucherStatus.equals("Pending")) {
				if (fromDate == null && toDate == null) {
					SupplierAdvances.addAll(supplierAdvanceRepo.findPendingData());
				} else if (fromDate != null && toDate != null) {
					SupplierAdvances.addAll(filteredLst);
				}
			}
			if (voucherStatus.equals("Approved")) {
				if (fromDate == null && toDate == null) {
					SupplierAdvances.addAll(supplierAdvanceRepo.findApprovedData());
				} else if (fromDate != null && toDate != null) {
					SupplierAdvances.addAll(filteredLst);
				}
			}
			if (voucherStatus.isEmpty()) {
				if (fromDate == null && toDate == null) {
					SupplierAdvances.addAll(supplierAdvanceRepo.findPendingData());
					SupplierAdvances.addAll(supplierAdvanceRepo.findApprovedData());
				} else if (fromDate != null && toDate != null) {
					SupplierAdvances.addAll(filteredLst);
				}
			}
			data.setSupplierAdvance(SupplierAdvances);
			return data;
		}
		case "billsGstOb": {
			List<BillsGstOb> billsGstObs = new ArrayList<BillsGstOb>();
			List<BillsGstOb> filteredLst = null;

			if (fromDate != null && toDate != null) {
				filteredLst = billsGstObRepo.findByOfficeName(officeName).stream()
						.filter(item -> (item.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty())
								&& !item.getDate().isBefore(fromDate) && !item.getDate().isAfter(toDate))
						.collect(Collectors.toList());
			}
			if (voucherStatus.equals("Pending")) {
				if (fromDate == null && toDate == null) {
					billsGstObs.addAll(billsGstObRepo.findPendingDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					billsGstObs.addAll(filteredLst);
				}
			}
			if (voucherStatus.equals("Approved")) {
				if (fromDate == null && toDate == null) {
					billsGstObs.addAll(billsGstObRepo.findApprovedDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					billsGstObs.addAll(filteredLst);
				}
			}
			if (voucherStatus.isEmpty()) {
				if (fromDate == null && toDate == null) {
					billsGstObs.addAll(billsGstObRepo.findPendingDataByOfficeName(officeName));
					billsGstObs.addAll(billsGstObRepo.findApprovedDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					billsGstObs.addAll(filteredLst);
				}
			}
			data.setBillsGstOb(billsGstObs);
			return data;
		}
		case "sundryCrOb": {
			List<SundryCrOb> sundryCrObs = new ArrayList<SundryCrOb>();
			List<SundryCrOb> filteredLst = null;

			if (fromDate != null && toDate != null) {
				filteredLst = sundryCrObRepo.findByOfficeName(null).stream()
						.filter(item -> (item.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty())
								&& !item.getInvoiceDate().isBefore(fromDate) && !item.getInvoiceDate().isAfter(toDate))
						.collect(Collectors.toList());
			}
			if (voucherStatus.equals("Pending")) {
				if (fromDate == null && toDate == null) {
					sundryCrObs.addAll(sundryCrObRepo.findPendingDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					sundryCrObs.addAll(filteredLst);
				}
			}
			if (voucherStatus.equals("Approved")) {
				if (fromDate == null && toDate == null) {
					sundryCrObs.addAll(sundryCrObRepo.findApprovedDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					sundryCrObs.addAll(filteredLst);
				}
			}
			if (voucherStatus.isEmpty()) {
				if (fromDate == null && toDate == null) {
					sundryCrObs.addAll(sundryCrObRepo.findPendingDataByOfficeName(officeName));
					sundryCrObs.addAll(sundryCrObRepo.findApprovedDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					sundryCrObs.addAll(filteredLst);
				}
			}
			data.setSundryCrOb(sundryCrObs);
			return data;
		}
		case "sundryDrOb": {
			List<SundryDrOb> sundryDrOb = new ArrayList<SundryDrOb>();
			List<SundryDrOb> filteredLst = null;

			if (fromDate != null && toDate != null) {
				filteredLst = sundryDrObRepo.findByOfficeName(officeName).stream()
						.filter(item -> (item.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty())
								&& !item.getInvoiceDate().isBefore(fromDate) && !item.getInvoiceDate().isAfter(toDate))
						.collect(Collectors.toList());
			}
			if (voucherStatus.equals("Pending")) {
				if (fromDate == null && toDate == null) {
					sundryDrOb.addAll(sundryDrObRepo.findPendingDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					sundryDrOb.addAll(filteredLst);
				}
			}
			if (voucherStatus.equals("Approved")) {
				if (fromDate == null && toDate == null) {
					sundryDrOb.addAll(sundryDrObRepo.findApprovedDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					sundryDrOb.addAll(filteredLst);
				}
			}
			if (voucherStatus.isEmpty()) {
				if (fromDate == null && toDate == null) {
					sundryDrOb.addAll(sundryDrObRepo.findPendingDataByOfficeName(officeName));
					sundryDrOb.addAll(sundryDrObRepo.findApprovedDataByOfficeName(officeName));
				} else if (fromDate != null && toDate != null) {
					sundryDrOb.addAll(filteredLst);
				}
			}
			data.setSundryDrOb(sundryDrOb);
			return data;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + formType);
		}
	}

}
