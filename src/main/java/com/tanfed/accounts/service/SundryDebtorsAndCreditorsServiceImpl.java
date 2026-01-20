package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.entity.*;
import com.tanfed.accounts.model.BuyerFirmInfo;
import com.tanfed.accounts.model.DataForIC;
import com.tanfed.accounts.model.IcTableData;
import com.tanfed.accounts.model.IcmObject;
import com.tanfed.accounts.model.SundryDebtorsSubHeadTable;
import com.tanfed.accounts.model.SupplierInfo;
import com.tanfed.accounts.model.VoucherApproval;
import com.tanfed.accounts.repository.*;
import com.tanfed.accounts.response.DataForSundryDebtor;
import com.tanfed.accounts.utils.CodeGenerator;

import jakarta.transaction.Transactional;

import com.tanfed.accounts.model.BankInfo;
import com.tanfed.accounts.model.InvoiceCollectionObject;

@Service
public class SundryDebtorsAndCreditorsServiceImpl implements SundryDebtorsAndCreditorsService {

	@Autowired
	private MasterService masterService;

	@Autowired
	private CashReceiptVoucherService cashReceiptVoucherService;

	@Autowired
	private AdjustmentReceiptVoucherService adjustmentReceiptVoucherService;

	@Autowired
	private PaymentVoucherService paymentVoucherService;

	@Autowired
	private DebitCreditNoteService debitCreditNoteService;

	@Autowired
	private SundryDrCrTableRepo sundryDrCrTableRepo;

	private static Logger logger = LoggerFactory.getLogger(SundryDebtorsAndCreditorsServiceImpl.class);

