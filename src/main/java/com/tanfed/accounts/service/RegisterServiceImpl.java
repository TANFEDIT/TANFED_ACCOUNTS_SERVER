package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
import com.tanfed.accounts.entity.CashReceiptVoucher;
import com.tanfed.accounts.entity.JournalVoucher;
import com.tanfed.accounts.entity.PaymentVoucher;
import com.tanfed.accounts.entity.SundryDrOb;
import com.tanfed.accounts.model.CollectionRegisterTable;
import com.tanfed.accounts.model.Invoice;
import com.tanfed.accounts.model.SundryDebtorsRegister;
import com.tanfed.accounts.repository.SundryDrObRepo;
import com.tanfed.accounts.response.CashChittaTable;
import com.tanfed.accounts.response.CashDayBookTable;
import com.tanfed.accounts.response.JournalRegisterTable;

@Service
public class RegisterServiceImpl implements RegisterService {

	@Autowired
	private CashReceiptVoucherService cashReceiptVoucherService;

	@Autowired
	private JournalVoucherService journalVoucherService;

	@Autowired
	private PaymentVoucherService paymentVoucherService;

	@Autowired
	private SundryDebtorsAndCreditorsService sundryDebtorsAndCreditorsService;

	@Autowired
	private AdjustmentReceiptVoucherService adjustmentReceiptVoucherService;
	private static Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);

	@Override
	public List<CashChittaTable> fetchCashChittaData(String officeName, LocalDate fromDate, LocalDate toDate)
			throws Exception {
		try {
			List<CashChittaTable> table = new ArrayList<CashChittaTable>();
			if (fromDate != null && toDate != null) {
				table.addAll(cashReceiptVoucherService.getVouchersByOfficeName(officeName).stream()
						.filter(item -> item.getVoucherStatus().equals("Approved") && !item.getDate().isBefore(fromDate)
								&& !item.getDate().isAfter(toDate))
						.map(item -> new CashChittaTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
								item.getSubHead(), item.getRemarks(), item.getReceivedAmount(), null, null))
						.collect(Collectors.toList()));

				table.addAll(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
						.filter(item -> item.getVoucherStatus().equals("Approved")
								&& item.getPvType().equals("Cash Payment Voucher") && !item.getDate().isBefore(fromDate)
								&& !item.getDate().isAfter(toDate))
						.map(item -> new CashChittaTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
								item.getSubHead(), item.getNarration(), null, item.getAmount(), null))
						.collect(Collectors.toList()));
			}
			table.sort(Comparator.comparing(CashChittaTable::getDate));
			return table;

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public List<CashDayBookTable> fetchCashDayBookData(String officeName, String month) throws Exception {
		try {
			List<CashDayBookTable> table = new ArrayList<CashDayBookTable>();
			if (month != null && !month.isEmpty()) {
				table.addAll(
						cashReceiptVoucherService.getVouchersByOfficeName(officeName).stream()
								.filter(item -> item.getVoucherStatus().equals("Approved") && String
										.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
										.equals(month))
								.map(item -> {
									String contra = item.getContraEntry().equals("Yes") ? "Contra Entry" : null;
									return new CashDayBookTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
											item.getSubHead(), item.getRemarks(), item.getReceivedAmount(), null,
											contra, null);
								}).collect(Collectors.toList()));

				table.addAll(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
						.filter(item -> item.getVoucherStatus().equals("Approved")
								&& item.getPvType().equals("Cash Payment Voucher")
								&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
										.equals(month))
						.map(item -> {
							String contra = item.getContraEntry().equals("Yes") ? "Contra Entry" : null;
							return new CashDayBookTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
									item.getSubHead(), item.getNarration(), null, item.getAmount(), contra, null);
						}).collect(Collectors.toList()));
			}
			table.sort(Comparator.comparing(CashDayBookTable::getDate));
			return table;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<CashDayBookTable> fetchBankDayBookData(String officeName, String jwt, String month) throws Exception {
		try {
			List<CashDayBookTable> table = new ArrayList<CashDayBookTable>();
			table.addAll(adjustmentReceiptVoucherService.getVoucherByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && String
							.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear()).equals(month))
					.map(item -> {
						String contra = item.getContraEntry().equals("Yes") ? "Contra Entry" : null;
						return new CashDayBookTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
								item.getSubHead(), item.getNarration(), item.getReceivedAmount(), null, contra,
								item.getAccountType());
					}).collect(Collectors.toList()));

			table.addAll(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved")
							&& !item.getPvType().equals("Cash Payment Voucher")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month))
					.map(item -> {
						String contra = item.getContraEntry().equals("Yes") ? "Contra Entry" : null;
						return new CashDayBookTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
								item.getSubHead(), item.getNarration(), null, item.getAmount(), contra,
								item.getAccountType());
					}).collect(Collectors.toList()));
			table.sort(Comparator.comparing(CashDayBookTable::getDate));
			return table;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<JournalRegisterTable> fetchJournalRegisterData(String officeName, String month) throws Exception {
		try {
			if (month != null && !month.isEmpty()) {
				return journalVoucherService.getJvByOfficeName(officeName).stream()
						.filter(item -> item.getVoucherStatus().equals("Approved") && item.getJvMonth().equals(month))
						.map(item -> {
							Double debit = item.getRows().stream().filter(data -> data.getDrOrCr().equals("Dr"))
									.mapToDouble(sum -> sum.getAmount()).sum();
							Double credit = item.getRows().stream().filter(data -> data.getDrOrCr().equals("Cr"))
									.mapToDouble(sum -> sum.getAmount()).sum();
							return new JournalRegisterTable(item.getVoucherNo(), item.getJvDate(), item.getRows(),
									debit, credit, item.getNarration());
						}).collect(Collectors.toList());
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<CashChittaTable> fetchSubsidyLedgerData(String officeName, String month, String subHead)
			throws Exception {
		try {
			List<CashChittaTable> table = new ArrayList<CashChittaTable>();
			logger.info(subHead);
			table.addAll(cashReceiptVoucherService.getVouchersByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month)
							&& item.getContraEntry().equals("No")
							&& (item.getSubHead().equals(subHead) || subHead.isEmpty()))
					.map(item -> new CashChittaTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
							item.getSubHead(), item.getRemarks(), item.getReceivedAmount(), null, "CDB"))
					.collect(Collectors.toList()));

			table.addAll(adjustmentReceiptVoucherService.getVoucherByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month)
							&& item.getContraEntry().equals("No")
							&& (item.getSubHead().equals(subHead) || subHead.isEmpty()))
					.map(item -> new CashChittaTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
							item.getSubHead(), item.getNarration(), item.getReceivedAmount(), null, "BDB"))
					.collect(Collectors.toList()));

			table.addAll(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month)
							&& item.getContraEntry().equals("No")
							&& (item.getSubHead().equals(subHead) || subHead.isEmpty())
							&& item.getPvType().equals("Cash Payment Voucher"))
					.map(item -> new CashChittaTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
							item.getSubHead(), item.getNarration(), null, item.getAmount(), "CDB"))
					.collect(Collectors.toList()));

			table.addAll(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month)
							&& item.getContraEntry().equals("No")
							&& (item.getSubHead().equals(subHead) || subHead.isEmpty())
							&& !item.getPvType().equals("Cash Payment Voucher"))
					.map(item -> new CashChittaTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
							item.getSubHead(), item.getNarration(), null, item.getAmount(), "BDB"))
					.collect(Collectors.toList()));

			table.addAll(journalVoucherService.getJvByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getJvMonth().equals(month))
					.map(item -> {
						Double debit = item.getRows().stream()
								.filter(data -> data.getDrOrCr().equals("Dr")
										&& (data.getSubHead().equals(subHead) || subHead.isEmpty()))
								.mapToDouble(sum -> sum.getAmount()).sum();
						Double credit = item.getRows().stream()
								.filter(data -> data.getDrOrCr().equals("Cr")
										&& (data.getSubHead().equals(subHead) || subHead.isEmpty()))
								.mapToDouble(sum -> sum.getAmount()).sum();
						return new CashChittaTable(item.getVoucherNo(), item.getJvDate(),
								item.getRows().get(0).getMainHead(), item.getRows().get(0).getSubHead(),
								item.getNarration(), credit, debit, "JV");
					}).collect(Collectors.toList()));
			table.sort(Comparator.comparing(CashChittaTable::getDate));
			return table;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private MasterService masterService;

	@Autowired
	private InventryService inventryService;

	@Override
	public List<CashChittaTable> fetchGeneralLedgerData(String officeName, String month, String jwt) throws Exception {
		try {
			List<CashChittaTable> table = new ArrayList<CashChittaTable>();
			Set<String> mainHeadList = masterService.accountsMasterListHandler(jwt).stream()
					.map(item -> item.getMainHead()).collect(Collectors.toSet());

			List<CashReceiptVoucher> crvList = cashReceiptVoucherService.getVouchersByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("No")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month))
					.collect(Collectors.toList());

			List<AdjustmentReceiptVoucher> arvList = adjustmentReceiptVoucherService.getVoucherByOfficeName(officeName)
					.stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("No")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month))
					.collect(Collectors.toList());

			List<PaymentVoucher> pvList = paymentVoucherService.getVoucherByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("No")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month))
					.collect(Collectors.toList());

			List<JournalVoucher> jvList = journalVoucherService.getJvByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getJvMonth().equals(month))
					.collect(Collectors.toList());

			mainHeadList.forEach(mainHead -> {
				Double crv = crvList.stream().filter(item -> item.getMainHead().equals(mainHead))
						.mapToDouble(item -> item.getReceivedAmount()).sum();

				Double arv = arvList.stream().filter(item -> item.getMainHead().equals(mainHead))
						.mapToDouble(item -> item.getReceivedAmount()).sum();

				Double pv = pvList.stream().filter(item -> item.getMainHead().equals(mainHead))
						.mapToDouble(item -> item.getAmount()).sum();

				Double jvDebit = jvList.stream()
						.mapToDouble(item -> item.getRows().stream()
								.filter(data -> data.getDrOrCr().equals("Dr") && data.getMainHead().equals(mainHead))
								.mapToDouble(sum -> sum.getAmount()).sum())
						.sum();

				Double jvCredit = jvList.stream()
						.mapToDouble(item -> item.getRows().stream()
								.filter(data -> data.getDrOrCr().equals("Cr") && data.getMainHead().equals(mainHead))
								.mapToDouble(sum -> sum.getAmount()).sum())
						.sum();
				table.add(new CashChittaTable(null, null, mainHead, null, null, crv + arv + jvCredit, pv + jvDebit,
						null));
			});
			return table;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}


	@Autowired
	private SundryDrObRepo sundryDrObRepo;
	
	@Override
	public List<SundryDebtorsRegister> fetchSundryDebtorsData(String officeName, String month, String subHead,
			String ifmsId, String firmType, String jwt) throws Exception {
		try {
			logger.info(ifmsId);
			logger.info(firmType);
			logger.info(month);
			logger.info(subHead);
			List<Invoice> invoiceData = inventryService.getInvoiceDataByOfficenameHandler(officeName, jwt);

			List<SundryDebtorsRegister> data = new ArrayList<SundryDebtorsRegister>();
			String[] monthAndYr = month.split(" ");
			YearMonth yearMonth = YearMonth.of(Integer.valueOf(monthAndYr[1]), Month.valueOf(monthAndYr[0]));
			
			data.addAll(invoiceData.stream().filter(i -> {
				YearMonth yearMonthinvoice = YearMonth.from(i.getDate());
				return (ifmsId.isEmpty() || ifmsId.equals(i.getNameOfInstitution()))
						&& (yearMonthinvoice.equals(yearMonth) || yearMonthinvoice.isBefore(yearMonth));
			}).map(i -> {
				return new SundryDebtorsRegister(i.getDate(), "Sales invoice " + i.getInvoiceNo(),
						i.getNetInvoiceAdjustment(), 0.0);
			}).collect(Collectors.toList()));
			
			data.addAll(invoiceData.stream().filter(i -> i.getDateOfCollectionFromCcb() != null
					&& (ifmsId.isEmpty() || ifmsId.equals(i.getNameOfInstitution()))).flatMap(i -> {
						List<LocalDate> dates = i.getDateOfCollectionFromCcb();
						List<Double> amounts = i.getCollectionValue();
						return IntStream.range(0, Math.min(dates.size(), amounts.size())).filter(idx -> {
							YearMonth yearMonthinvoice = YearMonth.from(dates.get(idx));
							return yearMonthinvoice.equals(yearMonth) || yearMonthinvoice.isBefore(yearMonth);
						}).mapToObj(idx -> new SundryDebtorsRegister(dates.get(idx),
								"Collection invoice " + i.getInvoiceNo(), 0.0, amounts.get(idx)));
					}).collect(Collectors.toList()));
			
			List<SundryDrOb> sundryDrOb = sundryDrObRepo.findByOfficeName(officeName);
			data.addAll(sundryDrOb.stream().filter(i -> {
				YearMonth yearMonthinvoice = YearMonth.from(i.getInvoiceDate());
				return (ifmsId.isEmpty() || ifmsId.equals(i.getNameOfInstitution()))
						&& (yearMonthinvoice.equals(yearMonth) || yearMonthinvoice.isBefore(yearMonth));
			}).map(i -> {
				return new SundryDebtorsRegister(i.getInvoiceDate(), "Sales invoice " + i.getInvoiceNo(),
						i.getAmount(), 0.0);
			}).collect(Collectors.toList()));
			
			data.addAll(sundryDrOb.stream().filter(i -> i.getDateOfCollectionFromCcb() != null
					&& (ifmsId.isEmpty() || ifmsId.equals(i.getNameOfInstitution()))).flatMap(i -> {
						List<LocalDate> dates = i.getDateOfCollectionFromCcb();
						List<Double> amounts = i.getCollectionValue();
						return IntStream.range(0, Math.min(dates.size(), amounts.size())).filter(idx -> {
							YearMonth yearMonthinvoice = YearMonth.from(dates.get(idx));
							return yearMonthinvoice.equals(yearMonth) || yearMonthinvoice.isBefore(yearMonth);
						}).mapToObj(idx -> new SundryDebtorsRegister(dates.get(idx),
								"Collection invoice " + i.getInvoiceNo(), 0.0, amounts.get(idx)));
					}).collect(Collectors.toList()));
			return data;
//			List<CashChittaTable> list = new ArrayList<CashChittaTable>();
//			list.addAll(journalVoucherService.getJvByOfficeName(officeName).stream()
//					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getJvMonth().equals(month)
//							&& item.getJvFor().equals("Sales Jv") && item.getJvType().equals("net")
//							&& (item.getIfmsId().contains(ifmsId) || ifmsId.isEmpty())
//							&& validateFirmTypeByIfmsId(item.getIfmsId().get(0), firmType, jwt))
//					.map(item -> new CashChittaTable(item.getVoucherNo(), item.getJvDate(),
//							joinJvHead(item.getRows().stream()
//									.filter(data -> data.getDrOrCr().equals("Dr")
//											&& data.getMainHead().equals("H.O a/c - Sundry Debtors")
//											&& data.getSubHead().equals(subHead))
//									.map(data -> data.getMainHead()).collect(Collectors.toList())),
//							joinJvHead(item.getRows().stream()
//									.filter(data -> data.getDrOrCr().equals("Dr")
//											&& data.getMainHead().equals("H.O a/c - Sundry Debtors")
//											&& data.getSubHead().equals(subHead))
//									.map(data -> data.getSubHead()).collect(Collectors.toList())),
//							item.getNarration(), null,
//							item.getRows().stream()
//									.filter(data -> data.getDrOrCr().equals("Dr")
//											&& data.getMainHead().equals("H.O a/c - Sundry Debtors")
//											&& data.getSubHead().equals(subHead))
//									.mapToDouble(sum -> sum.getAmount()).sum(),
//							"JV"))
//					.collect(Collectors.toList()));
//
//			list.addAll(adjustmentReceiptVoucherService.getVoucherByOfficeName(officeName).stream()
//					.filter(item -> item.getVoucherStatus().equals("Approved")
//							&& (item.getVoucherFor().equals("Non-CC Invoice") || item.getVoucherFor().equals("ICM"))
//							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
//									.equals(month)
//							&& item.getMainHead().equals("H.O a/c - Sundry Debtors")
//							&& item.getSubHead().equals(subHead)
//							&& ifmsId.isEmpty()
//							&& (validateFirmTypeByIfmsId(null, firmType, jwt) || firmType.isEmpty()))
//					.map(item -> new CashChittaTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
//							item.getSubHead(), item.getNarration(), item.getReceivedAmount(), null, "ICM ADJ"))
//					.collect(Collectors.toList()));
//
//			list.addAll(sundryDebtorsAndCreditorsService.fetchReconciliationEntriesByOfficeName(officeName).stream()
//					.filter(item -> item.getFormType().equals("Dr") && item.getVoucherStatus().equals("Approved")
//							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
//									.equals(month)
//							&& (item.getIfmsId().equals(ifmsId) || ifmsId.isEmpty())
//							&& item.getMainHead().equals("H.O a/c - Sundry Debtors")
//							&& item.getSubHead().equals(subHead)
//							&& validateFirmTypeByIfmsId(item.getIfmsId(), firmType, jwt))
//					.map(item -> {
//						double credit = 0.0, debit = 0.0;
//						if (item.getIdNo().startsWith("DN")) {
//							debit = item.getAmount();
//						} else {
//							credit = item.getAmount();
//						}
//						return new CashChittaTable(item.getIdNo(), item.getDate(), item.getMainHead(),
//								item.getSubHead(), item.getRemarks(), credit, debit, "RECONCILIATION");
//					}).collect(Collectors.toList()));
//			return list;

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

//	private Boolean validateFirmTypeByIfmsId(String ifmsId, String firmType, String jwt) {
//		try {
//			BuyerFirmInfo buyerFirmInfo = masterService.getBuyerFirmByFirmNameHandler(jwt, ifmsId);
//			return buyerFirmInfo.getFirmType().equals(firmType) ? true : false;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

	private String joinJvHead(List<String> head) {
		return String.join(", ", head);
	}

	@Override
	public List<CashChittaTable> fetchSundryCreditorsData(String officeName, String month, String subHead,
			String supplierName) throws Exception {
		try {
			List<CashChittaTable> list = new ArrayList<CashChittaTable>();
			list.addAll(journalVoucherService.getJvByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getJvMonth().equals(month)
							&& item.getJvFor().equals("Purchase JV") && item.getJvType()
									.equals("net")
							&& (supplierName.isEmpty() || supplierName.equals(item.getSupplierName())))
					.map(item -> new CashChittaTable(item.getVoucherNo(), item.getJvDate(),
							joinJvHead(item.getRows().stream()
									.filter(data -> data.getDrOrCr().equals("Cr") && data.getSubHead().equals(subHead)
											&& data.getMainHead().equals("H.O a/c - Sundry Creditors"))
									.map(data -> data.getMainHead()).collect(Collectors.toList())),
							joinJvHead(item.getRows().stream()
									.filter(data -> data.getDrOrCr().equals("Cr") && data.getSubHead().equals(subHead)
											&& data.getMainHead().equals("H.O a/c - Sundry Creditors"))
									.map(data -> data.getSubHead()).collect(Collectors.toList())),
							item.getNarration(),
							item.getRows().stream()
									.filter(data -> data.getDrOrCr().equals("Cr") && data.getSubHead().equals(subHead)
											&& data.getMainHead().equals("H.O a/c - Sundry Creditors"))
									.mapToDouble(sum -> sum.getAmount()).sum(),
							null, "JV"))
					.collect(Collectors.toList()));

			list.addAll(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("No")
							&& item.getVoucherFor().equals("CheckMemoGoods") && item.getSubHead().equals(subHead)
							&& item.getMainHead().equals("H.O a/c - Sundry Creditors")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month)
							&& (supplierName.isEmpty() || supplierName.equals(item.getPaidTo())))
					.map(item -> new CashChittaTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
							item.getSubHead(), item.getNarration(), null, item.getAmount(), "PV"))
					.collect(Collectors.toList()));

			list.addAll(sundryDebtorsAndCreditorsService.fetchReconciliationEntriesByOfficeName(officeName).stream()
					.filter(item -> item.getFormType().equals("Cr") && item.getVoucherStatus().equals("Approved")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month)
							&& item.getMainHead().equals("H.O a/c - Sundry Creditors")
							&& item.getSubHead().equals(subHead) && item.getSubHead().equals(subHead)
							&& (supplierName.isEmpty() || supplierName.equals(item.getNameOfInstitution())))
					.map(item -> {
						double credit = 0.0, debit = 0.0;
						if (item.getIdNo().startsWith("DN")) {
							credit = item.getAmount();
						} else if (item.getIdNo().startsWith("CN")) {
							debit = item.getAmount();
						}
						return new CashChittaTable(item.getIdNo(), item.getDate(), item.getMainHead(),
								item.getSubHead(), item.getRemarks(), credit, debit, "RECONCILIATION");
					}).collect(Collectors.toList()));
			return list;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private SupplierAdvanceService supplierAdvanceService;

	@Override
	public List<CashChittaTable> fetchSupplierAdvanceData(String month, String supplierName) throws Exception {
		try {
			List<CashChittaTable> list = new ArrayList<CashChittaTable>();
			supplierAdvanceService.fetchOutstandingAdvancesByProduct("").stream()
					.filter(item -> String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
							.equals(month) && (supplierName.isEmpty() || supplierName.equals(item.getSupplierName())))
					.map(item -> {
						double credit = 0.0, debit = 0.0;
						debit = item.getPv().getAmount();
						credit = item.getNetAdvanceValueAfterOthers() - item.getAvlAmountForCheckMemo();
						return new CashChittaTable(item.getSupplierAdvanceNo(), item.getDate(), null, null, null,
								credit, debit, "SUPPLIERADVANCE");
					}).collect(Collectors.toList());
			return list;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<CollectionRegisterTable> fetchChequeCollectionData(String month, String officeName) throws Exception {
		try {
			return adjustmentReceiptVoucherService.getVoucherByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getReceiptMode().equals("Cheque")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month))
					.map(item -> new CollectionRegisterTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
							item.getSubHead(), item.getReceivedAmount(), item.getReceivedFrom(), null, null, null, null,
							item.getAccountType(), item.getBranchName(), item.getAccountNo(), item.getDepositDate(),
							item.getDateOfCollection(), item.getBankCharges()))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<CollectionRegisterTable> fetchChequeIssueData(String month, String officeName) throws Exception {
		try {
			return paymentVoucherService.getVoucherByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("No")
							&& item.getPvType().equals("Cheque Payment Voucher")
							&& String.format("%s%s%04d", item.getDate().getMonth(), " ", item.getDate().getYear())
									.equals(month))
					.map(item -> new CollectionRegisterTable(item.getVoucherNo(), item.getDate(), item.getMainHead(),
							item.getSubHead(), item.getAmount(), null, item.getPaidTo(), item.getChequeNumber(),
							item.getChequeDate(), item.getIssueBankName(), null, null, null, null, null, null))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
