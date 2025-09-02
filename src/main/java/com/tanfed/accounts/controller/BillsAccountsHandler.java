package com.tanfed.accounts.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tanfed.accounts.entity.*;
import com.tanfed.accounts.model.*;
import com.tanfed.accounts.repository.ClosingBalanceRepo;
import com.tanfed.accounts.repository.OpeningBalanceRepo;
import com.tanfed.accounts.response.*;
import com.tanfed.accounts.service.*;

@RestController
@RequestMapping("/api/billsaccounts")
public class BillsAccountsHandler {

	@Autowired
	private SupplierAdvanceService supplierAdvanceService;

	@Autowired
	private OpeningBalanceService openingBalanceService;

	@Autowired
	private SundryDebtorsAndCreditorsService sundryDebtorsAndCreditorsService;

	@Autowired
	private FilteredAccountsDataService filteredAccountsDataService;

	@Autowired
	private VoucherApprovalService voucherApprovalService;

//	private static Logger logger = LoggerFactory.getLogger(BillsAccountsHandler.class);

	@PostMapping("/savesupplieradvance")
	public ResponseEntity<String> saveSupplierAdvanceHadnler(@RequestBody SupplierAdvance obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return supplierAdvanceService.saveSupplierAdvance(obj, jwt);
	}

	@PostMapping("/saveifmsvouchers")
	public ResponseEntity<String> updateIfmsIdAccVouchersHandler(@RequestBody ReconciliationEntry obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return sundryDebtorsAndCreditorsService.saveSundryDebtorsAndCreditors(obj, jwt);
	}

	@PostMapping("/savesdrob")
	public ResponseEntity<String> saveSundryDrObHandler(@RequestBody List<SundryDrOb> obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return openingBalanceService.saveSundryDrOb(obj, jwt);
	}

	@PostMapping("/savescrob")
	public ResponseEntity<String> saveSundryCrObHandler(@RequestBody List<SundryCrOb> obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return openingBalanceService.saveSundryCrOb(obj, jwt);
	}

	@PostMapping("/savebillgstob")
	public ResponseEntity<String> saveBillsGstObHandler(@RequestBody BillsGstOb entity,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return openingBalanceService.saveBillsGstOpeningBalance(entity, jwt);
	}

