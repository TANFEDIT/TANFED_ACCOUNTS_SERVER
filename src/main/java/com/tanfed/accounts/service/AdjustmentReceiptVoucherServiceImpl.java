package com.tanfed.accounts.service;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
import com.tanfed.accounts.entity.ClosingBalanceTable;
import com.tanfed.accounts.repository.AdjustmentReceiptVoucherRepo;
import com.tanfed.accounts.repository.ClosingBalanceRepo;
import com.tanfed.accounts.utils.CodeGenerator;

@Service
public class AdjustmentReceiptVoucherServiceImpl implements AdjustmentReceiptVoucherService {

	@Autowired
	private AdjustmentReceiptVoucherRepo adjustmentReceiptVoucherRepo;

	@Autowired
	private CodeGenerator codeGenerator;
	private Logger logger = LoggerFactory.getLogger(AdjustmentReceiptVoucherServiceImpl.class);

	@Override
	public ResponseEntity<String> saveAdjustmentReceiptVoucher(AdjustmentReceiptVoucher obj, String jwt)
			throws Exception {
		try {
			String code;
			do {
				code = "AR" + codeGenerator.voucherNoGenerator();
			} while (adjustmentReceiptVoucherRepo.findByVoucherNo(code).isPresent());
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.setEmpId(Arrays.asList(empId));
			obj.setVoucherNo(code);
			obj.setVoucherStatus("Pending");
			adjustmentReceiptVoucherRepo.save(obj);
			return new ResponseEntity<String>("Created Successfully" + "\nVoucher Number: " + code, HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> editAdjustmentReceiptVoucher(AdjustmentReceiptVoucher obj, String jwt)
			throws Exception {
		try {
			AdjustmentReceiptVoucher byVoucherNo = adjustmentReceiptVoucherRepo.findById(obj.getId()).get();
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			byVoucherNo.getEmpId().add(empId);
			byVoucherNo.setMainHead(obj.getMainHead());
			byVoucherNo.setSubHead(obj.getSubHead());
			byVoucherNo.setNarration(obj.getNarration());

			adjustmentReceiptVoucherRepo.save(obj);

			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public AdjustmentReceiptVoucher getVoucherByVoucherNo(String voucherNo) throws Exception {
		try {
			logger.info(voucherNo);
			AdjustmentReceiptVoucher byVoucherNo = adjustmentReceiptVoucherRepo.findByVoucherNo(voucherNo).get();

			if (byVoucherNo == null) {
				throw new FileNotFoundException("No data found!");
			}
			return byVoucherNo;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<AdjustmentReceiptVoucher> getVoucherByOfficeName(String officeName) throws Exception {
		try {
			return adjustmentReceiptVoucherRepo.findByOfficeName(officeName);

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<AdjustmentReceiptVoucher> getVouchersForUpdate(String officeName, LocalDate fromDate, LocalDate toDate)
			throws Exception {
		try {
			return adjustmentReceiptVoucherRepo.findByOfficeName(officeName).stream().filter(item -> {
				return item.getDepositDate() == null && item.getBankCharges() == null
						&& item.getDateOfCollection() == null && !item.getDate().isBefore(fromDate)
						&& !item.getDate().isAfter(toDate) && !item.getReceiptMode().equals("returned");
			}).collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> AdjustmentReceiptVoucherUpdate(List<AdjustmentReceiptVoucher> obj, String jwt)
			throws Exception {
		try {

			logger.info("{}", obj);
			obj.forEach(item -> {
				AdjustmentReceiptVoucher byVoucherNo = adjustmentReceiptVoucherRepo.findByVoucherNo(item.getVoucherNo())
						.get();
				String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
				byVoucherNo.getEmpId().add(empId);
				byVoucherNo.setDepositDate(item.getDepositDate());
				byVoucherNo.setBankCharges(item.getBankCharges());
				byVoucherNo.setDateOfCollection(item.getDateOfCollection());
				adjustmentReceiptVoucherRepo.save(byVoucherNo);
			});

			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private ClosingBalanceRepo closingBalanceRepo;

	@Override
	public void updateClosingBalance(AdjustmentReceiptVoucher obj) throws Exception {
		try {
			List<ClosingBalanceTable> cb = closingBalanceRepo
					.findByOfficeNameAndDate(obj.getOfficeName(), obj.getDate()).stream()
					.filter(item -> item.getCashBalance() == null && item.getAccType().equals(obj.getAccountType())
							&& item.getAccNo().equals(obj.getAccountNo()))
					.collect(Collectors.toList());
			if (cb.isEmpty()) {
				int n = 1;
				while (cb.isEmpty()) {
					LocalDate prevDate = obj.getDate().minusDays(n++);
					cb = closingBalanceRepo.findByOfficeNameAndDate(obj.getOfficeName(), prevDate).stream()
							.filter(item -> item.getCashBalance() == null
									&& item.getAccType().equals(obj.getAccountType())
									&& item.getAccNo().equals(obj.getAccountNo()))
							.collect(Collectors.toList());
					if (n == 365) {
						closingBalanceRepo.save(new ClosingBalanceTable(null, obj.getOfficeName(), obj.getDate(), null,
								obj.getReceivedAmount(), obj.getAccountType(), obj.getAccountNo()));
						break;
					}
				}
				closingBalanceRepo.save(new ClosingBalanceTable(null, obj.getOfficeName(), obj.getDate(), null,
						cb.get(0).getBankBalance() + obj.getReceivedAmount(), obj.getAccountType(),
						obj.getAccountNo()));
			} else {
				cb.get(0).setBankBalance(cb.get(0).getBankBalance() + obj.getReceivedAmount());
				closingBalanceRepo.save(cb.get(0));
			}
			List<ClosingBalanceTable> cbData = closingBalanceRepo
					.findByOfficeName(obj.getOfficeName()).stream().filter(item -> item.getCashBalance() == null
							&& item.getAccNo().equals(obj.getAccountNo()) && item.getDate().isAfter(obj.getDate()))
					.collect(Collectors.toList());
			cbData.forEach(item -> {
				item.setBankBalance(item.getBankBalance() + obj.getReceivedAmount());
			});
			closingBalanceRepo.saveAll(cbData);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public AdjustmentReceiptVoucher getAdjustmentReceiptVoucherByContraId(String contraId) throws Exception {
		try {
			return adjustmentReceiptVoucherRepo.findByContraId(contraId);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
