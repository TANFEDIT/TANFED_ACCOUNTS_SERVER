package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.components.MasterDataManager;
import com.tanfed.accounts.dto.ContraEntryDto;
import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
import com.tanfed.accounts.entity.CashReceiptVoucher;
import com.tanfed.accounts.entity.ContraEntry;
import com.tanfed.accounts.entity.PaymentVoucher;
import com.tanfed.accounts.model.BankInfo;
import com.tanfed.accounts.repository.AdjustmentReceiptVoucherRepo;
import com.tanfed.accounts.repository.CashReceiptRepo;
import com.tanfed.accounts.repository.ContraEntryRepo;
import com.tanfed.accounts.response.DataForContraEntry;
import com.tanfed.accounts.response.DataForPaymentVoucher;
import com.tanfed.accounts.utils.RoundToDecimalPlace;

@Service
public class ContraVoucherServiceImpl implements ContraVoucherService {

	@Autowired
	private PaymentVoucherService paymentVoucherService;

	@Autowired
	private AdjustmentReceiptVoucherService adjustmentReceiptVoucherService;

	@Autowired
	private CashReceiptVoucherService cashReceiptVoucherService;

	@Autowired
	private MasterDataManager masterDataManager;

	@Autowired
	private UserService userService;

	@Override
	public DataForContraEntry getDataForContraEntry(String officeName, String jwt, String paymentAccType, String pvType,
			String contraBetween, String receiptAccType, LocalDate date, String paymentAccountNo,
			String paymentBranchName, String receiptAccountNo, String paidTo) throws Exception {
		try {
			DataForContraEntry data = new DataForContraEntry();
			if (officeName != null && !officeName.isEmpty()) {
				if (contraBetween.startsWith("Cash")) {
					data.setBalance(RoundToDecimalPlace.roundToTwoDecimalPlaces(paymentVoucherService
							.getDataForPaymentVoucher(officeName, null, null, null, jwt, null, null, date, pvType, null)
							.getBalance()));
				} else if (contraBetween.startsWith("Bank") || contraBetween.startsWith("HO")
						|| contraBetween.startsWith("RO") || contraBetween.equals("Invoice Collection Transfer")) {
					try {
						List<BankInfo> bankInfo = masterDataManager.fetchBankInfoData(jwt);
						data.setPaymentAccountTypeList(bankInfo.stream().filter(
								i -> i.getOfficeName().equals(officeName) && contraBetween.equals("Bank to Bank") ? true
										: !i.getAccountType().equals("Non PDS A/c Fert"))
								.map(BankInfo::getAccountType).collect(Collectors.toSet()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (paymentAccType != null && !paymentAccType.isEmpty()) {
						DataForPaymentVoucher dataForPaymentVoucher = paymentVoucherService.getDataForPaymentVoucher(
								officeName, paymentAccType, paymentAccountNo, paymentBranchName, jwt, null, null, date,
								pvType, null);
						data.setPaymentAccNoList(dataForPaymentVoucher.getAccountNumList());
						if (paymentAccountNo != null && !paymentAccountNo.isEmpty()) {
							data.setPaymentBranchNameList(dataForPaymentVoucher.getBranchNameList());
							if (paymentBranchName != null && !paymentBranchName.isEmpty()) {
								data.setBalance(RoundToDecimalPlace
										.roundToTwoDecimalPlaces(dataForPaymentVoucher.getBalance()));
							}
						}
					}
				}
				if (contraBetween.startsWith("HO")) {
					data.setOfficeNameList(
							userService.getOfficeList().stream().filter(i -> !i.getOfficeType().equals("Head Office"))
									.map(i -> i.getOfficeName()).collect(Collectors.toList()));
				}
				if (contraBetween.endsWith("Bank") || contraBetween.equals("Invoice Collection Transfer")) {
					String office = paidTo.equals("Self") ? officeName : paidTo;
					List<BankInfo> bankInfo = masterDataManager.fetchBankInfoData(jwt).stream()
							.filter(i -> i.getOfficeName().equals(office)).collect(Collectors.toList());
					data.setReceiptAccountTypeList(bankInfo.stream().filter(
							i -> (contraBetween.equals("Cash to Bank") || contraBetween.equals("HO Bank to RO Bank"))
									? !i.getAccountType().equals("Non PDS A/c Fert")
									: true)
							.map(BankInfo::getAccountType).collect(Collectors.toSet()));
					if (receiptAccType != null && !receiptAccType.isEmpty()) {
						data.setReceiptAccNoList(
								bankInfo.stream().filter(i -> i.getAccountType().equals(receiptAccType))
										.map(i -> i.getAccountNumber()).collect(Collectors.toList()));
						if (receiptAccountNo != null && !receiptAccountNo.isEmpty()) {
							data.setReceiptBranchNameList(bankInfo.stream()
									.filter(i -> i.getAccountNumber().equals(Long.valueOf(receiptAccountNo)))
									.map(i -> i.getBranchName()).collect(Collectors.toSet()));
						}
					}
				}
				if (contraBetween.equals("Invoice Collection Transfer")) {
					data.setPaymentAccountTypeList(Set.of("Non PDS A/c Fert"));
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
			pv.setActivity(obj.getContraBetween().equals("Invoice Collection Transfer") ? obj.getActivity() : null);
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
					obj.getOfficeName(), obj.getAmount(), obj.getMainHead(), obj.getActivity(), obj.getPvType(),
					obj.getPaidTo(), obj.getPaymentAccType(), obj.getPaymentAccountNo(), obj.getPaymentBranchName(),
					obj.getPaymentRemarks(), obj.getPaymentSubHead(), obj.getReceivedFrom(), obj.getReceiptAccType(),
					obj.getReceiptAccountNo(), obj.getReceiptBranchName(), obj.getReceiptRemarks(),
					obj.getReceiptSubHead(), obj.getReceiptMode(), obj.getUtrChequeNoDdNo(), obj.getDocDate(),
					obj.getIssuingBank()));
			return new ResponseEntity<String>("Created Successfully", HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
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
	public List<ContraEntry> getContraByOfficeName(String officeName) throws Exception {
		try {
			return contraEntryRepo.findByOfficeName(officeName);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private AdjustmentReceiptVoucherRepo adjustmentReceiptVoucherRepo;

	@Autowired
	private CashReceiptRepo cashReceiptRepo;

	@Override
	public void updateVoucherStatusForContra(PaymentVoucher pv, String jwt) throws Exception {
		try {
			ContraEntry contraEntry = contraEntryRepo.findByContraId(pv.getContraId());
			if (contraEntry.getContraBetween().startsWith("Cash")) {
				AdjustmentReceiptVoucher adjustmentReceiptVoucher = adjustmentReceiptVoucherService
						.getAdjustmentReceiptVoucherByContraId(pv.getContraId());
				adjustmentReceiptVoucher.setVoucherStatus(pv.getVoucherStatus());
				adjustmentReceiptVoucher.setApprovedDate(LocalDate.now());
				adjustmentReceiptVoucherRepo.save(adjustmentReceiptVoucher);
				if (pv.getVoucherStatus().equals("Approved")) {
					adjustmentReceiptVoucherService.updateClosingBalance(adjustmentReceiptVoucher);
				}
			} else if (contraEntry.getContraBetween().equals("Bank to Cash")) {
				CashReceiptVoucher cashReceiptVoucher = cashReceiptVoucherService
						.getCashReceiptVoucherByContraId(pv.getContraId());
				cashReceiptVoucher.setVoucherStatus(pv.getVoucherStatus());
				cashReceiptVoucher.setApprovedDate(LocalDate.now());
				cashReceiptRepo.save(cashReceiptVoucher);
				if (pv.getVoucherStatus().equals("Approved")) {
					cashReceiptVoucherService.updateClosingBalance(cashReceiptVoucher);
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
