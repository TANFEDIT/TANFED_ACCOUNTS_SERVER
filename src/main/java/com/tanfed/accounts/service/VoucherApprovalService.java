package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.entity.*;
import com.tanfed.accounts.model.VoucherApproval;
import com.tanfed.accounts.repository.*;

@Service
public class VoucherApprovalService {

	@Autowired
	private OpeningBalanceService openingBalanceService;

	@Autowired
	private OpeningBalanceRepo openingBalanceRepo;

	@Autowired
	private CashReceiptRepo cashReceiptRepo;

	@Autowired
	private CashReceiptVoucherService cashReceiptVoucherService;

	@Autowired
	private AdjustmentReceiptVoucherRepo adjustmentReceiptVoucherRepo;

	@Autowired
	private AdjustmentReceiptVoucherService adjustmentReceiptVoucherService;

	@Autowired
	private PaymentVoucherRepo paymentVoucherRepo;

	@Autowired
	private PaymentVoucherService paymentVoucherService;

	@Autowired
	private JournalVoucherRepo journalVoucherRepo;

	@Autowired
	private SupplierAdvanceRepo supplierAdvanceRepo;

	@Autowired
	private SupplierAdvanceService supplierAdvanceService;

	@Autowired
	private DebitOrCreditNoteRepo debitOrCreditNoteRepo;

	@Autowired
	private SundryDebtorsAndCreditorsService sundryDebtorsAndCreditorsService;

	@Autowired
	private InventryService inventryService;

	@Autowired
	private UserService userService;

	private Logger logger = LoggerFactory.getLogger(VoucherApprovalService.class);

	public String updateVoucherApproval(VoucherApproval obj, String jwt) throws Exception {
		String designation = null;
		List<String> oldDesignation = null;

		String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
		logger.info("{}", obj);

		switch (obj.getFormType()) {
		case "openingBalance": {
			OpeningBalance ob = openingBalanceRepo.findById(Long.valueOf(obj.getId())).orElse(null);

			designation = userService.getNewDesignation(empId);
			oldDesignation = ob.getDesignation();

			ob.setVoucherStatus(obj.getVoucherStatus());
			ob.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				ob.setApprovedDate(LocalDate.now());
				if (ob.getOpeningBalanceFor().equals("Bank")) {
					openingBalanceService.updateClosingBalance(ob);
				}
			}
			if (oldDesignation == null) {
				ob.setDesignation(Arrays.asList(designation));
			} else {
				ob.getDesignation().add(designation);
			}
			openingBalanceRepo.save(ob);
			return designation;
		}

		case "cashReceiptVoucher": {
			CashReceiptVoucher crv = cashReceiptRepo.findById(Long.valueOf(obj.getId())).orElse(null);

			designation = userService.getNewDesignation(empId);
			oldDesignation = crv.getDesignation();

			crv.setVoucherStatus(obj.getVoucherStatus());
			crv.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				crv.setApprovedDate(LocalDate.now());
				cashReceiptVoucherService.updateClosingBalance(crv);
			}
			if (oldDesignation == null) {
				crv.setDesignation(Arrays.asList(designation));
			} else {
				crv.getDesignation().add(designation);
			}
			cashReceiptRepo.save(crv);
			return designation;
		}

