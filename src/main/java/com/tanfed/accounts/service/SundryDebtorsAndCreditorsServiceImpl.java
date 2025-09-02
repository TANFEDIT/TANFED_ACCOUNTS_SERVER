package com.tanfed.accounts.service;

import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.entity.*;
import com.tanfed.accounts.model.BuyerFirmInfo;
import com.tanfed.accounts.model.SundryDebtorsSubHeadTable;
import com.tanfed.accounts.model.SupplierInfo;
import com.tanfed.accounts.repository.*;
import com.tanfed.accounts.response.DataForSundryDebtor;

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

//	private static Logger logger = LoggerFactory.getLogger(SundryDebtorsAndCreditorsServiceImpl.class);
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
					: calculateSDrObValue(month, subHead, officeName);
			SundryDrCrTable sundryDrCrTable = sundryDrCrTableRepo.findByMonthAndSubHeadAndOfficeNameAndFormType(month,
					subHead, officeName, drCr);
			Double total = ob + sundryDrCrTable.getDebit() + sundryDrCrTable.getOtherDebit();
			table.add(new SundryDebtorsSubHeadTable(subHead, ob, sundryDrCrTable.getDebit(),
					sundryDrCrTable.getOtherDebit(), total, sundryDrCrTable.getCredit(),
					sundryDrCrTable.getOtherCredit(), sundryDrCrTable.getCb()));
		});
		return table;
	}

	@Override
	public Double calculateSDrObValue(String month, String subHead, String officeName) {
		List<SundryDrOb> sdrOb = sundryDrObRepo.findAll();
		if (month.equals("APRIL 2025")) {
			return sdrOb.stream().filter(item -> item.getSubHead().equals(subHead))
					.mapToDouble(item -> item.getAmount()).sum();
		} else {
			return prevSundryDrCrTableData(month, subHead, officeName, "Dr").getCb();
		}
	}

	@Override
	public Double calculateSCrObValue(String month, String subHead, String officeName) {
		List<SundryCrOb> scrOb = sundryCrObRepo.findAll();
		if (month.equals("APRIL 2025")) {
			return scrOb.stream().filter(item -> item.getSubHead().equals(subHead))
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
		} while (sundryDrCrTable == null);

		return sundryDrCrTable;
	}

	private void updateSdr(String subHead, String officeName, Double amount, String type, String month, String drCr) {
		SundryDrCrTable sundryDrCrTable = sundryDrCrTableRepo.findByMonthAndSubHeadAndOfficeNameAndFormType(month,
				subHead, officeName, drCr);
		if (sundryDrCrTable == null) {
			sundryDrCrTable = prevSundryDrCrTableData(month, subHead, officeName, drCr);
			switch (type) {
			case "debit": {
				sundryDrCrTableRepo.save(new SundryDrCrTable(null, drCr, month, subHead, officeName, amount, 0.0, 0.0,
						0.0, sundryDrCrTable.getCb() + amount));
				break;
			}
			case "otherDebit": {
				sundryDrCrTableRepo.save(new SundryDrCrTable(null, drCr, month, subHead, officeName, 0.0, amount, 0.0,
						0.0, sundryDrCrTable.getCb() + amount));
				break;
			}
			case "credit": {
				sundryDrCrTableRepo.save(new SundryDrCrTable(null, drCr, month, subHead, officeName, 0.0, 0.0, amount,
						0.0, sundryDrCrTable.getCb() - amount));
				break;
			}
			case "otherCredit": {
				sundryDrCrTableRepo.save(new SundryDrCrTable(null, drCr, month, subHead, officeName, 0.0, 0.0, 0.0,
						amount, sundryDrCrTable.getCb() - amount));
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

//	private static double roundToTwoDecimalPlaces(double value) {
//		return new BigDecimal(value).setScale(3, RoundingMode.HALF_UP).doubleValue();
//	}
}
