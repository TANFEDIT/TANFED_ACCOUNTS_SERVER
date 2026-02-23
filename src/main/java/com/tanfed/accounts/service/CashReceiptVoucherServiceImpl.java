package com.tanfed.accounts.service;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.entity.CashReceiptVoucher;
import com.tanfed.accounts.entity.ClosingBalanceTable;
import com.tanfed.accounts.repository.CashReceiptRepo;
import com.tanfed.accounts.repository.ClosingBalanceRepo;
import com.tanfed.accounts.utils.CodeGenerator;

@Service
public class CashReceiptVoucherServiceImpl implements CashReceiptVoucherService {

	@Autowired
	private CashReceiptRepo cashReceiptRepo;

	@Autowired
	private CodeGenerator codeGenerator;

	@Override
	public ResponseEntity<String> saveCashReceiptVoucher(CashReceiptVoucher obj, String jwt) throws Exception {
		try {
			String code;
			do {
				code = "CR" + codeGenerator.voucherNoGenerator();
			} while (cashReceiptRepo.findByVoucherNo(code).isPresent());
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.setEmpId(Arrays.asList(empId));
			obj.setVoucherNo(code);
			cashReceiptRepo.save(obj);
			return new ResponseEntity<String>("Created Successfully" + "\nVoucher Number: " + code, HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> editCashReceiptVoucher(CashReceiptVoucher obj, String jwt) throws Exception {
		try {
			CashReceiptVoucher cashReceiptVoucher = cashReceiptRepo.findById(obj.getId()).get();
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			cashReceiptVoucher.getEmpId().add(empId);
			cashReceiptVoucher.setMainHead(obj.getMainHead());
			cashReceiptVoucher.setSubHead(obj.getSubHead());
			cashReceiptVoucher.setRemarks(obj.getRemarks());

			cashReceiptRepo.save(cashReceiptVoucher);

			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public CashReceiptVoucher getCashReceiptVoucherByVoucherNo(String voucherNo) throws Exception {
		try {
			CashReceiptVoucher byVoucherNo = cashReceiptRepo.findByVoucherNo(voucherNo).get();

			if (byVoucherNo == null) {
				throw new FileNotFoundException("No data found for voucherNo" + voucherNo);
			}
			return byVoucherNo;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<CashReceiptVoucher> getVouchersByOfficeName(String officeName) throws Exception {
		try {
			return cashReceiptRepo.findByOfficeName(officeName);

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private ClosingBalanceRepo closingBalanceRepo;

	@Override
	public void updateClosingBalance(CashReceiptVoucher obj) throws Exception {
		try {
			List<ClosingBalanceTable> cb = closingBalanceRepo
					.findByOfficeNameAndDate(obj.getOfficeName(), obj.getDate()).stream()
					.filter(item -> item.getCashBalance() != null).collect(Collectors.toList());
			if (cb.isEmpty()) {
				int n = 1;
				while (cb.isEmpty()) {
					LocalDate prevDate = obj.getDate().minusDays(n++);
					cb = closingBalanceRepo.findByOfficeNameAndDate(obj.getOfficeName(), prevDate).stream()
							.filter(item -> item.getCashBalance() != null).collect(Collectors.toList());
					if (n == 365) {
						closingBalanceRepo.save(new ClosingBalanceTable(null, obj.getOfficeName(), obj.getDate(),
								obj.getReceivedAmount(), null, null, null, null));
						break;
					}
				}
				closingBalanceRepo.save(new ClosingBalanceTable(null, obj.getOfficeName(), obj.getDate(),
						cb.get(0).getCashBalance() + obj.getReceivedAmount(), null, null, null, null));
			} else {
				cb.get(0).setCashBalance(cb.get(0).getCashBalance() + obj.getReceivedAmount());
				closingBalanceRepo.save(cb.get(0));
			}
			List<ClosingBalanceTable> cbData = closingBalanceRepo.findByOfficeName(obj.getOfficeName()).stream()
					.filter(item -> item.getCashBalance() != null && item.getDate().isAfter(obj.getDate()))
					.collect(Collectors.toList());
			cbData.forEach(item -> {
				item.setCashBalance(item.getCashBalance() + obj.getReceivedAmount());
			});
			closingBalanceRepo.saveAll(cbData);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public CashReceiptVoucher getCashReceiptVoucherByContraId(String contraId) throws Exception {
		try {
			return cashReceiptRepo.findByContraId(contraId);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
