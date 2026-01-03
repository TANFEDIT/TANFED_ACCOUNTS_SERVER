package com.tanfed.accounts.controller;

import java.time.LocalDate;
import java.util.List;

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
import com.tanfed.accounts.response.*;
import com.tanfed.accounts.service.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

	@Autowired
	private FilteredAccountsDataService filteredAccountsDataService;

	@Autowired
	private OpeningBalanceService openingBalanceService;

	@Autowired
	private CashReceiptVoucherService cashReceiptVoucher;

	@Autowired
	private AdjustmentReceiptVoucherService adjustmentReceiptVoucher;

	@Autowired
	private PaymentVoucherService paymentVoucher;

	@Autowired
	private ContraVoucherService contraVoucher;

	@Autowired
	private JournalVoucherService journalVoucher;

	@Autowired
	private DebitCreditNoteService debitCreditNoteService;

	@Autowired
	private InventryService inventryService;

	/** POST MAPPING ACCOUNTS VOUCHERS **/
	@PostMapping("/saveob")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ACCADMIN', 'ROLE_ACCUSER', 'ROLE_ROADMIN', 'ROLE_ROUSER')")
	public ResponseEntity<String> saveOpeningBalanceHandler(@RequestBody OpeningBalanceDto obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return openingBalanceService.saveOpeningBalance(obj, jwt);
	}

	@PostMapping("/accvouchers/{formType}")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ACCADMIN', 'ROLE_ACCUSER', 'ROLE_ROADMIN', 'ROLE_ROUSER')")
	public ResponseEntity<String> saveAccountsVouchersHandler(@PathVariable String formType, @RequestBody Vouchers obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		switch (formType) {
		case "cashReceiptVoucher": {
			return cashReceiptVoucher.saveCashReceiptVoucher(obj.getCashReceiptVoucherData(), jwt);
		}
		case "adjustmentReceiptVoucher": {
			return adjustmentReceiptVoucher.saveAdjustmentReceiptVoucher(obj.getAdjustmentReceiptVoucherData(), jwt);
		}
		case "paymentVoucher": {
			return paymentVoucher.savePaymentVoucher(obj.getPaymentVoucherData(), jwt);
		}

		case "journalVoucher": {
			return journalVoucher.saveJournalVoucher(obj.getJournalVoucherData(), jwt);
		}
		case "drCrNote": {
			return debitCreditNoteService.saveDebitCreditNote(obj.getDrCrNoteData(), jwt);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + formType);
		}
	}

	@PostMapping("/accvoucher/journalVoucher/{formType}/{grnNo}")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ACCADMIN', 'ROLE_ACCUSER', 'ROLE_ROADMIN', 'ROLE_ROUSER')")
	public ResponseEntity<String> createJV(@RequestBody JournalVoucher obj,
			@PathVariable(required = false) String formType, @PathVariable(required = false) String grnNo,
			@RequestHeader("Authorization") String jwt) throws Exception {
		try {
			switch (formType) {
			case "grn": {
				obj.setIdNo(grnNo);
				ResponseEntity<String> saveJournalVoucher = journalVoucher.saveJournalVoucher(obj, jwt);
				String jv = saveJournalVoucher.getBody();
				if (jv == null) {
					return null;
				}
				int index = jv.indexOf("JV Number : ");
				String jvNo = jv.substring(index + "JV Number : ".length()).trim();
				inventryService.updateJvHandler(grnNo, jwt, jvNo);
				return saveJournalVoucher;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + formType);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@PostMapping("/savesalesjv")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ROADMIN', 'ROLE_ROUSER')")
	public ResponseEntity<String> saveSalesJvHandler(@RequestBody SalesJvTable obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return journalVoucher.saveSalesJv(obj, jwt);
	}

	/** PUT MAPPING ACCOUNTS VOUCHERS **/
	@PutMapping("/editob")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ACCADMIN', 'ROLE_ACCUSER', 'ROLE_ROADMIN', 'ROLE_ROUSER')")
	public ResponseEntity<String> editOpeningBalanceHandler(@RequestBody OpeningBalance obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return openingBalanceService.editOpeningBalance(obj);
	}

	@PutMapping("/accvouchersedit/{formType}")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ACCADMIN', 'ROLE_ACCUSER', 'ROLE_ROADMIN', 'ROLE_ROUSER')")
	public ResponseEntity<String> editAccountsVouchersHandler(@PathVariable String formType, @RequestBody Vouchers obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		switch (formType) {
		case "cashReceiptVoucher": {
			return cashReceiptVoucher.editCashReceiptVoucher(obj.getCashReceiptVoucherData(), jwt);
		}
		case "adjustmentReceiptVoucher": {
			return adjustmentReceiptVoucher.editAdjustmentReceiptVoucher(obj.getAdjustmentReceiptVoucherData(), jwt);
		}
		case "paymentVoucher": {
			return paymentVoucher.editPaymentVoucher(obj.getPaymentVoucherData(), jwt);
		}

		case "journalVoucher": {
			return journalVoucher.editJournalVoucher(obj.getJournalVoucherData(), jwt);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + formType);
		}
	}

	@PutMapping("/voucherupdate/{formType}")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ACCADMIN', 'ROLE_ACCUSER', 'ROLE_ROADMIN', 'ROLE_ROUSER')")
	public ResponseEntity<String> accountsVouchersUpdateHandler(@PathVariable String formType,
			@RequestBody Vouchers obj, @RequestHeader("Authorization") String jwt) throws Exception {
		switch (formType) {
		case "adjUpdate": {
			return adjustmentReceiptVoucher.AdjustmentReceiptVoucherUpdate(obj.getAdjustmentReceiptVoucher(), jwt);
		}
		case "pvCashUpdate": {
			return paymentVoucher.paymentVoucherCashUpdate(obj.getPaymentVoucher(), jwt);
		}
		case "pvOnlineUpdate": {
			return paymentVoucher.paymentVoucherOnlineUpdate(obj.getPaymentVoucher(), jwt);
		}
		case "pvChequeUpdate": {
			return paymentVoucher.paymentVoucherChequeUpdate(obj.getPaymentVoucher(), jwt);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + formType);
		}
	}

	@Autowired
	private VoucherApprovalService voucherApprovalService;

	@PutMapping("/voucherApproval")
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ACCADMIN', 'ROLE_ROADMIN')")
	public ResponseEntity<String> voucherApprovalHandler(@RequestBody VoucherApproval obj,
			@RequestHeader("Authorization") String jwt) throws Exception {
		String updatedStatus = voucherApprovalService.updateVoucherApproval(obj, jwt);
		return new ResponseEntity<String>(updatedStatus, HttpStatus.ACCEPTED);
	}

//	@PutMapping("/updatecontra")
//	public ResponseEntity<String> updateContraEntryHandler(@RequestParam String fromNo, @RequestParam String toNo,
//			@RequestParam String narration) throws Exception {
//		return contraVoucher.updateContraEntry(fromNo, toNo, narration);
//	}
//
//	@PutMapping("/rejectcontra")
//	public ResponseEntity<String> rejectContraEntryHandler(@RequestParam String fromNo, @RequestParam String toNo)
//			throws Exception {
//		return contraVoucher.rejectContraEntry(fromNo, toNo);
//	}

	@GetMapping("/accountsfilterdata")
	public Vouchers getFilteredDataHandler(@RequestParam String formType, @RequestParam String officeName,
			@RequestParam(required = false) String voucherStatus, @RequestParam(required = false) String voucherNo,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return filteredAccountsDataService.getFilteredData(formType, fromDate, toDate, officeName, voucherStatus,
				voucherNo, jwt);
	}

	@GetMapping("/getvouchersbyvoucherno")
	public Vouchers getAccountsVoucherByVoucherNoHandler(@RequestParam String formType, @RequestParam String voucherNo)
			throws Exception {
		Vouchers data = new Vouchers();
		switch (formType) {
		case "cashReceiptVoucher": {
			CashReceiptVoucher cashReceipt = cashReceiptVoucher.getCashReceiptVoucherByVoucherNo(voucherNo);
			data.setCashReceiptVoucherData(cashReceipt);
			return data;
		}
		case "adjustmentReceiptVoucher": {
			AdjustmentReceiptVoucher adjReceipt = adjustmentReceiptVoucher.getVoucherByVoucherNo(voucherNo);
			data.setAdjustmentReceiptVoucherData(adjReceipt);
			return data;
		}
		case "paymentVoucher": {
			PaymentVoucher pymntVoucher = paymentVoucher.getVoucherByVoucherNo(voucherNo);
			data.setPaymentVoucherData(pymntVoucher);
			return data;
		}
		case "journalVoucher": {
			JournalVoucher jvByJvNo = journalVoucher.getJvByJvNo(voucherNo);
			data.setJournalVoucherData(jvByJvNo);
			return data;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + formType);
		}
	}

	@GetMapping("/fetchvouchernumforupdate")
	public Vouchers getVoucherNoForUpdateHandler(@RequestParam String formType, @RequestParam String officeName,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestHeader("Authorization") String jwt) throws Exception {
		try {
			Vouchers data = new Vouchers();
			switch (formType) {
			case "adjUpdate": {
				data.setAdjustmentReceiptVoucher(
						adjustmentReceiptVoucher.getVouchersForUpdate(officeName, fromDate, toDate));
				return data;
			}
			case "pvCashUpdate": {
				data.setPaymentVoucher(paymentVoucher.getVouchersForCashUpdate(officeName, fromDate, toDate));
				return data;
			}
			case "pvOnlineUpdate": {
				data.setPaymentVoucher(paymentVoucher.getVouchersForOnlineUpdate(officeName, fromDate, toDate));
				return data;
			}
			case "pvChequeUpdate": {
				data.setPaymentVoucher(paymentVoucher.getVouchersForChequeUpdate(officeName, fromDate, toDate));
				return data;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + formType);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@GetMapping("/fetchdataforpv")
	public DataForPaymentVoucher getDataForPaymentVoucherHandler(@RequestParam String officeName,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam String accountType, @RequestParam(required = false) String accountNo,
			@RequestParam String pvType, @RequestParam String mainHead, @RequestParam String paidTo,
			@RequestHeader("Authorization") String jwt) throws Exception {
		return paymentVoucher.getDataForPaymentVoucher(officeName, accountType, accountNo, jwt, mainHead, paidTo, date,
				pvType);
	}

	@GetMapping("/fetchfilterjvdata")
	public List<JournalVoucher> getJvDataByFilterHandler(@RequestParam String officeName, String month)
			throws Exception {
		return journalVoucher.getJvDataByFilter(officeName, month);
	}

	@GetMapping("/fetchsalesjvdata")
	public DataForSalesJv getDataForSalesJvHandler(@RequestParam String officeName, String activity, String firmType,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			String productCategory, @RequestHeader("Authorization") String jwt) throws Exception {
		return journalVoucher.getDataForSalesJv(officeName, activity, firmType, productCategory, fromDate, toDate, jwt);
	}

	@GetMapping("/fetchdataforcontra")
	public DataForContraEntry getDataForContraEntryHandler(@RequestParam String officeName, @RequestParam String contraBetween,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate contraFromDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate contraToDate)
			throws Exception {
		return contraVoucher.getDataForContraEntry(officeName, contraBetween, contraFromDate, contraToDate);
	}

	@GetMapping("/accob/validate/{officeName}")
	public String accObValidateHandler(@PathVariable String officeName) throws Exception {
		return openingBalanceService.accObValidate(officeName);
	}

}
