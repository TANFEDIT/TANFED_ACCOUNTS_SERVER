package com.tanfed.accounts.components;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
import com.tanfed.accounts.entity.ContraEntry;
import com.tanfed.accounts.entity.PaymentVoucher;
import com.tanfed.accounts.model.BankInfo;
import com.tanfed.accounts.repository.AdjustmentReceiptVoucherRepo;
import com.tanfed.accounts.repository.ContraEntryRepo;
import com.tanfed.accounts.service.AdjustmentReceiptVoucherService;
import com.tanfed.accounts.service.MasterService;

@Component
public class InterTransferReceipts {

	@Autowired
	private ContraEntryRepo contraEntryRepo;

	@Autowired
	private MasterService masterService;

	@Autowired
	private AdjustmentReceiptVoucherService adjustmentReceiptVoucherService;

	@Autowired
	private AdjustmentReceiptVoucherRepo adjustmentReceiptVoucherRepo;

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
		adjustmentReceiptVoucher.setVoucherStatus("Approved");
		adjustmentReceiptVoucher.setApprovedDate(LocalDate.now());
		adjustmentReceiptVoucherRepo.save(adjustmentReceiptVoucher);
		adjustmentReceiptVoucherService.updateClosingBalance(adjustmentReceiptVoucher);
	}
}