	@Override
	public DataForSundryDebtor getDataForSundryBills(String jwt, String ifmsId, String idNo, String officeName,
			String month, String formType) throws Exception {
		try {
			DataForSundryDebtor data = new DataForSundryDebtor();
			if (ifmsId != null && !ifmsId.isEmpty()) {
				BuyerFirmInfo buyerFirmInfo = masterService.getBuyerFirmByFirmNameHandler(jwt, ifmsId);
				data.setBlock(buyerFirmInfo.getBlock());
				data.setDistrict(buyerFirmInfo.getDistrict());
				data.setGstNo(buyerFirmInfo.getBuyerGstNo());
				data.setNameOfInstitution(buyerFirmInfo.getNameOfInstitution());
				data.setTaluk(buyerFirmInfo.getTaluk());
				data.setVillage(buyerFirmInfo.getVillage());
				data.setAddress(buyerFirmInfo.getAddress());
			}
			if (idNo != null && !idNo.isEmpty()) {
				if (idNo.startsWith("AR")) {
					AdjustmentReceiptVoucher adjustmentReceiptVoucher = adjustmentReceiptVoucherService
							.getVoucherByVoucherNo(idNo);
					data.setDate(adjustmentReceiptVoucher.getDate());
					data.setAmount(adjustmentReceiptVoucher.getReceivedAmount());
					data.setMainHead(adjustmentReceiptVoucher.getMainHead());
					data.setSubHead(adjustmentReceiptVoucher.getSubHead());
					data.setRemarks(adjustmentReceiptVoucher.getNarration());
				}
				if (idNo.startsWith("CR")) {
					CashReceiptVoucher cashReceiptVoucher = cashReceiptVoucherService
							.getCashReceiptVoucherByVoucherNo(idNo);
					data.setDate(cashReceiptVoucher.getDate());
					data.setAmount(cashReceiptVoucher.getReceivedAmount());
					data.setMainHead(cashReceiptVoucher.getMainHead());
					data.setSubHead(cashReceiptVoucher.getSubHead());
					data.setRemarks(cashReceiptVoucher.getRemarks());
				}
				if (idNo.startsWith("PV")) {
					PaymentVoucher paymentVoucher = paymentVoucherService.getVoucherByVoucherNo(idNo);
					data.setDate(paymentVoucher.getDate());
					data.setAmount(paymentVoucher.getAmount());
					data.setMainHead(paymentVoucher.getMainHead());
					data.setSubHead(paymentVoucher.getSubHead());
					data.setRemarks(paymentVoucher.getNarration());
				}
				if (idNo.startsWith("DN") || idNo.startsWith("CN")) {
					DebitOrCreditNote debitOrCreditNote = debitCreditNoteService
							.fetchDebitOrCreditNoteByVoucherNo(idNo);
					data.setDate(debitOrCreditNote.getDate());
					data.setAmount(debitOrCreditNote.getDrCrNoteValue());
					data.setMainHead(debitOrCreditNote.getMainHead());
					data.setSubHead(debitOrCreditNote.getSubHead());
					data.setRemarks(debitOrCreditNote.getRemarks());
					if (debitOrCreditNote.getIfmsId() != null) {
						BuyerFirmInfo buyerFirmInfo = masterService.getBuyerFirmByFirmNameHandler(jwt,
								debitOrCreditNote.getIfmsId());
						data.setBlock(buyerFirmInfo.getBlock());
						data.setDistrict(buyerFirmInfo.getDistrict());
						data.setGstNo(buyerFirmInfo.getBuyerGstNo());
						data.setNameOfInstitution(buyerFirmInfo.getNameOfInstitution());
						data.setTaluk(buyerFirmInfo.getTaluk());
						data.setVillage(buyerFirmInfo.getVillage());
						data.setAddress(buyerFirmInfo.getAddress());
						data.setIfmsId(buyerFirmInfo.getIfmsIdNo());
					} else {
						SupplierInfo supplierInfo = masterService
								.getSupplierInfoBySupplierNameHandler(debitOrCreditNote.getName(), jwt);
						data.setDistrict(supplierInfo.getDistrict());
						data.setGstNo(supplierInfo.getSupplierGst());
						data.setNameOfInstitution(supplierInfo.getSupplierName());
						data.setIfmsId(0000l + supplierInfo.getId().toString());
					}
				}
			}
			if (officeName != null && !officeName.isEmpty()) {
				if (month != null && !month.isEmpty()) {
					String drCr = formType.equals("sundryDebtors") ? "Dr" : "Cr";
					data.setTableData(mapSdrScrSubHeadTableData(jwt, officeName, month, drCr));
				}
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private SundryDrObRepo sundryDrObRepo;

	@Autowired
	private SundryCrObRepo sundryCrObRepo;

	private List<SundryDebtorsSubHeadTable> mapSdrScrSubHeadTableData(String jwt, String officeName, String month,
			String drCr) throws Exception {
		List<SundryDebtorsSubHeadTable> table = new ArrayList<SundryDebtorsSubHeadTable>();
		String head = drCr.equals("Cr") ? "H.O a/c - Sundry Creditors" : "H.O a/c - Sundry Debtors";
		List<String> subHeadList = masterService.accountsMasterListHandler(jwt).stream()
				.filter(item -> item.getMainHead().equals(head)).map(item -> item.getSubHead())
				.collect(Collectors.toList());
		subHeadList.forEach(subHead -> {
			double ob = drCr.equals("Cr") ? calculateSCrObValue(month, subHead, officeName)
					: calculateSDrObValue(month, subHead, officeName, null);
			SundryDrCrTable sundryDrCrTable = sundryDrCrTableRepo.findByMonthAndSubHeadAndOfficeNameAndFormType(month,
					subHead, officeName, drCr);
			double debit = 0.0, otherDebit = 0.0, credit = 0.0, otherCredit = 0.0;
			if (sundryDrCrTable != null) {
				debit = sundryDrCrTable.getDebit();
				otherDebit = sundryDrCrTable.getOtherDebit();
				credit = sundryDrCrTable.getCredit();
				otherCredit = sundryDrCrTable.getOtherCredit();
			}
			Double total = ob + debit + otherDebit;
			Double cb = total - credit - otherCredit;
			table.add(new SundryDebtorsSubHeadTable(subHead, ob, debit, otherDebit, total, credit, otherCredit, cb));
		});
		return table;
	}

	@Override
	public Double calculateSDrObValue(String month, String subHead, String officeName, String buyerName) {
		List<SundryDrOb> sdrOb = sundryDrObRepo.findAll();
//		if (month.equals("APRIL 2025")) {
			return sdrOb.stream()
					.filter(item -> item.getOfficeName().equals(officeName)
							&& (item.getNameOfInstitution().equals(buyerName) || buyerName.isEmpty()))
					.mapToDouble(item -> item.getAmount()).sum();
//		} else {
//			return prevSundryDrCrTableData(month, subHead, officeName, "Dr").getCb();
//		}
	}

	@Override
	public Double calculateSCrObValue(String month, String subHead, String officeName) {
		List<SundryCrOb> scrOb = sundryCrObRepo.findAll();
		if (month.equals("APRIL 2025")) {
			return scrOb.stream()
					.filter(item -> item.getSubHead().equals(subHead) && item.getOfficeName().equals(officeName))
					.mapToDouble(item -> item.getAmount()).sum();
		} else {
			return prevSundryDrCrTableData(month, subHead, officeName, "Cr").getCb();
		}
	}

	@Autowired
	private ReconciliationEntryRepo reconciliationEntryRepo;

	@Override
	public ResponseEntity<String> saveSundryDebtorsAndCreditors(ReconciliationEntry obj, String jwt) throws Exception {
		try {
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.setEmpId(Arrays.asList(empId));
			reconciliationEntryRepo.save(obj);
			return new ResponseEntity<>("Reconciliation Entry Created Successfully", HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public void updateSdrJV(JournalVoucher jv, String jwt, String drCr) throws Exception {
		try {
			String type = drCr.equals("Dr") ? "debit" : "credit";
			jv.getRows().forEach(item -> {
				if (item.getDrOrCr().equals(drCr)) {
					updateSdr(item.getSubHead(), jv.getOfficeName(), item.getAmount(), type, jv.getJvMonth(), drCr);
				}
			});
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public void updateSdrAdjReceipt(AdjustmentReceiptVoucher arv, String jwt) throws Exception {
		try {
			String month = String.format("%s%s%04d", arv.getDate().getMonth(), " ", arv.getDate().getYear());
			updateSdr(arv.getSubHead(), arv.getOfficeName(), arv.getReceivedAmount(), "credit", month, "Dr");
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public void updateScrPv(PaymentVoucher pv, String jwt) throws Exception {
		try {
			String month = String.format("%s%s%04d", pv.getDate().getMonth(), " ", pv.getDate().getYear());
			updateSdr(pv.getSubHead(), pv.getOfficeName(), pv.getAmount(), "debit", month, "Cr");
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public void updateSdrReconEntry(ReconciliationEntry reconEntry, String jwt) throws Exception {
		try {
			if (reconEntry.getFormType().equals("Dr")) {
				if (reconEntry.getIdNo().startsWith("DN")) {
					DebitOrCreditNote dn = debitCreditNoteService
							.fetchDebitOrCreditNoteByVoucherNo(reconEntry.getIdNo());
					String month = String.format("%s%s%04d", dn.getDate().getMonth(), " ", dn.getDate().getYear());
					updateSdr(dn.getSubHead(), dn.getOfficeName(), dn.getDrCrNoteValue(), "otherDebit", month,
							reconEntry.getFormType());
				} else {
					switch (reconEntry.getIdNo().substring(0, 2)) {
					case "CN": {
						DebitOrCreditNote cn = debitCreditNoteService
								.fetchDebitOrCreditNoteByVoucherNo(reconEntry.getIdNo());
						String month = String.format("%s%s%04d", cn.getDate().getMonth(), " ", cn.getDate().getYear());
						updateSdr(cn.getSubHead(), cn.getOfficeName(), cn.getDrCrNoteValue(), "otherCredit", month,
								reconEntry.getFormType());
					}
					case "CR": {
						CashReceiptVoucher crv = cashReceiptVoucherService
								.getCashReceiptVoucherByVoucherNo(reconEntry.getIdNo());
						String month = String.format("%s%s%04d", crv.getDate().getMonth(), " ",
								crv.getDate().getYear());
						updateSdr(crv.getSubHead(), crv.getOfficeName(), crv.getReceivedAmount(), "otherCredit", month,
								reconEntry.getFormType());
					}
					case "AR": {
						AdjustmentReceiptVoucher arv = adjustmentReceiptVoucherService
								.getVoucherByVoucherNo(reconEntry.getIdNo());
						String month = String.format("%s%s%04d", arv.getDate().getMonth(), " ",
								arv.getDate().getYear());
						updateSdr(arv.getSubHead(), arv.getOfficeName(), arv.getReceivedAmount(), "otherCredit", month,
								reconEntry.getFormType());
					}
					default:
						throw new IllegalArgumentException("Unexpected value: " + reconEntry.getIdNo());
					}
				}
			} else {
				if (reconEntry.getIdNo().startsWith("DN")) {
					DebitOrCreditNote dn = debitCreditNoteService
							.fetchDebitOrCreditNoteByVoucherNo(reconEntry.getIdNo());

					String month = String.format("%s%s%04d", dn.getDate().getMonth(), " ", dn.getDate().getYear());

					updateSdr(dn.getSubHead(), dn.getOfficeName(), dn.getDrCrNoteValue(), "otherCredit", month,
							reconEntry.getFormType());

				} else if (reconEntry.getIdNo().startsWith("CN")) {
					DebitOrCreditNote dn = debitCreditNoteService
							.fetchDebitOrCreditNoteByVoucherNo(reconEntry.getIdNo());

					String month = String.format("%s%s%04d", dn.getDate().getMonth(), " ", dn.getDate().getYear());

					updateSdr(dn.getSubHead(), dn.getOfficeName(), dn.getDrCrNoteValue(), "otherDebit", month,
							reconEntry.getFormType());
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private SundryDrCrTable prevSundryDrCrTableData(String month, String subHead, String officeName, String drCr) {
		String[] monthAndYr = month.split(" ");
		// Parse month and year correctly
		YearMonth yearMonth = YearMonth.of(Integer.parseInt(monthAndYr[1]), Month.valueOf(monthAndYr[0].toUpperCase()));

		SundryDrCrTable sundryDrCrTable;
		int n = 1;
		do {
			YearMonth prevYearMonth = yearMonth.minusMonths(n++);
			String prevMonth = prevYearMonth.getMonth().toString() + " " + prevYearMonth.getYear();
			sundryDrCrTable = sundryDrCrTableRepo.findByMonthAndSubHeadAndOfficeNameAndFormType(prevMonth, subHead,
					officeName, drCr);
			if (n == 100) {
				break;
			}
		} while (sundryDrCrTable == null);

		return sundryDrCrTable;
	}

	private void updateSdr(String subHead, String officeName, Double amount, String type, String month, String drCr) {
		SundryDrCrTable sundryDrCrTable = sundryDrCrTableRepo.findByMonthAndSubHeadAndOfficeNameAndFormType(month,
				subHead, officeName, drCr);
		if (sundryDrCrTable == null) {
			sundryDrCrTable = prevSundryDrCrTableData(month, subHead, officeName, drCr);
			Double cb = sundryDrCrTable.getCb() == null ? 0.0 : sundryDrCrTable.getCb();
			switch (type) {
			case "debit": {
				sundryDrCrTableRepo.save(new SundryDrCrTable(null, drCr, month, subHead, officeName, amount, 0.0, 0.0,
						0.0, cb + amount));
				break;
			}
			case "otherDebit": {
				sundryDrCrTableRepo.save(new SundryDrCrTable(null, drCr, month, subHead, officeName, 0.0, amount, 0.0,
						0.0, cb + amount));
				break;
			}
			case "credit": {
				sundryDrCrTableRepo.save(new SundryDrCrTable(null, drCr, month, subHead, officeName, 0.0, 0.0, amount,
						0.0, cb - amount));
				break;
			}
			case "otherCredit": {
				sundryDrCrTableRepo.save(new SundryDrCrTable(null, drCr, month, subHead, officeName, 0.0, 0.0, 0.0,
						amount, cb - amount));
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + type);
			}

		} else {
			switch (type) {
			case "debit": {
				sundryDrCrTable.setDebit(sundryDrCrTable.getDebit() + amount);
				sundryDrCrTable.setCb(sundryDrCrTable.getCb() + amount);
				sundryDrCrTableRepo.save(sundryDrCrTable);
				break;
			}
			case "otherDebit": {
				sundryDrCrTable.setOtherDebit(sundryDrCrTable.getOtherDebit() + amount);
				sundryDrCrTable.setCb(sundryDrCrTable.getCb() + amount);
				sundryDrCrTableRepo.save(sundryDrCrTable);
				break;
			}
			case "credit": {
				sundryDrCrTable.setCredit(sundryDrCrTable.getCredit() + amount);
				sundryDrCrTable.setCb(sundryDrCrTable.getCb() - amount);
				sundryDrCrTableRepo.save(sundryDrCrTable);
				break;
			}
			case "otherCredit": {
				sundryDrCrTable.setOtherCredit(sundryDrCrTable.getOtherCredit() + amount);
				sundryDrCrTable.setCb(sundryDrCrTable.getCb() - amount);
				sundryDrCrTableRepo.save(sundryDrCrTable);
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + type);
			}
		}
	}

	@Override
	public List<ReconciliationEntry> fetchReconciliationEntriesByOfficeName(String officeName) throws Exception {
		try {
			return reconciliationEntryRepo.findByOfficeName(officeName);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public DataForIC fetchDataForIC(String officeName, String activity, String collectionProcess, String jwt,
			LocalDate fromDate, LocalDate toDate, String ccbBranch, LocalDate ackEntryDate, LocalDate dueDate,
			LocalDate addedToPresentDate, String icmNo) throws Exception {
		try {
			DataForIC data = new DataForIC();
			if (officeName != null && !officeName.isEmpty()) {
				if (activity != null && !activity.isEmpty()) {
					List<SundryDrOb> sDrOb = sundryDrObRepo.findByOfficeName(officeName).stream()
							.filter(item -> item.getVoucherStatus().equals("Approved")
									&& item.getActivity().equals(activity) && item.getStatus().equals("Dr"))
							.collect(Collectors.toList());
					if (collectionProcess.equals("invoiceAckEntry")) {
						invoiceAckEntryData(data, officeName, sDrOb, fromDate, toDate, jwt);
					}
					if ("invoiceCollectionAvailable".equals(collectionProcess)) {
						invoiceCollectionAvailableData(data, sDrOb, officeName, jwt, ccbBranch, ackEntryDate);
					}
					if ("presentToCCB".equals(collectionProcess)) {
						presentToCCBData(data, sDrOb, ccbBranch, dueDate, addedToPresentDate);
					}
					if ("collectionUpdate".equals(collectionProcess)) {
						collectionUpdateData(data, sDrOb, icmNo);
					}
				}
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private void collectionUpdateData(DataForIC data, List<SundryDrOb> sDrOb, String icmNo) throws Exception {
		try {
			List<SundryDrOb> NoOfPresented = sDrOb.stream().filter(temp -> {
				return temp.getDateOfPresent() != null && temp.getIsShort().equals(false) && (temp
						.getCollectionValue() == null
						|| (temp.getCollectionValue().stream().mapToDouble(item -> item).sum() < temp.getAmount()));
			}).collect(Collectors.toList());

			Integer totalNoOfInvoicePresented = 0;
			Double totalValueOfInvoices = 0.0;
			Set<String> icmNoLst = new HashSet<String>();
			for (var i : NoOfPresented) {
				totalNoOfInvoicePresented++;
				if (i.getAmount() != null) {
					totalValueOfInvoices += i.getAmount();
				}
				icmNoLst.add(i.getIcmNo());
			}
			data.setTotalNoOfInvoicePresented(totalNoOfInvoicePresented);
			data.setTotalValueOfInvoices(totalValueOfInvoices);
			data.setIcmNoList(icmNoLst);
			if (icmNo != null && !icmNo.isEmpty()) {
				data.setTableData(sDrOb.stream().filter(item -> item.getDateOfPresent() != null
						&& icmNo.equals(item.getIcmNo()) && item.getIsShort().equals(false)
						&& item.getVoucherStatusICP3().equals("Approved") && (item.getCollectionValue() == null || (item
								.getCollectionValue().stream().mapToDouble(sum -> sum).sum() < item.getAmount())))
						.map(item -> {
							try {
								return new IcTableData(item.getInvoiceNo(), item.getInvoiceDate(), item.getIfmsId(),
										item.getNameOfInstitution(), item.getDistrict(), item.getAmount(),
										item.getQty(), item.getCcbBranch(), item.getDueDate(),
										fetchCollectedValue(item), item.getIsShort(), null);
							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}
						}).collect(Collectors.toList()));
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private Double fetchCollectedValue(SundryDrOb inv) {
		return inv.getCollectionValue() == null ? 0.0
				: inv.getCollectionValue().stream().mapToDouble(item -> item).sum();
	}

	private void presentToCCBData(DataForIC data, List<SundryDrOb> sDrOb, String ccbBranch, LocalDate dueDate,
			LocalDate addedToPresentDate) throws Exception {
		try {
			List<SundryDrOb> InvoicesAvlToPresent = sDrOb.stream().filter(temp -> {
				return temp.getAddedToPresentDate() != null && !"AdjReceipt".equals(temp.getCollectionMethod())
						&& temp.getDateOfPresent() == null;
			}).collect(Collectors.toList());
			Set<String> ccbBranchlst = new HashSet<String>();
			Set<LocalDate> dueDatelst = new HashSet<LocalDate>();
			Set<LocalDate> addedToPresentDatelst = new HashSet<LocalDate>();
			Integer totalNoOfAvlToPresent = 0;
			Double totalValueOfInvoices = 0.0;
			for (var item : InvoicesAvlToPresent) {
				totalNoOfAvlToPresent++;
				if (item.getAmount() != null) {
					totalValueOfInvoices += item.getAmount();
				}
				ccbBranchlst.add(item.getCcbBranch());
				dueDatelst.add(item.getDueDate());
				addedToPresentDatelst.add(item.getAddedToPresentDate());
			}
			data.setTotalNoOfAvlToPresent(totalNoOfAvlToPresent);
			data.setTotalValueOfInvoices(totalValueOfInvoices);
			data.setCcbBranchLst(ccbBranchlst);
			data.setDueDate(dueDatelst);
			data.setAddedToPresentDate(addedToPresentDatelst);
			logger.info(ccbBranch);
			logger.info("{}", dueDate);
			if (ccbBranch != null && !ccbBranch.isEmpty()) {
				if (dueDate != null) {
					data.setTableData(sDrOb.stream().filter(item -> {
						return item.getAddedToPresentDate() != null && item.getDueDate() != null
								&& addedToPresentDate.equals(item.getAddedToPresentDate())
								&& !"AdjReceipt".equals(item.getCollectionMethod())
								&& item.getVoucherStatusICP2().equals("Approved")
								&& ccbBranch.equals(item.getCcbBranch()) && item.getDueDate().isEqual(dueDate)
								&& item.getDateOfPresent() == null;
					}).map(item -> {
						try {
							return new IcTableData(item.getInvoiceNo(), item.getInvoiceDate(), item.getIfmsId(),
									item.getNameOfInstitution(), item.getDistrict(), item.getAmount(), item.getQty(),
									item.getCcbBranch(), item.getDueDate(), null, null, null);
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					}).collect(Collectors.toList()));
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		}

	}

	private void invoiceCollectionAvailableData(DataForIC data, List<SundryDrOb> sDrOb, String officeName, String jwt,
			String ccbBranch, LocalDate ackEntryDate) throws Exception {
		try {
			List<SundryDrOb> AvlAckInvoices = sDrOb.stream().filter(temp -> {
				return temp.getAckQty() != null && temp.getAckEntryDate() != null
						&& temp.getAddedToPresentDate() == null;
			}).collect(Collectors.toList());
			Integer totalNoOfAvlAckInvoices = 0;
			Double totalValueOfInvoices = 0.0;

			Set<String> ccbBranchlst = new HashSet<String>();
			Set<LocalDate> ackEntryDatelst = new HashSet<LocalDate>();

			for (SundryDrOb o : AvlAckInvoices) {
				totalNoOfAvlAckInvoices++;
				if (o.getAmount() != null) {
					totalValueOfInvoices += o.getAmount();
				}
				ccbBranchlst.add(o.getCcbBranch());
				ackEntryDatelst.add(o.getAckEntryDate());
			}
			data.setNoOfAvlAckInvoices(totalNoOfAvlAckInvoices);
			data.setTotalValueOfInvoices(totalValueOfInvoices);
			data.setCcbBranchLst(ccbBranchlst);
			data.setAckEntryDate(ackEntryDatelst);

			if (ccbBranch != null && !ccbBranch.isEmpty()) {
				data.setTableData(sDrOb.stream()
						.filter(item -> item.getAckQty() != null && ccbBranch.equals(item.getCcbBranch())
								&& item.getVoucherStatusICP1().equals("Approved") && null == item.getCollectionMethod()
								&& ackEntryDate.equals(item.getAckEntryDate()) && item.getAddedToPresentDate() == null)
						.map(item -> {
							try {
								return new IcTableData(item.getInvoiceNo(), item.getInvoiceDate(), item.getIfmsId(),
										item.getNameOfInstitution(), item.getDistrict(), item.getAmount(),
										item.getQty(), item.getCcbBranch(), item.getDueDate(), null, null, null);
							} catch (Exception e) {
								e.printStackTrace();
								return null;
							}
						}).collect(Collectors.toList()));
			}

			data.setAdjTableData(sDrOb.stream()
					.filter(temp -> null == temp.getAddedToPresentDate() && null != temp.getVoucherStatusICP2()
							&& !temp.getVoucherStatusICP2().equals("Approved") && temp.getAdjReceipt() != null
							&& temp.getCollectionMethod().equals("AdjReceipt"))
					.map(item -> {
						try {
							return new IcTableData(item.getInvoiceNo(), item.getInvoiceDate(), item.getIfmsId(),
									item.getNameOfInstitution(), item.getDistrict(), item.getAmount(), item.getQty(),
									item.getCcbBranch(), item.getDueDate(), null, null, item.getAdjReceipt().get(0));
						} catch (Exception e) {
							e.printStackTrace();
							return new IcTableData(item.getInvoiceNo(), item.getInvoiceDate(), item.getIfmsId(),
									item.getNameOfInstitution(), item.getDistrict(), item.getAmount(), item.getQty(),
									item.getCcbBranch(), item.getDueDate(), null, null, null);
						}
					}).collect(Collectors.toList()));
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private void invoiceAckEntryData(DataForIC data, String officeName, List<SundryDrOb> sDrOb, LocalDate fromDate,
			LocalDate toDate, String jwt) throws Exception {
		try {
			Integer totalNoOfInvoices = 0;
			Double totalValueOfInvoices = 0.0;
			Integer totalNoOfInvoicesAcklgd = 0;

			for (SundryDrOb o : sDrOb) {
				totalNoOfInvoices++;
				if (o.getAmount() != null) {
					totalValueOfInvoices += o.getAmount();
				}
				if (o.getAckQty() != null) {
					totalNoOfInvoicesAcklgd++;
				}
			}
			data.setTotalNoOfInvoices(totalNoOfInvoices);
			data.setTotalValueOfInvoices(totalValueOfInvoices);
			data.setTotalNoOfInvoicesAcklgd(totalNoOfInvoicesAcklgd);
			data.setTotalNoOfInvoicesrmng(totalNoOfInvoices - totalNoOfInvoicesAcklgd);
			if (fromDate != null && toDate != null) {
				data.setTableData(sDrOb.stream().filter(item -> {
					Boolean dateMatch = !item.getInvoiceDate().isBefore(fromDate)
							&& !item.getInvoiceDate().isAfter(toDate);
					return item.getAckQty() == null && dateMatch && item.getAckEntryDate() == null;
				}).map(item -> {
					try {
						BuyerFirmInfo buyerFirmInfo = masterService.getBuyerFirmByFirmNameHandler(jwt,
								item.getNameOfInstitution());
						return new IcTableData(item.getInvoiceNo(), item.getInvoiceDate(), item.getIfmsId(),
								item.getNameOfInstitution(), item.getDistrict(), item.getAmount(), item.getQty(),
								buyerFirmInfo.getBranchName(), item.getDueDate(), null, null, null);
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}).collect(Collectors.toList()));
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private CodeGenerator codeGenerator;

	@Autowired
	private UserService userService;

	@Override
	public ResponseEntity<String> updateICData(List<InvoiceCollectionObject> obj, String jwt) throws Exception {
		try {
			final String[] code = new String[1];
			if ("presentToCCB".equals(obj.get(0).getCollectionProcess())) {
				code[0] = codeGenerator.icmNoGenerator(obj.get(0).getOfficeName());
			}
			logger.info("len {}", obj.size());
			for (var temp : obj) {
				if ("invoiceAckEntry".equals(temp.getCollectionProcess())) {
					SundryDrOb sundryDrOb = sundryDrObRepo.findByInvoiceNo(temp.getInvoiceNo());
					sundryDrOb.setAckQty(temp.getAckQty());
					sundryDrOb.setAckEntryDate(temp.getAckEntryDate());
					sundryDrOb.setCcbBranch(temp.getCcbBranch());
					sundryDrOb.setVoucherStatusICP1("Pending");
					sundryDrObRepo.save(sundryDrOb);
				}
				if ("invoiceCollectionAvailable".equals(temp.getCollectionProcess())) {
					SundryDrOb sundryDrOb = sundryDrObRepo.findByInvoiceNo(temp.getInvoiceNo());
					sundryDrOb.setVoucherStatusICP2("Pending");
					sundryDrOb.setCollectionMethod(temp.getCollectionMethod());

					if (!"AdjReceipt".equals(temp.getCollectionMethod())) {
						sundryDrOb.setAddedToPresentDate(temp.getAddedToPresentDate());
					}
					sundryDrObRepo.save(sundryDrOb);
				}
				if ("presentToCCB".equals(temp.getCollectionProcess())) {
					SundryDrOb sundryDrOb = sundryDrObRepo.findByInvoiceNo(temp.getInvoiceNo());
					sundryDrOb.setVoucherStatusICP3("Pending");
					sundryDrOb.setCollectionMethod(temp.getCollectionProcess());
					sundryDrOb.setDateOfPresent(temp.getDateOfPresent());
					sundryDrOb.setIcmNo(code[0]);
					sundryDrOb.setIsShort(false);
					sundryDrObRepo.save(sundryDrOb);
				}
				if ("collectionUpdate".equals(temp.getCollectionProcess())) {
					logger.info("loop executed");
					SundryDrOb sundryDrOb = sundryDrObRepo.findByInvoiceNo(temp.getInvoiceNo());
					if (sundryDrOb.getDateOfCollectionFromCcb() == null) {
						sundryDrOb.setDateOfCollectionFromCcb(
								new ArrayList<>(List.of(temp.getDateOfCollectionFromCcb())));
						sundryDrOb.setCollectionValue(Arrays.asList(temp.getCollectionValue()));
					} else {
						sundryDrOb.getDateOfCollectionFromCcb().add(temp.getDateOfCollectionFromCcb());
						sundryDrOb.getCollectionValue().add(temp.getCollectionValue());
					}
					sundryDrOb.setTransferDone(false);
					sundryDrOb.setIsShort(temp.getIsShort());
					SundryDrOb save = sundryDrObRepo.save(sundryDrOb);
					logger.info("{}", save);

				}
			}

			return new ResponseEntity<String>("Updated Successfully!", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> saveAdjReceiptForIcmInvoices(IcmObject obj, String jwt, String type)
			throws Exception {
		try {
			logger.info("{}", obj);
			BankInfo bankInfo = masterService.getBankInfoByOfficeNameHandler(jwt, obj.getAdjData().getOfficeName())
					.stream()
					.filter(itemData -> itemData.getAccountType().equals("Non PDS A/c")
							&& itemData.getBranchName().equals(obj.getAdjData().getBranchName()))
					.collect(Collectors.toList()).get(0);
			obj.getAdjData().setAccountType("Non PDS A/c");
			obj.getAdjData().setAccountNo(bankInfo.getAccountNumber());
			obj.getAdjData().setContraEntry("No");
			ResponseEntity<String> responseEntity = adjustmentReceiptVoucherService
					.saveAdjustmentReceiptVoucher(obj.getAdjData(), jwt);
			String responseString = responseEntity.getBody();
			if (responseString == null) {
				throw new Exception("No data found");
			}
			String prefix = "Voucher Number: ";
			int index = responseString.indexOf(prefix);
			String voucherNo = responseString.substring(index + prefix.length()).trim();
			if (type.equals("icm")) {
				List<String> invoiceList = obj.getInvoices().stream().map(i -> i.getInvoiceNo())
						.collect(Collectors.toList());
				List<SundryDrOb> sundryDrOb = sundryDrObRepo.findByIcmNo(obj.getAdjData().getIcmInvNo());
				sundryDrOb.forEach(item -> {
					if (invoiceList.contains(item.getInvoiceNo())) {
						try {
							if (item.getAdjReceipt() == null) {
								item.setAdjReceipt(Arrays
										.asList(adjustmentReceiptVoucherService.getVoucherByVoucherNo(voucherNo)));
							} else {
								item.getAdjReceipt()
										.add(adjustmentReceiptVoucherService.getVoucherByVoucherNo(voucherNo));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				sundryDrObRepo.saveAll(sundryDrOb);
			} else {
				SundryDrOb sundryDrOb = sundryDrObRepo.findByInvoiceNo(obj.getAdjData().getIcmInvNo());
				sundryDrOb
						.setAdjReceipt(Arrays.asList(adjustmentReceiptVoucherService.getVoucherByVoucherNo(voucherNo)));
				sundryDrOb.setAdjReceiptStatus(Arrays.asList("Pending"));
				sundryDrObRepo.save(sundryDrOb);
			}
			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}

	}

	@Override
	public void updateFundTransfered(List<String> invoiceNoList) throws Exception {
		try {
			invoiceNoList.forEach(item -> {
				SundryDrOb sundryDrOb = sundryDrObRepo.findByInvoiceNo(item);
				sundryDrOb.setTransferDone(true);
				sundryDrObRepo.save(sundryDrOb);
			});
		} catch (Exception e) {
			throw new Exception(e);
		}

	}

	@Override
	public void revertFundTransfered(List<String> invoiceNoList) throws Exception {
		try {
			invoiceNoList.forEach(item -> {
				SundryDrOb sundryDrOb = sundryDrObRepo.findByInvoiceNo(item);
				sundryDrOb.setTransferDone(false);
				sundryDrObRepo.save(sundryDrOb);
			});
		} catch (Exception e) {
			throw new Exception(e);
		}

	}

	@Transactional
	@Override
	public String updateAplStatusInvoiceCollection(VoucherApproval obj, String jwt) throws Exception {
		try {
			String designation = null;
			List<String> oldDesignation = null;

			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);

			switch (obj.getFormType()) {
			case "invoiceAckEntry": {
				SundryDrOb invoice = sundryDrObRepo.findById(Long.valueOf(obj.getId())).get();

				designation = userService.getNewDesignation(empId);
				oldDesignation = invoice.getDesignationICP1();

				invoice.setVoucherStatusICP1(obj.getVoucherStatus());
				if (obj.getVoucherStatus().equals("Rejected")) {
					invoice.setAckEntryDate(null);
					invoice.setAckQty(null);
					invoice.setVoucherStatusICP1(null);
				}
				if (oldDesignation == null) {
					invoice.setDesignationICP1(Arrays.asList(designation));
				} else {
					invoice.getDesignationICP1().add(designation);
				}

				sundryDrObRepo.save(invoice);
				return designation;
			}

			case "invoiceCollectionAvailable": {
				SundryDrOb invoice = sundryDrObRepo.findById(Long.valueOf(obj.getId())).get();
				designation = userService.getNewDesignation(empId);
				oldDesignation = invoice.getDesignationICP2();
				if (invoice.getCollectionMethod().equals("AdjReceipt")) {
					AdjustmentReceiptVoucher arv = adjustmentReceiptVoucherService
							.getVoucherByVoucherNo(obj.getAdjNo());
					arv.setVoucherStatus(obj.getVoucherStatus());
					arv.getEmpId().add(empId);
					if (obj.getVoucherStatus().equals("Approved")) {
						arv.setApprovedDate(LocalDate.now());
						adjustmentReceiptVoucherService.updateClosingBalance(arv);
						invoice.setCollectionValue(Arrays.asList(arv.getReceivedAmount()));
						invoice.setDateOfCollectionFromCcb(new ArrayList<>(List.of(arv.getDateOfCollection())));
						invoice.setTransferDone(false);
					}
					if (arv.getDesignation() == null) {
						arv.setDesignation(Arrays.asList(designation));
					} else {
						arv.getDesignation().add(designation);
					}
				}

				invoice.setVoucherStatusICP2(obj.getVoucherStatus());
				if (obj.getVoucherStatus().equals("Rejected")) {
					invoice.setAddedToPresentDate(null);
					invoice.setVoucherStatusICP2(null);
					invoice.setDueDate(null);
					invoice.setCollectionMethod(null);
					invoice.setAdjReceipt(null);
					invoice.setAdjReceiptStatus(null);
				}
				if (oldDesignation == null) {
					invoice.setDesignationICP2(Arrays.asList(designation));
				} else {
					invoice.getDesignationICP2().add(designation);
				}

				sundryDrObRepo.save(invoice);
				return designation;
			}

			case "presentToCCB": {
				SundryDrOb invoice = sundryDrObRepo.findById(Long.valueOf(obj.getId())).get();

				designation = userService.getNewDesignation(empId);
				oldDesignation = invoice.getDesignationICP3();

				invoice.setVoucherStatusICP3(obj.getVoucherStatus());
				if (obj.getVoucherStatus().equals("Rejected")) {
					invoice.setDateOfPresent(null);
					invoice.setIcmNo(null);
					invoice.setVoucherStatusICP3(null);
				}
				if (oldDesignation == null) {
					invoice.setDesignationICP3(Arrays.asList(designation));
				} else {
					invoice.getDesignationICP3().add(designation);
				}

				sundryDrObRepo.save(invoice);
				return designation;
			}

			case "icm": {
				List<SundryDrOb> byIcmNo = sundryDrObRepo.findByIcmNo(obj.getId());
				String designationIcp4 = userService.getNewDesignation(empId);
				AdjustmentReceiptVoucher arv = adjustmentReceiptVoucherService.getVoucherByVoucherNo(obj.getAdjNo());
				arv.setVoucherStatus(obj.getVoucherStatus());
				arv.getEmpId().add(empId);
				if (obj.getVoucherStatus().equals("Approved")) {
					arv.setApprovedDate(LocalDate.now());
					adjustmentReceiptVoucherService.updateClosingBalance(arv);
					updateSdrAdjReceipt(arv, jwt);
				}
				if (arv.getDesignation() == null) {
					arv.setDesignation(Arrays.asList(designation));
				} else {
					arv.getDesignation().add(designation);
				}

				byIcmNo.forEach(invoice -> {
					List<String> oldDesignationIcp4 = invoice.getDesignationICP4();
					if (obj.getVoucherStatus().equals("Rejected")) {
						List<String> adjNoLst = invoice.getAdjReceipt().stream().map(i -> i.getVoucherNo())
								.collect(Collectors.toList());
						if (adjNoLst.contains(obj.getAdjNo())) {
							int index = adjNoLst.indexOf(obj.getAdjNo());
							invoice.getAdjReceipt().remove(index);
							invoice.getCollectionValue().remove(index);
							invoice.getDateOfCollectionFromCcb().remove(index);
							invoice.setIsShort(false);
						}
						invoice.setTransferDone(false);
					}
					if (oldDesignationIcp4 == null) {
						invoice.setDesignationICP4(Arrays.asList(designationIcp4));
					} else {
						invoice.getDesignationICP4().add(designationIcp4);
					}
					logger.info("{}", invoice);
					sundryDrObRepo.save(invoice);
				});

				return designation;
			}

			default:
				throw new IllegalArgumentException("Unexpected value: " + obj.getFormType());
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
