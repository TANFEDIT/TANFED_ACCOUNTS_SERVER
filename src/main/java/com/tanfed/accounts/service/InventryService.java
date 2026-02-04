package com.tanfed.accounts.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
import com.tanfed.accounts.model.Invoice;
import com.tanfed.accounts.model.TermsPrice;

@FeignClient(name = "INVENTRY-SERVICE", url = "${INVENTORY_API_URL}")
public interface InventryService {

	@GetMapping("/api/inventry/fetchinvoicebyoffice")
	public List<Invoice> getInvoiceDataByOfficenameHandler(@RequestParam String officeName,
			@RequestHeader("Authorization") String jwt) throws Exception;

	@PutMapping("/api/inventry/updatejvgrn/{grnNo}/{jv}")
	public void updateJvHandler(@PathVariable String grnNo, @RequestHeader("Authorization") String jwt,
			@PathVariable String jv) throws Exception;

	@GetMapping("/api/inventry/fetchterms")
	public List<TermsPrice> getTermsDataHandler(@RequestHeader("Authorization") String jwt) throws Exception;

	@GetMapping("/api/inventry/fetchtermsbytermsno")
	public TermsPrice getTermsDataByTermsNoHandler(@RequestParam String termsNo,
			@RequestHeader("Authorization") String jwt) throws Exception;

	@PutMapping("/api/inventry/revertnonccinvoice")
	public void revertNonCCInvoiceHandler(@RequestParam AdjustmentReceiptVoucher adjv,
			@RequestHeader("Authorization") String jwt) throws Exception;

	@PutMapping("/api/inventry/approvenonccinvoice")
	public void approveNonCCInvoiceHandler(@RequestParam String invoiceNo, @RequestHeader("Authorization") String jwt)
			throws Exception;

	@PutMapping("/api/inventry/revertjvingrn/{grnNo}")
	public void revertJvHandler(@PathVariable String grnNo, @RequestHeader("Authorization") String jwt)
			throws Exception;

}
