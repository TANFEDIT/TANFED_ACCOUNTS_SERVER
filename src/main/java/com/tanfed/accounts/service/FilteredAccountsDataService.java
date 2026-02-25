package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.components.MasterDataManager;
import com.tanfed.accounts.dto.ContraEntryViewDto;
import com.tanfed.accounts.entity.*;
import com.tanfed.accounts.model.AccountsMaster;
import com.tanfed.accounts.model.BeneficiaryMaster;
import com.tanfed.accounts.model.TaxInfo;
import com.tanfed.accounts.model.Vouchers;
import com.tanfed.accounts.repository.*;

@Service
public class FilteredAccountsDataService {

	@Autowired
	private OpeningBalanceRepo openingBalanceRepo;

	@Autowired
	private BrsRepo brsRepo;

	@Autowired
	private ContraVoucherService contraVoucherService;

	@Autowired
	private CashReceiptRepo cashReceiptRepo;

	@Autowired
	private AdjustmentReceiptVoucherRepo adjustmentReceiptVoucherRepo;

	@Autowired
	private PaymentVoucherRepo paymentVoucherRepo;

	@Autowired
	private JournalVoucherRepo journalVoucherRepo;

	@Autowired
	private SupplierAdvanceRepo supplierAdvanceRepo;

	@Autowired
	private MasterDataManager masterService;

	@Autowired
	private DebitOrCreditNoteRepo debitOrCreditNoteRepo;

	private static Logger logger = LoggerFactory.getLogger(FilteredAccountsDataService.class);

