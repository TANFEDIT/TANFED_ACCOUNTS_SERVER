package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
import com.tanfed.accounts.entity.CashReceiptVoucher;
import com.tanfed.accounts.entity.PaymentVoucher;
import com.tanfed.accounts.model.ContraEntry;
import com.tanfed.accounts.repository.AdjustmentReceiptVoucherRepo;
import com.tanfed.accounts.repository.CashReceiptRepo;
import com.tanfed.accounts.repository.PaymentVoucherRepo;
import com.tanfed.accounts.response.DataForContraEntry;

@Service
public class ContraVoucherServiceImpl implements ContraVoucherService {

	@Autowired
	private CashReceiptVoucherService cashReceiptVoucherService;

	@Autowired
	private AdjustmentReceiptVoucherService adjustmentReceiptVoucherService;

	@Autowired
	private PaymentVoucherService paymentVoucherService;

	@Override
	public DataForContraEntry getDataForContraEntry(String officeName, String contraType, LocalDate date, String fromNo,
			String toNo) throws Exception {
		try {
			DataForContraEntry data = new DataForContraEntry();
			if (officeName != null && !officeName.isEmpty()) {
				if (contraType != null && contraType.equals("Bank to Cash")) {
					data.setFromNoList(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
							.filter(item -> item.getDate().equals(date)
									&& !item.getPvType().equals("Cash Payment Voucher")
									&& item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("No")
									&& item.getMainHead().equals("H.O a/c - Other")
									&& item.getSubHead().equals("BANK ACCOUNT"))
							.map(item -> item.getVoucherNo()).collect(Collectors.toList()));

					data.setToNoList(cashReceiptVoucherService.getVouchersByOfficeName(officeName).stream()
							.filter(item -> item.getDate().equals(date) && item.getMainHead().equals("H.O a/c - Other")
									&& item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("No")
									&& item.getSubHead().equals("CASH ACCOUNT"))
							.map(item -> item.getVoucherNo()).collect(Collectors.toList()));
					if (fromNo != null && !fromNo.isEmpty()) {
						PaymentVoucher paymentVoucher = paymentVoucherService.getVoucherByVoucherNo(fromNo);
						data.setPaymentAmount(paymentVoucher.getAmount());
						data.setPaymentAccType(paymentVoucher.getAccountType());
						data.setPaymentAccNo(paymentVoucher.getAccountNo());
					}
					if (toNo != null && !toNo.isEmpty()) {
						CashReceiptVoucher cashReceiptVoucher = cashReceiptVoucherService
								.getCashReceiptVoucherByVoucherNo(toNo);
						data.setReceiptAmount(cashReceiptVoucher.getReceivedAmount());
					}
				}
				if (contraType != null && contraType.equals("Cash to Bank")) {
					data.setFromNoList(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
							.filter(item -> item.getDate().equals(date)
									&& item.getPvType().equals("Cash Payment Voucher")
									&& item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("No")
									&& item.getMainHead().equals("H.O a/c - Other")
									&& item.getSubHead().equals("CASH ACCOUNT"))
							.map(item -> item.getVoucherNo()).collect(Collectors.toList()));

					data.setToNoList(adjustmentReceiptVoucherService.getVoucherByOfficeName(officeName).stream()
							.filter(item -> item.getDate().equals(date) && item.getMainHead().equals("H.O a/c - Other")
									&& item.getContraEntry().equals("No") && item.getSubHead().equals("BANK ACCOUNT")
									&& item.getVoucherStatus().equals("Approved"))
							.map(item -> item.getVoucherNo()).collect(Collectors.toList()));
					if (fromNo != null && !fromNo.isEmpty()) {
						PaymentVoucher paymentVoucher = paymentVoucherService.getVoucherByVoucherNo(fromNo);
						data.setPaymentAmount(paymentVoucher.getAmount());
					}
					if (toNo != null && !toNo.isEmpty()) {
						AdjustmentReceiptVoucher adjustmentReceiptVoucher = adjustmentReceiptVoucherService
								.getVoucherByVoucherNo(toNo);
						data.setReceiptAmount(adjustmentReceiptVoucher.getReceivedAmount());
						data.setReceiptAccType(adjustmentReceiptVoucher.getAccountType());
						data.setReceiptAccNo(adjustmentReceiptVoucher.getAccountNo());
					}
				}
				if (contraType != null && contraType.equals("Bank to Bank")) {
					data.setFromNoList(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
							.filter(item -> item.getDate().equals(date)
									&& !item.getPvType().equals("Cash Payment Voucher")
									&& item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("No")
									&& item.getMainHead().equals("H.O a/c - Other")
									&& item.getSubHead().equals("BANK ACCOUNT"))
							.map(item -> item.getVoucherNo()).collect(Collectors.toList()));

					data.setToNoList(adjustmentReceiptVoucherService.getVoucherByOfficeName(officeName).stream()
							.filter(item -> item.getDate().equals(date) && item.getMainHead().equals("H.O a/c - Other")
									&& item.getContraEntry().equals("No") && item.getSubHead().equals("BANK ACCOUNT")
									&& item.getVoucherStatus().equals("Approved"))
							.map(item -> item.getVoucherNo()).collect(Collectors.toList()));
					if (fromNo != null && !fromNo.isEmpty()) {
						PaymentVoucher paymentVoucher = paymentVoucherService.getVoucherByVoucherNo(fromNo);
						data.setPaymentAmount(paymentVoucher.getAmount());
						data.setPaymentAccType(paymentVoucher.getAccountType());
						data.setPaymentAccNo(paymentVoucher.getAccountNo());
					}
					if (toNo != null && !toNo.isEmpty()) {
						AdjustmentReceiptVoucher adjustmentReceiptVoucher = adjustmentReceiptVoucherService
								.getVoucherByVoucherNo(toNo);
						data.setReceiptAmount(adjustmentReceiptVoucher.getReceivedAmount());
						data.setReceiptAccType(adjustmentReceiptVoucher.getAccountType());
						data.setReceiptAccNo(adjustmentReceiptVoucher.getAccountNo());
					}
				}
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private CashReceiptRepo cashReceiptRepo;

	@Autowired
	private AdjustmentReceiptVoucherRepo adjustmentReceiptVoucherRepo;

	@Autowired
	private PaymentVoucherRepo paymentVoucherRepo;

	@Override
	public ResponseEntity<String> updateContraEntry(String fromNo, String toNo, String narration) throws Exception {
		try {
			String contraId = UUID.randomUUID().toString();
			PaymentVoucher paymentVoucher = paymentVoucherService.getVoucherByVoucherNo(fromNo);
			paymentVoucher.setContraNarration(narration);
			paymentVoucher.setContraEntry("Yes");
			paymentVoucher.setContraId(contraId);
			paymentVoucherRepo.save(paymentVoucher);

			if (toNo.startsWith("CR")) {
				CashReceiptVoucher cashReceiptVoucher = cashReceiptVoucherService
						.getCashReceiptVoucherByVoucherNo(toNo);
				cashReceiptVoucher.setContraNarration(narration);
				cashReceiptVoucher.setContraEntry("Yes");
				cashReceiptVoucher.setContraId(contraId);
				cashReceiptRepo.save(cashReceiptVoucher);
			} else {
				AdjustmentReceiptVoucher adjustmentReceiptVoucher = adjustmentReceiptVoucherService
						.getVoucherByVoucherNo(toNo);
				adjustmentReceiptVoucher.setContraNarration(narration);
				adjustmentReceiptVoucher.setContraEntry("Yes");
				adjustmentReceiptVoucher.setContraId(contraId);
				adjustmentReceiptVoucherRepo.save(adjustmentReceiptVoucher);
			}
			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> rejectContraEntry(String fromNo, String toNo) throws Exception {
		try {
			PaymentVoucher paymentVoucher = paymentVoucherService.getVoucherByVoucherNo(fromNo);
			paymentVoucher.setContraNarration(null);
			paymentVoucher.setContraEntry("No");
			paymentVoucher.setContraId(null);
			paymentVoucherRepo.save(paymentVoucher);

			if (toNo.startsWith("CR")) {
				CashReceiptVoucher cashReceiptVoucher = cashReceiptVoucherService
						.getCashReceiptVoucherByVoucherNo(toNo);
				cashReceiptVoucher.setContraNarration(null);
				cashReceiptVoucher.setContraEntry("No");
				cashReceiptVoucher.setContraId(null);
				cashReceiptRepo.save(cashReceiptVoucher);
			} else {
				AdjustmentReceiptVoucher adjustmentReceiptVoucher = adjustmentReceiptVoucherService
						.getVoucherByVoucherNo(toNo);
				adjustmentReceiptVoucher.setContraNarration(null);
				adjustmentReceiptVoucher.setContraEntry("No");
				adjustmentReceiptVoucher.setContraId(null);
				adjustmentReceiptVoucherRepo.save(adjustmentReceiptVoucher);
			}
			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<ContraEntry> getContraEntryData(String officeName) throws Exception {
		try {
			return paymentVoucherService.getVoucherByOfficeName(officeName).stream()
					.filter(item -> item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("Yes"))
					.map(item -> {
						try {
							CashReceiptVoucher cv = cashReceiptVoucherService
									.getCashReceiptVoucherByContraId(item.getContraId());

							if (cv == null) {
								AdjustmentReceiptVoucher adj = adjustmentReceiptVoucherService
										.getAdjustmentReceiptVoucherByContraId(item.getContraId());
								return new ContraEntry(getContraFor(item, adj), item.getDate(),
										item.getContraNarration(), item.getVoucherNo(), item.getAccountType(),
										item.getAccountNo(), item.getAmount(), item.getMainHead(), item.getSubHead(),
										adj.getVoucherNo(), adj.getAccountType(), adj.getAccountNo(),
										adj.getReceivedAmount(), adj.getMainHead(), adj.getSubHead());
							} else {
								return new ContraEntry(getContraFor(item, null), item.getDate(),
										item.getContraNarration(), item.getVoucherNo(), item.getAccountType(),
										item.getAccountNo(), item.getAmount(), item.getMainHead(), item.getSubHead(),
										cv.getVoucherNo(), null, null, cv.getReceivedAmount(), cv.getMainHead(),
										cv.getSubHead());
							}
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					}).collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private String getContraFor(PaymentVoucher pv, AdjustmentReceiptVoucher adj) {
		if (pv.getPvType().equals("Cash Payment Voucher") && adj != null) {
			return "CASH TO BANK";
		} else if (!pv.getPvType().equals("Cash Payment Voucher") && adj != null) {
			return "BANK TO BANK";
		} else {
			return "BANK TO CASH";
		}

	}

}
