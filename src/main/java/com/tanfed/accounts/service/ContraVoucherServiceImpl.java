package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.dto.ContraEntryDto;
import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
import com.tanfed.accounts.entity.CashReceiptVoucher;
import com.tanfed.accounts.entity.ContraEntry;
import com.tanfed.accounts.entity.PaymentVoucher;
import com.tanfed.accounts.model.BankInfo;
import com.tanfed.accounts.model.VoucherApproval;
import com.tanfed.accounts.repository.ContraEntryRepo;
import com.tanfed.accounts.response.DataForContraEntry;
import com.tanfed.accounts.response.DataForPaymentVoucher;

@Service
public class ContraVoucherServiceImpl implements ContraVoucherService {

	@Autowired
	private PaymentVoucherService paymentVoucherService;

	@Autowired
	private AdjustmentReceiptVoucherService adjustmentReceiptVoucherService;

	@Autowired
	private CashReceiptVoucherService cashReceiptVoucherService;

	@Autowired
	private MasterService masterService;

	@Autowired
	private UserService userService;

	@Override
	public DataForContraEntry getDataForContraEntry(String officeName, String jwt, String paymentAccType, String pvType,
			String contraBetween, String receiptAccType, LocalDate date, String paymentAccountNo,
			String receiptAccountNo, String paidTo) throws Exception {
		try {
			DataForContraEntry data = new DataForContraEntry();
			if (officeName != null && !officeName.isEmpty()) {
				if (contraBetween.startsWith("Cash")) {
					data.setBalance(paymentVoucherService
							.getDataForPaymentVoucher(officeName, null, null, jwt, null, null, date, pvType)
							.getBalance());
				} else if (contraBetween.startsWith("Bank") || contraBetween.startsWith("HO")
						|| contraBetween.startsWith("RO")) {
					List<BankInfo> bankInfo = masterService.getBankInfoByOfficeNameHandler(jwt, officeName);
					data.setPaymentAccountTypeList(
							bankInfo.stream().map(BankInfo::getAccountType).collect(Collectors.toSet()));
					if (paymentAccType != null && !paymentAccType.isEmpty()) {
						DataForPaymentVoucher dataForPaymentVoucher = paymentVoucherService.getDataForPaymentVoucher(
								officeName, paymentAccType, paymentAccountNo, jwt, null, null, date, pvType);
						data.setPaymentAccNoList(dataForPaymentVoucher.getAccountNumList());
						if (paymentAccountNo != null && !paymentAccountNo.isEmpty()) {
							data.setPaymentBranchName(dataForPaymentVoucher.getBranchName());
							data.setBalance(dataForPaymentVoucher.getBalance());
						}
					}
				}
				if (contraBetween.startsWith("HO")) {
					data.setOfficeNameList(userService.getOfficeList().stream()
							.filter(i -> i.getOfficeType().equals("Regional Office")).map(i -> i.getOfficeName())
							.collect(Collectors.toList()));
				}
				if (contraBetween.endsWith("Bank")) {
					String office = paidTo.equals("Self") ? officeName : paidTo;
					List<BankInfo> bankInfo = masterService.getBankInfoByOfficeNameHandler(jwt, office);
					data.setReceiptAccountTypeList(
							bankInfo.stream().map(BankInfo::getAccountType).collect(Collectors.toSet()));
					if (receiptAccType != null && !receiptAccType.isEmpty()) {
						DataForPaymentVoucher dataForPaymentVoucher = paymentVoucherService.getDataForPaymentVoucher(
								office, receiptAccType, receiptAccountNo, jwt, null, null, date, pvType);
						data.setReceiptAccNoList(dataForPaymentVoucher.getAccountNumList());
						if (receiptAccountNo != null && !receiptAccountNo.isEmpty()) {
							data.setReceiptBranchName(dataForPaymentVoucher.getBranchName());
						}
					}
				}
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private ContraEntryRepo contraEntryRepo;

	@Override
	public ResponseEntity<String> saveContraEntry(ContraEntryDto obj, String jwt) throws Exception {
		try {
			String contraId = UUID.randomUUID().toString();
			PaymentVoucher pv = new PaymentVoucher();
			pv.setDate(obj.getDate());
			pv.setPvType(obj.getPvType());
			pv.setMainHead(obj.getMainHead());
			pv.setSubHead(obj.getPaymentSubHead());
			pv.setPaidTo(obj.getPaidTo());
			pv.setAmount(obj.getAmount());
			pv.setNarration(obj.getPaymentRemarks());
			pv.setContraEntry("Yes");
			pv.setVoucherStatus("Pending");
			pv.setVoucherFor("Contra");
			pv.setContraId(contraId);
			pv.setOfficeName(obj.getOfficeName());
			if (!pv.getPvType().equals("Cash Payment Voucher")) {
				pv.setAccountType(obj.getPaymentAccType());
				pv.setAccountNo(obj.getPaymentAccountNo());
				pv.setBranchName(obj.getPaymentBranchName());
			}
			paymentVoucherService.savePaymentVoucher(pv, jwt);
			if (obj.getContraBetween().equals("Cash to Bank")) {
				AdjustmentReceiptVoucher adj = new AdjustmentReceiptVoucher();
				adj.setDate(obj.getDate());
				adj.setOfficeName(obj.getOfficeName());
				adj.setReceivedFrom(obj.getReceivedFrom());
				adj.setContraId(contraId);
				adj.setReceivedAmount(obj.getAmount());
				adj.setReceiptMode("Cash Deposit");
				adj.setAccountType(obj.getReceiptAccType());
				adj.setAccountNo(obj.getReceiptAccountNo());
				adj.setBranchName(obj.getReceiptBranchName());
				adj.setMainHead(obj.getMainHead());
				adj.setSubHead(obj.getReceiptSubHead());
				adj.setNarration(obj.getReceiptRemarks());
				adj.setVoucherFor("Contra");
				adj.setContraEntry("Yes");
				adjustmentReceiptVoucherService.saveAdjustmentReceiptVoucher(adj, jwt);
			} else if (obj.getContraBetween().endsWith("Cash")) {
				CashReceiptVoucher cr = new CashReceiptVoucher();
				cr.setOfficeName(obj.getOfficeName());
				cr.setMainHead(obj.getMainHead());
				cr.setSubHead(obj.getReceiptSubHead());
				cr.setRemarks(obj.getReceiptRemarks());
				cr.setReceivedFrom(obj.getReceivedFrom());
				cr.setReceivedAmount(obj.getAmount());
				cr.setDate(obj.getDate());
				cr.setContraEntry("Yes");
				cr.setContraId(contraId);
				cashReceiptVoucherService.saveCashReceiptVoucher(cr, jwt);
			}
			contraEntryRepo.save(new ContraEntry(null, null, contraId, obj.getDate(), obj.getContraBetween(),
					obj.getOfficeName(), obj.getAmount(), obj.getMainHead(), obj.getPvType(), obj.getPaidTo(),
					obj.getPaymentAccType(), obj.getPaymentAccountNo(), obj.getPaymentBranchName(),
					obj.getPaymentRemarks(), obj.getPaymentSubHead(), obj.getReceivedFrom(), obj.getReceiptAccType(),
					obj.getReceiptAccountNo(), obj.getReceiptBranchName(), obj.getReceiptRemarks(),
					obj.getReceiptSubHead(), obj.getReceiptMode(), obj.getUtrChequeNoDdNo(), obj.getDocDate(),
					obj.getIssuingBank()));
			return new ResponseEntity<String>("Created Successfully", HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private VoucherApprovalService voucherApprovalService;

	@Override
	public void createInterTransferAdjVoucher(PaymentVoucher pv, String jwt) throws Exception {
		ContraEntry contraEntry = contraEntryRepo.findByContraId(pv.getContraId());
		if (contraEntry == null) {
			throw new Exception("No contra Found!");
		}
		AdjustmentReceiptVoucher adj = new AdjustmentReceiptVoucher();
		adj.setDate(contraEntry.getDate());
		adj.setOfficeName(contraEntry.getPaidTo());
		adj.setReceivedFrom(contraEntry.getReceivedFrom());
		adj.setContraId(pv.getContraId());
		adj.setReceivedAmount(contraEntry.getAmount());
		adj.setReceiptMode(contraEntry.getReceiptMode());
		String no = pv.getPvType().equals("Online Payment Voucher") ? pv.getUtrNumber().toString()
				: pv.getChequeNumber();
		adj.setUtrChequeNoDdNo(Long.valueOf(no));
		LocalDate date = pv.getPvType().equals("Online Payment Voucher") ? pv.getOnlineDate() : pv.getChequeDate();
		adj.setDocDate(date);
		BankInfo bankInfo = masterService.getBankInfoByOfficeNameHandler(jwt, pv.getOfficeName()).stream()
				.filter(i -> i.getAccountNumber().equals(pv.getAccountNo())).collect(Collectors.toList()).get(0);
		adj.setIssuingBank(bankInfo.getBankName());
		adj.setAccountType(contraEntry.getReceiptAccType());
		adj.setAccountNo(contraEntry.getReceiptAccountNo());
		adj.setBranchName(contraEntry.getReceiptBranchName());
		adj.setMainHead(contraEntry.getMainHead());
		adj.setSubHead(contraEntry.getReceiptSubHead());
		adj.setNarration(contraEntry.getReceiptRemarks());
		adj.setVoucherFor("Contra");
		adj.setContraEntry("Yes");
		adjustmentReceiptVoucherService.saveAdjustmentReceiptVoucher(adj, jwt);
		AdjustmentReceiptVoucher adjustmentReceiptVoucher = adjustmentReceiptVoucherService
				.getAdjustmentReceiptVoucherByContraId(adj.getContraId());
		voucherApprovalService.updateVoucherApproval(new VoucherApproval("Approved",
				adjustmentReceiptVoucher.getId().toString(), "adjustmentReceiptVoucher", null), jwt);
	}

	@Override
	public ContraEntry getContraById(String contraId) throws Exception {
		try {
			ContraEntry contraEntry = contraEntryRepo.findByContraId(contraId);
			if (contraEntry == null) {
				throw new Exception("No contra Found!");
			}
			return contraEntry;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public void updateVoucherStatusForContra(PaymentVoucher pv, String jwt) throws Exception {
		try {
			ContraEntry contraEntry = contraEntryRepo.findByContraId(pv.getContraId());
			if (contraEntry.getContraBetween().startsWith("Cash")) {
				AdjustmentReceiptVoucher adjustmentReceiptVoucher = adjustmentReceiptVoucherService
						.getAdjustmentReceiptVoucherByContraId(pv.getContraId());
				voucherApprovalService.updateVoucherApproval(new VoucherApproval(pv.getVoucherStatus(),
						adjustmentReceiptVoucher.getId().toString(), "adjustmentReceiptVoucher", null), jwt);
			} else if (contraEntry.getContraBetween().equals("Bank to Cash")) {
				CashReceiptVoucher cashReceiptVoucher = cashReceiptVoucherService
						.getCashReceiptVoucherByContraId(pv.getContraId());
				voucherApprovalService.updateVoucherApproval(new VoucherApproval(pv.getVoucherStatus(),
						cashReceiptVoucher.getId().toString(), "cashReceiptVoucher", null), jwt);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