		case "adjustmentReceiptVoucher": {
			AdjustmentReceiptVoucher arv = adjustmentReceiptVoucherRepo.findById(Long.valueOf(obj.getId()))
					.orElse(null);
			designation = userService.getNewDesignation(empId);
			oldDesignation = arv.getDesignation();

			arv.setVoucherStatus(obj.getVoucherStatus());
			arv.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				arv.setApprovedDate(LocalDate.now());
				adjustmentReceiptVoucherService.updateClosingBalance(arv);
				if (arv.getVoucherFor().equals("Non-CC Invoice") || arv.getVoucherFor().equals("ICM")) {
					sundryDebtorsAndCreditorsService.updateSdrAdjReceipt(arv, jwt);
				}
			}
			if (obj.getVoucherStatus().equals("Rejected")) {
				if (arv.getVoucherFor().equals("Non-CC Invoice")) {
					inventryService.revertNonCCInvoiceHandler(arv, jwt);
				}
			}
			if (oldDesignation == null) {
				arv.setDesignation(Arrays.asList(designation));
			} else {
				arv.getDesignation().add(designation);
			}
			adjustmentReceiptVoucherRepo.save(arv);
			return designation;
		}

		case "paymentVoucher": {
			PaymentVoucher pv = paymentVoucherRepo.findById(Long.valueOf(obj.getId())).orElse(null);
			designation = userService.getNewDesignation(empId);
			oldDesignation = pv.getDesignation();

			pv.setVoucherStatus(obj.getVoucherStatus());
			pv.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				pv.setApprovedDate(LocalDate.now());
				paymentVoucherService.updateClosingBalance(pv);
				if (pv.getVoucherFor().equals("CheckMemoGoods")) {
					sundryDebtorsAndCreditorsService.updateScrPv(pv, jwt);
				}
			}
			if (obj.getVoucherStatus().equals("Rejected")) {
				if (pv.getVoucherFor().equals("supplier advance")) {
					supplierAdvanceService.revertPvAndJv(null, jwt, pv, null);
				}
			}
			if (oldDesignation == null) {
				pv.setDesignation(Arrays.asList(designation));
			} else {
				pv.getDesignation().add(designation);
			}
			paymentVoucherRepo.save(pv);
			return designation;
		}

		case "journalVoucher": {
			JournalVoucher jv = journalVoucherRepo.findById(Long.valueOf(obj.getId())).orElse(null);

			designation = userService.getNewDesignation(empId);
			oldDesignation = jv.getDesignation();

			jv.setVoucherStatus(obj.getVoucherStatus());
			jv.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				jv.setApprovedDate(LocalDate.now());
				if (jv.getJvFor().equals("Sales Jv") && jv.getJvType().equals("net")) {
					sundryDebtorsAndCreditorsService.updateSdrJV(jv, jwt, "Dr");
				}
				if (jv.getJvFor().equals("Purchase JV") && jv.getJvType().equals("net")) {
					sundryDebtorsAndCreditorsService.updateSdrJV(jv, jwt, "Cr");
				}
			}
			if (obj.getVoucherStatus().equals("Rejected")) {
				if (jv.getJvFor().equals("supplier advance")) {
					supplierAdvanceService.revertPvAndJv(null, jwt, null, jv);
				} else if (jv.getJvFor().equals("Wagon GRN")) {
					inventryService.revertJvHandler(jv.getIdNo(), jwt);
				}
			}
			if (oldDesignation == null) {
				jv.setDesignation(Arrays.asList(designation));
			} else {
				jv.getDesignation().add(designation);
			}
			journalVoucherRepo.save(jv);
			return designation;
		}

		case "drCrNote": {
			DebitOrCreditNote debitOrCreditNote = debitOrCreditNoteRepo.findById(Long.valueOf(obj.getId()))
					.orElse(null);

			designation = userService.getNewDesignation(empId);
			oldDesignation = debitOrCreditNote.getDesignation();

			debitOrCreditNote.setVoucherStatus(obj.getVoucherStatus());
			debitOrCreditNote.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				debitOrCreditNote.setApprovedDate(LocalDate.now());
			}
			if (oldDesignation == null) {
				debitOrCreditNote.setDesignation(Arrays.asList(designation));
			} else {
				debitOrCreditNote.getDesignation().add(designation);
			}
			debitOrCreditNoteRepo.save(debitOrCreditNote);
			return designation;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + obj.getFormType());

		}
	}

	@Autowired
	private BillsGstObRepo billsGstObRepo;

	@Autowired
	private SundryCrObRepo sundryCrObRepo;

	@Autowired
	private SundryDrObRepo sundryDrObRepo;

	@Autowired
	private ReconciliationEntryRepo reconciliationEntryRepo;

	public String updateBillsAccountsVoucherApproval(VoucherApproval obj, String jwt) throws Exception {
		String designation = null;
		List<String> oldDesignation = null;

		String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
		logger.info("{}", obj);

		switch (obj.getFormType()) {
		case "supplierAdvance": {
			SupplierAdvance sa = supplierAdvanceRepo.findById(Long.valueOf(obj.getId())).orElse(null);

			designation = userService.getNewDesignation(empId);
			oldDesignation = sa.getDesignation();

			sa.setVoucherStatus(obj.getVoucherStatus());
			sa.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				sa.setApprovedDate(LocalDate.now());
			}
			if (obj.getVoucherStatus().equals("Rejected")) {
				try {
					supplierAdvanceService.revertPvAndJv(sa, jwt, null, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (oldDesignation == null) {
				sa.setDesignation(Arrays.asList(designation));
			} else {
				sa.getDesignation().add(designation);
			}
			supplierAdvanceRepo.save(sa);
			return designation;
		}
		case "billsGstOb": {
			BillsGstOb gstOb = billsGstObRepo.findById(Long.valueOf(obj.getId())).orElse(null);

			designation = userService.getNewDesignation(empId);
			oldDesignation = gstOb.getDesignation();

			gstOb.setVoucherStatus(obj.getVoucherStatus());
			gstOb.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				gstOb.setApprovedDate(LocalDate.now());
			}
			if (oldDesignation == null) {
				gstOb.setDesignation(Arrays.asList(designation));
			} else {
				gstOb.getDesignation().add(designation);
			}
			billsGstObRepo.save(gstOb);
			return designation;
		}
		case "sundryCrOb": {
			SundryCrOb scrOb = sundryCrObRepo.findById(Long.valueOf(obj.getId())).orElse(null);

			designation = userService.getNewDesignation(empId);
			oldDesignation = scrOb.getDesignation();

			scrOb.setVoucherStatus(obj.getVoucherStatus());
			scrOb.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				scrOb.setApprovedDate(LocalDate.now());
			}
			if (oldDesignation == null) {
				scrOb.setDesignation(Arrays.asList(designation));
			} else {
				scrOb.getDesignation().add(designation);
			}
			sundryCrObRepo.save(scrOb);
			return designation;
		}
		case "sundryDrOb": {
			SundryDrOb sdrOb = sundryDrObRepo.findById(Long.valueOf(obj.getId())).orElse(null);

			designation = userService.getNewDesignation(empId);
			oldDesignation = sdrOb.getDesignation();

			sdrOb.setVoucherStatus(obj.getVoucherStatus());
			sdrOb.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				sdrOb.setApprovedDate(LocalDate.now());
			}
			if (oldDesignation == null) {
				sdrOb.setDesignation(Arrays.asList(designation));
			} else {
				sdrOb.getDesignation().add(designation);
			}
			sundryDrObRepo.save(sdrOb);
			return designation;
		}
		case "reconciliationEntry": {
			ReconciliationEntry reconEntry = reconciliationEntryRepo.findById(Long.valueOf(obj.getId())).orElse(null);

			designation = userService.getNewDesignation(empId);
			oldDesignation = reconEntry.getDesignation();

			reconEntry.setVoucherStatus(obj.getVoucherStatus());
			reconEntry.getEmpId().add(empId);
			if (obj.getVoucherStatus().equals("Approved")) {
				reconEntry.setApprovedDate(LocalDate.now());
				sundryDebtorsAndCreditorsService.updateSdrReconEntry(reconEntry, jwt);

			}
			if (oldDesignation == null) {
				reconEntry.setDesignation(Arrays.asList(designation));
			} else {
				reconEntry.getDesignation().add(designation);
			}
			reconciliationEntryRepo.save(reconEntry);
			return designation;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + obj.getFormType());

		}
	}

}
