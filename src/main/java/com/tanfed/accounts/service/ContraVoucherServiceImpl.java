package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	public DataForContraEntry getDataForContraEntry(String officeName, String contraBetween, LocalDate contraFromDate,
			LocalDate contraToDate) throws Exception {
		try {
			DataForContraEntry data = new DataForContraEntry();
			if (officeName != null && !officeName.isEmpty()) {
				if (contraBetween != null && contraBetween.equals("Bank to Cash")) {
					data.setPaymentList(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
							.filter(item -> !item.getDate().isBefore(contraFromDate)
									&& !item.getDate().isAfter(contraToDate)
									&& !item.getPvType().equals("Cash Payment Voucher")
									&& item.getVoucherStatus().equals("Approved")
									&& item.getContraEntry().equals("Yes"))
							.collect(Collectors.toList()));

					data.setCashReceiptList(cashReceiptVoucherService.getVouchersByOfficeName(officeName).stream()
							.filter(item -> !item.getDate().isBefore(contraFromDate)
									&& !item.getDate().isAfter(contraToDate)
									&& item.getVoucherStatus().equals("Approved")
									&& item.getContraEntry().equals("Yes"))
							.collect(Collectors.toList()));

				}
				if (contraBetween != null && contraBetween.equals("Cash to Bank")) {
					data.setPaymentList(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
							.filter(item -> !item.getDate().isBefore(contraFromDate)
									&& !item.getDate().isAfter(contraToDate)
									&& item.getPvType().equals("Cash Payment Voucher")
									&& item.getVoucherStatus().equals("Approved")
									&& item.getContraEntry().equals("Yes"))
							.collect(Collectors.toList()));

					data.setAdjReceiptList(adjustmentReceiptVoucherService.getVoucherByOfficeName(officeName).stream()
							.filter(item -> !item.getDate().isBefore(contraFromDate)
									&& !item.getDate().isAfter(contraToDate) && item.getContraEntry().equals("Yes")
									&& item.getVoucherStatus().equals("Approved"))
							.collect(Collectors.toList()));

				}
				if (contraBetween != null && contraBetween.equals("Bank to Bank")) {
					data.setPaymentList(paymentVoucherService.getVoucherByOfficeName(officeName).stream()
							.filter(item -> !item.getDate().isBefore(contraFromDate)
									&& !item.getDate().isAfter(contraToDate)
									&& !item.getPvType().equals("Cash Payment Voucher")
									&& item.getVoucherStatus().equals("Approved") && item.getContraEntry().equals("Yes"))
							.collect(Collectors.toList()));

					data.setAdjReceiptList(adjustmentReceiptVoucherService.getVoucherByOfficeName(officeName).stream()
							.filter(item -> !item.getDate().isBefore(contraFromDate)
									&& !item.getDate().isAfter(contraToDate)
									&& item.getVoucherStatus().equals("Approved")
									&& item.getContraEntry().equals("Yes"))
							.collect(Collectors.toList()));

				}
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