	public Vouchers getFilteredData(String formType, LocalDate fromDate, LocalDate toDate, String officeName,
			String voucherStatus, String voucherNo, String jwt) throws Exception {
		Vouchers data = new Vouchers();
		logger.info(formType);

		switch (formType) {
		case "accountsMaster": {
			List<AccountsMaster> accountsMasterListHandler = masterService.fetchAccMasterData(jwt);
			data.setAccountsMaster(accountsMasterListHandler);
			return data;
		}
		case "taxInfo": {
			List<TaxInfo> taxInfoListHandler = masterService.fetchTaxInfoData(jwt);
			data.setTaxInfo(taxInfoListHandler);
			return data;
		}
		case "beneficiaryMaster": {
			List<BeneficiaryMaster> beneficiaryListByOfficeName = masterService.fetchBeneficiaryMasterData(jwt).stream()
					.filter(i -> i.getOfficeName().equals(officeName)).collect(Collectors.toList());
			beneficiaryListByOfficeName.sort(Comparator.comparing(BeneficiaryMaster::getId).reversed());
			data.setBeneficiaryMaster(beneficiaryListByOfficeName);
			return data;
		}
		case "openingBalance": {

			List<OpeningBalance> obData = openingBalanceRepo.findByOfficeName(officeName);
			List<OpeningBalance> obFiltered = obData.stream()
					.filter(i -> !i.getVoucherStatus().equals("Rejected")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getOpDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getOpDate().isAfter(toDate))))
					.collect(Collectors.toList());
			obFiltered.sort(Comparator.comparing(OpeningBalance::getId).reversed());
			data.setOpeningBalance(obFiltered);
			return data;
		}
		case "cashReceiptVoucher": {
			List<CashReceiptVoucher> crvData = cashReceiptRepo.findByOfficeName(officeName);
			List<CashReceiptVoucher> crvFiltered = crvData.stream()
					.filter(i -> ((!i.getVoucherStatus().equals("Rejected")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getDate().isAfter(toDate))))
							&& (voucherNo.equals(i.getVoucherNo()) || voucherNo.isEmpty())))
					.collect(Collectors.toList());
			crvFiltered.sort(Comparator.comparing(CashReceiptVoucher::getId).reversed());
			data.setCashReceiptVoucher(crvFiltered);
			return data;
		}
		case "adjustmentReceiptVoucher": {
			List<AdjustmentReceiptVoucher> adjData = adjustmentReceiptVoucherRepo.findByOfficeName(officeName);
			List<AdjustmentReceiptVoucher> adjFfiltered = adjData.stream()
					.filter(i -> ((!i.getVoucherStatus().equals("Rejected")
							&& i.getVoucherFor().equals("Adjustment Receipt")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getDate().isAfter(toDate))))
							&& (voucherNo.equals(i.getVoucherNo()) || voucherNo.isEmpty())))
					.collect(Collectors.toList());
			adjFfiltered.sort(Comparator.comparing(AdjustmentReceiptVoucher::getId).reversed());
			data.setAdjustmentReceiptVoucher(adjFfiltered);
			return data;
		}
		case "paymentVoucher": {
			List<PaymentVoucher> pvData = paymentVoucherRepo.findByOfficeName(officeName);
			List<PaymentVoucher> pvFiltered = pvData.stream()
					.filter(i -> ((!i.getVoucherStatus().equals("Rejected")
							&& i.getVoucherFor().equals("Payment Voucher")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getDate().isAfter(toDate))))
							&& (voucherNo.equals(i.getVoucherNo()) || voucherNo.isEmpty())))
					.collect(Collectors.toList());
			pvFiltered.sort(Comparator.comparing(PaymentVoucher::getId).reversed());
			data.setPaymentVoucher(pvFiltered);
			return data;
		}
		case "contraEntry": {
			List<ContraEntryViewDto> mappedContra = fetchMappedContra(officeName);
			List<ContraEntryViewDto> contraFiltered = mappedContra.stream()
					.filter(i -> ((fromDate != null && !i.getDate().isBefore(fromDate)) || fromDate == null)
							&& ((toDate != null && !i.getDate().isBefore(toDate)) || toDate == null)
							&& (i.getVoucherStatus().equals(voucherStatus) || voucherStatus.isEmpty()))
					.collect(Collectors.toList());
			data.setContraEntry(contraFiltered);
			return data;
		}
		case "journalVoucher": {
			List<JournalVoucher> jvData = journalVoucherRepo.findByOfficeName(officeName);
			List<JournalVoucher> jvFiltered = jvData.stream()
					.filter(i -> ((!i.getVoucherStatus().equals("Rejected") && i.getJvFor().equals("Journal Voucher")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getJvDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getJvDate().isAfter(toDate))))
							&& (voucherNo.equals(i.getVoucherNo()) || voucherNo.isEmpty())))
					.collect(Collectors.toList());
			jvFiltered.sort(Comparator.comparing(JournalVoucher::getId).reversed());
			data.setJournalVoucher(jvFiltered);
			return data;
		}
		case "brs": {
			List<BRS> brsData = brsRepo.findByOfficeName(officeName);
			List<BRS> brsFiltered = brsData.stream()
					.filter(i -> !i.getVoucherStatus().equals("Rejected")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getReconciliationDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getReconciliationDate().isAfter(toDate))))
					.map(i -> mapBrsData(i)).collect(Collectors.toList());
			brsFiltered.sort(Comparator.comparing(BRS::getId).reversed());
			data.setBrs(brsFiltered);
			return data;

		}
		case "drCrNote": {
			List<DebitOrCreditNote> drCrNote = debitOrCreditNoteRepo.findByOfficeName(officeName);
			List<DebitOrCreditNote> drCrFiltered = drCrNote.stream()
					.filter(i -> ((!i.getVoucherStatus().equals("Rejected")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getDate().isAfter(toDate))))
							&& (voucherNo.equals(i.getDrCrNo()) || voucherNo.isEmpty())))
					.collect(Collectors.toList());
			drCrFiltered.sort(Comparator.comparing(DebitOrCreditNote::getId).reversed());
			data.setDrCrNote(drCrFiltered);
			return data;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + formType);
		}

	}

	private List<ContraEntryViewDto> fetchMappedContra(String officeName) throws Exception {
		List<ContraEntryViewDto> contraEntry = new ArrayList<ContraEntryViewDto>();
		List<ContraEntry> contraEntryData = contraVoucherService.getContraByOfficeName(officeName);
		for (var contra : contraEntryData) {

			ContraEntryViewDto obj = new ContraEntryViewDto();
			obj.setDate(contra.getDate());
			PaymentVoucher pv = fetchContraPv(contra.getContraId());
			obj.setContraBetween((pv.getStatus() != null && pv.getStatus().equals("Amount Returned"))
					? contra.getContraBetween() + " Contra Reversed"
					: contra.getContraBetween());
			obj.setId(pv.getId());
			obj.setPaymentNo(pv.getVoucherNo());
			obj.setPaymentMainHead(pv.getMainHead());
			obj.setPaymentSubHead(pv.getSubHead());
			obj.setPaymentAmount(pv.getAmount());
			obj.setVoucherStatus(pv.getVoucherStatus());
			if (contra.getContraBetween().endsWith("Cash")) {
				CashReceiptVoucher crv = fetchContraCv(contra.getContraId());
				obj.setReceiptNo(crv.getVoucherNo());
				obj.setReceiptMainHead(crv.getMainHead());
				obj.setReceiptSubHead(crv.getSubHead());
				obj.setReceiptAmount(crv.getReceivedAmount());
			} else {
				AdjustmentReceiptVoucher arv = fetchContraArv(contra.getContraId());
				obj.setReceiptNo(arv == null ? null : arv.getVoucherNo());
				obj.setReceiptMainHead(arv == null ? null : arv.getMainHead());
				obj.setReceiptSubHead(arv == null ? null : arv.getSubHead());
				obj.setReceiptAmount(arv == null ? null : arv.getReceivedAmount());
			}
			if (contra.getContraBetween().startsWith("RO") || contra.getContraBetween().startsWith("HO")
					|| contra.getContraBetween().equals("Invoice Collection Transfer")) {
				obj.setToRegion(contra.getPaidTo());
			}
			contraEntry.add(obj);
		}
		return contraEntry;
	}

	private BRS mapBrsData(BRS brs) {
		List<BrsParticulars> daybookFiltered = brs.getDaybookTranscations().stream()
				.filter(i -> i.getParticularsType().equals("Daybook")).collect(Collectors.toList());
		List<BrsParticulars> passbookFiltered = brs.getPassbookTranscations().stream()
				.filter(i -> i.getParticularsType().equals("Passbook")).collect(Collectors.toList());
		brs.setDaybookTranscations(daybookFiltered);
		brs.setPassbookTranscations(passbookFiltered);
		return brs;
	}

	private PaymentVoucher fetchContraPv(String contraId) {
		return paymentVoucherRepo.findByContraId(contraId);
	}

	private AdjustmentReceiptVoucher fetchContraArv(String contraId) {
		return adjustmentReceiptVoucherRepo.findByContraId(contraId);
	}

	private CashReceiptVoucher fetchContraCv(String contraId) {
		return cashReceiptRepo.findByContraId(contraId);
	}

	@Autowired
	private BillsGstObRepo billsGstObRepo;

	@Autowired
	private SundryCrObRepo sundryCrObRepo;

	@Autowired
	private SundryDrObRepo sundryDrObRepo;

	@Autowired
	private ReconciliationEntryRepo reconciliationEntryRepo;

	public Vouchers getBillsAccountsFilteredData(String formType, LocalDate fromDate, LocalDate toDate,
			String officeName, String voucherStatus, String jwt) throws Exception {
		Vouchers data = new Vouchers();

		switch (formType) {
		case "supplierAdvance": {
			List<SupplierAdvance> sa = supplierAdvanceRepo.findAll();
			List<SupplierAdvance> saFiltered = sa.stream()
					.filter(i -> ((!i.getVoucherStatus().equals("Rejected")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getDate().isAfter(toDate))))))
					.collect(Collectors.toList());
			saFiltered.sort(Comparator.comparing(SupplierAdvance::getId).reversed());
			data.setSupplierAdvance(saFiltered);
			return data;
		}
		case "billsGstOb": {
			List<BillsGstOb> filteredLst = billsGstObRepo.findByOfficeName(officeName).stream()
					.filter(i -> ((!i.getVoucherStatus().equals("Rejected")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getDate().isAfter(toDate))))))
					.collect(Collectors.toList());
			filteredLst.sort(Comparator.comparing(BillsGstOb::getId).reversed());
			data.setBillsGstOb(filteredLst);
			return data;
		}
		case "sundryCrOb": {
			List<SundryCrOb> filteredLst = sundryCrObRepo.findByOfficeName(officeName).stream()
					.filter(i -> ((!i.getVoucherStatus().equals("Rejected")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getInvoiceDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getInvoiceDate().isAfter(toDate))))))
					.collect(Collectors.toList());
			filteredLst.sort(Comparator.comparing(SundryCrOb::getId).reversed());
			data.setSundryCrOb(filteredLst);
			return data;
		}
		case "sundryDrOb": {
			List<SundryDrOb> filteredLst = sundryDrObRepo.findByOfficeName(officeName).stream()
					.filter(i -> ((!i.getVoucherStatus().equals("Rejected")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getInvoiceDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getInvoiceDate().isAfter(toDate))))))
					.collect(Collectors.toList());
			filteredLst.sort(Comparator.comparing(SundryDrOb::getId).reversed());
			data.setSundryDrOb(filteredLst);
			return data;
		}
		case "reconciliationEntry": {
			List<ReconciliationEntry> filteredLst = reconciliationEntryRepo.findByOfficeName(officeName).stream()
					.filter(i -> ((!i.getVoucherStatus().equals("Rejected")
							&& (voucherStatus.isEmpty() || i.getVoucherStatus().equals(voucherStatus))
							&& (fromDate == null || (fromDate != null && !i.getDate().isBefore(fromDate)))
							&& (toDate == null || (toDate != null && !i.getDate().isAfter(toDate))))))
					.collect(Collectors.toList());
			filteredLst.sort(Comparator.comparing(ReconciliationEntry::getId).reversed());
			data.setReconciliationEntry(filteredLst);
			return data;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + formType);
		}
	}

}