	@GetMapping("/fetchdataforsupplieradvance")
	public DataForSupplierAdvance getDataForSupplierAdvanceHandler(@RequestParam String officeName,
			@RequestParam String activity, @RequestParam String supplierName, @RequestParam String productName,
			@RequestParam String termsMonth, @RequestParam String termsNo, @RequestParam String advanceMonth,
			@RequestHeader("Authorization") String jwt,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
			throws Exception {
		return supplierAdvanceService.getDataForSupplierAdvance(officeName, activity, supplierName, productName,
				termsMonth, termsNo, advanceMonth, jwt, date);
	}

	@GetMapping("/fetchadvancesbyproduct")
	public List<SupplierAdvance> fetchOutstandingAdvancesByProductHandler(@RequestParam String productName)
			throws Exception {
		return supplierAdvanceService.fetchOutstandingAdvancesByProduct(productName);
	}

	@GetMapping("/billsaccountsfilterdata")
	public Vouchers getBillsAccountsFilteredDataHandler(@RequestParam String formType, @RequestParam String officeName,
			@RequestParam String voucherStatus,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return filteredAccountsDataService.getBillsAccountsFilteredData(formType, fromDate, toDate, officeName,
				voucherStatus, jwt);
	}

	@GetMapping("/fetchdataforsundrybills")
	public DataForSundryDebtor getDataForSundryBillsHandler(@RequestHeader("Authorization") String jwt,
			@RequestParam(required = false) String ifmsId, @RequestParam(required = false) String idNo,
			@RequestParam(required = false) String formType, @RequestParam String officeName,
			@RequestParam(required = false) String month) throws Exception {
		return sundryDebtorsAndCreditorsService.getDataForSundryBills(jwt, ifmsId, idNo, officeName, month, formType);
	}

	@Autowired
	private RegisterService registerService;

	@Autowired
	private ClosingBalanceRepo closingBalanceRepo;

	@Autowired
	private OpeningBalanceRepo openingBalanceRepo;

	@GetMapping("/fetchaccregisters/{formType}")
	public Registers getAccountsRegistersHandler(@PathVariable String formType, @RequestParam String officeName,
			@RequestParam String month, @RequestHeader("Authorization") String jwt, @RequestParam String subHead,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate)
			throws Exception {
		Registers data = new Registers();
		switch (formType) {
		case "cashChitta": {
			List<CashChittaTable> cashChittaData = registerService.fetchCashChittaData(officeName, fromDate, toDate);
			data.setCashChitta(cashChittaData);
			Double ob = 0.0;
			if (fromDate.equals(LocalDate.of(2025, 4, 1))) {
				List<OpeningBalance> openingBalance = openingBalanceRepo
						.findByOfficeNameAndOpeningBalanceFor(officeName, "Cash").stream()
						.filter(item -> item.getCashBalanceDate().equals(fromDate)).collect(Collectors.toList());

				if (!openingBalance.isEmpty()) {
					ob = openingBalance.get(0).getAmount();
				} else {
					throw new RuntimeException("Opening balance not found for " + fromDate);
				}
			} else {
				List<ClosingBalanceTable> obData;
				int n = 1;
				do {
					LocalDate previousDate = fromDate.minusDays(n++);
					obData = closingBalanceRepo.findByOfficeNameAndDate(officeName, previousDate).stream()
							.filter(item -> item.getCashBalance() != null).collect(Collectors.toList());
				} while (obData.isEmpty());
				ob = obData.get(0).getCashBalance();
			}
			data.setOb(ob);
			return data;
		}
		case "cashDayBook": {
			List<CashDayBookTable> cashDayBookData = registerService.fetchCashDayBookData(officeName, month);
			data.setCashDayBook(cashDayBookData);

			if (!month.isEmpty()) {
				String[] monthAndYr = month.split(" ");
				YearMonth yearMonth = YearMonth.of(Integer.valueOf(monthAndYr[1]), Month.valueOf(monthAndYr[0]));

				Double ob = 0.0;
				LocalDate date = yearMonth.atDay(1);
				if (date.equals(LocalDate.of(2025, 4, 1))) {
					List<OpeningBalance> openingBalance = openingBalanceRepo
							.findByOfficeNameAndOpeningBalanceFor(officeName, "Cash").stream()
							.filter(item -> item.getCashBalanceDate().equals(date)).collect(Collectors.toList());

					if (!openingBalance.isEmpty()) {
						ob = openingBalance.get(0).getAmount();
					} else {
						throw new RuntimeException("Opening balance not found for " + date);
					}
				} else {
					List<ClosingBalanceTable> obData;
					int n = 1;
					do {
						LocalDate previousDate = date.minusDays(n++);
						obData = closingBalanceRepo.findByOfficeNameAndDate(officeName, previousDate).stream()
								.filter(item -> item.getCashBalance() != null).collect(Collectors.toList());
					} while (obData.isEmpty());
					ob = obData.get(0).getCashBalance();
				}
				data.setOb(ob);
			}
			return data;
		}
		case "bankDayBook": {
			List<CashDayBookTable> bankDayBookData = registerService.fetchBankDayBookData(officeName, jwt, month);
			data.setBankDayBook(bankDayBookData);
			if (!month.isEmpty()) {
				String[] monthAndYr = month.split(" ");
				YearMonth yearMonth = YearMonth.of(Integer.valueOf(monthAndYr[1]), Month.valueOf(monthAndYr[0]));

				Double obSa = 0.0;
				Double obCa = 0.0;
				Double obNpa = 0.0;
				LocalDate date = yearMonth.atDay(1);
				if (date.equals(LocalDate.of(2025, 4, 1))) {
					List<OpeningBalance> openingBalance = openingBalanceRepo
							.findByOfficeNameAndOpeningBalanceFor(officeName, "Bank").stream()
							.filter(item -> item.getBankBalanceDate().equals(date)).collect(Collectors.toList());
					if (!openingBalance.isEmpty()) {
						obCa = openingBalance.stream().filter(item -> item.getAccountType().equals("Current A/c"))
								.mapToDouble(OpeningBalance::getDayBookAmount).sum();
						obNpa = openingBalance.stream().filter(item -> item.getAccountType().equals("Non PDS A/c"))
								.mapToDouble(OpeningBalance::getDayBookAmount).sum();
						obSa = openingBalance.stream().filter(item -> item.getAccountType().equals("Savings A/c"))
								.mapToDouble(OpeningBalance::getDayBookAmount).sum();
					} else {
						throw new RuntimeException("Opening balance not found for " + date);
					}
				} else {
					List<ClosingBalanceTable> obData;
					int n = 1;
					do {
						LocalDate previousDate = date.minusDays(n++);
						obData = closingBalanceRepo.findByOfficeNameAndDate(officeName, previousDate).stream()
								.filter(item -> item.getBankBalance() != null).collect(Collectors.toList());
					} while (obData.isEmpty());

					for (var item : obData) {
						switch (item.getAccType()) {
						case "Savings A/c" -> obSa = item.getBankBalance();
						case "Current A/c" -> obCa = item.getBankBalance();
						case "Non PDS A/c" -> obNpa = item.getBankBalance();
						}
					}
				}
				data.setObSa(obSa);
				data.setObCa(obCa);
				data.setObNpa(obNpa);
			}
			return data;
		}
		case "journalRegister": {
			List<JournalRegisterTable> journalRegisterData = registerService.fetchJournalRegisterData(officeName,
					month);
			data.setJournalRegister(journalRegisterData);
			return data;
		}
		case "subsidyLedger": {
			List<CashChittaTable> subsidyLedgerData = registerService.fetchSubsidyLedgerData(officeName, month,
					subHead);
			data.setSubsidyLedger(subsidyLedgerData);
			return data;
		}
		case "generalLedger": {
			List<CashChittaTable> generalLedgerData = registerService.fetchGeneralLedgerData(officeName, month, jwt);
			data.setGeneralLedger(generalLedgerData);
			if (!month.isEmpty()) {
				String[] monthAndYr = month.split(" ");
				YearMonth yearMonth = YearMonth.of(Integer.valueOf(monthAndYr[1]), Month.valueOf(monthAndYr[0]));

				Double ob = 0.0;
				LocalDate date = yearMonth.atDay(1);
				if (date.equals(LocalDate.of(2025, 4, 1))) {
					List<OpeningBalance> openingBalance = openingBalanceRepo.findByOfficeName(officeName).stream()
							.filter(item -> date.equals(item.getBankBalanceDate())
									|| date.equals(item.getCashBalanceDate()))
							.collect(Collectors.toList());
					if (!openingBalance.isEmpty()) {
						ob = openingBalance.stream().filter(item -> item.getDayBookAmount() != null)
								.mapToDouble(OpeningBalance::getDayBookAmount).sum();
						ob += openingBalance.stream().filter(item -> item.getAmount() != null)
								.mapToDouble(OpeningBalance::getAmount).sum();
					} else {
						throw new RuntimeException("Opening balance not found for " + date);
					}
				} else {
					List<ClosingBalanceTable> obData;
					int n = 1;
					do {
						LocalDate previousDate = date.minusDays(n++);
						obData = closingBalanceRepo.findByOfficeNameAndDate(officeName, previousDate).stream()
								.collect(Collectors.toList());
					} while (obData.isEmpty());
					ob = obData.stream().mapToDouble(item -> item.getBankBalance()).sum();
					ob += obData.stream().mapToDouble(item -> item.getCashBalance()).sum();
				}
				data.setOb(ob);
			}
			return data;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + formType);
		}
	}

	@Autowired
	private MasterService masterService;

	@GetMapping("/fetchbillsregisters/{formType}")
	public Registers getBillsRegistersHandler(@PathVariable String formType,
			@RequestParam(required = false) String officeName, @RequestParam(required = false) String district,
			@RequestParam(required = false) String month, @RequestHeader("Authorization") String jwt,
			@RequestParam(required = false) String subHead, @RequestParam(required = false) String ifmsId,
			@RequestParam(required = false) String firmType,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate)
			throws Exception {
		Registers data = new Registers();
		switch (formType) {
		case "sundryDebitorsRegister": {
			List<String> subHeadList = masterService.accountsMasterListHandler(jwt).stream()
					.filter(item -> item.getMainHead().equals("H.O a/c - Sundry Debtors"))
					.map(item -> item.getSubHead()).collect(Collectors.toList());
			List<BuyerFirmInfo> buyerData = masterService.getBuyerDataByOfficeNameHandler(officeName, jwt);
			data.setSubHeadList(subHeadList);
			data.setDistrictList(buyerData.stream().map(item -> item.getDistrict()).collect(Collectors.toSet()));
			data.setIfmsIdList(buyerData.stream().filter(item -> item.getDistrict().equals(district))
					.map(item -> item.getIfmsIdNo()).collect(Collectors.toList()));
			List<CashChittaTable> fetchSundryDebitorsData = registerService.fetchSundryDebtorsData(officeName, month,
					subHead, ifmsId, firmType);
			data.setSundryDebitorsRegister(fetchSundryDebitorsData);
			data.setOb(sundryDebtorsAndCreditorsService.calculateSDrObValue(month, subHead, officeName));
			return data;
		}
		case "sundryCreditorsRegister": {
			List<String> subHeadList = masterService.accountsMasterListHandler(jwt).stream()
					.filter(item -> item.getMainHead().equals("H.O a/c - Sundry Creditors"))
					.map(item -> item.getSubHead()).collect(Collectors.toList());
			data.setSubHeadList(subHeadList);
			data.setNameList(masterService.getProductDataHandler(jwt).stream().map(item -> item.getSupplierName())
					.collect(Collectors.toList()));
			List<CashChittaTable> fetchSundryCreditorsData = registerService.fetchSundryCreditorsData(officeName, month,
					subHead);
			data.setSundryCreditorsRegister(fetchSundryCreditorsData);
			data.setOb(sundryDebtorsAndCreditorsService.calculateSCrObValue(month, subHead, officeName));
			return data;
		}
		case "supplierAdvanceRegister": {
			List<CashChittaTable> fetchSupplierAdvanceData = registerService.fetchSupplierAdvanceData(month);
			data.setSupplierAdvanceRegister(fetchSupplierAdvanceData);
			return data;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + formType);
		}
	}

	@PutMapping("/updatepvandjv/{supplierAdvanceNo}")
	public ResponseEntity<String> updatePvAndJvHandler(@PathVariable String supplierAdvanceNo,
			@RequestBody(required = false) JvAndPvObj obj, @RequestHeader("Authorization") String jwt)
			throws Exception {
		return supplierAdvanceService.updatePvAndJv(obj, supplierAdvanceNo, jwt);
	}

	@PutMapping("/billsaccountsvoucherApproval")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ACCADMIN', 'ROLE_ROADMIN')")
	public ResponseEntity<String> billsAccountsVoucherApprovalHandler(@RequestBody VoucherApproval obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		String updatedStatus = voucherApprovalService.updateBillsAccountsVoucherApproval(obj, jwt);
		return new ResponseEntity<String>(updatedStatus, HttpStatus.ACCEPTED);
	}

	@PutMapping("/updatesupplieradvance/{supplierAdvanceNo}")
	public void updateAvlQtyAndAmountHandler(@PathVariable String supplierAdvanceNo, @RequestParam double qty,
			@RequestParam double amount) throws Exception {
		supplierAdvanceService.updateAvlQtyAndAmount(supplierAdvanceNo, qty, amount);
	}

	@GetMapping("/sundryob/validate/{officeName}")
	public String sundryObValidateHandler(@PathVariable String officeName) throws Exception {
		return openingBalanceService.billsAccObValidate(officeName);
	}

}
