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

import com.tanfed.accounts.components.InterTransferReceipts;
import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.entity.AdjustmentReceiptVoucher;
import com.tanfed.accounts.entity.ClosingBalanceTable;
import com.tanfed.accounts.entity.PaymentVoucher;
import com.tanfed.accounts.model.BankInfo;
import com.tanfed.accounts.model.BeneficiaryMaster;
import com.tanfed.accounts.repository.ClosingBalanceRepo;
import com.tanfed.accounts.repository.PaymentVoucherRepo;
import com.tanfed.accounts.response.DataForPaymentVoucher;
import com.tanfed.accounts.utils.CodeGenerator;

@Service
public class PaymentVoucherServiceImpl implements PaymentVoucherService {

	@Autowired
	private PaymentVoucherRepo paymentVoucherRepo;

	@Autowired
	private CodeGenerator codeGenerator;

	@Override
	public ResponseEntity<String> savePaymentVoucher(PaymentVoucher obj, String jwt) throws Exception {
		try {
			String code;
			do {
				code = "PV" + codeGenerator.voucherNoGenerator();
			} while (paymentVoucherRepo.findByVoucherNo(code).isPresent());
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.setEmpId(Arrays.asList(empId));
			obj.setVoucherNo(code);
			paymentVoucherRepo.save(obj);

			return new ResponseEntity<String>("Created Successfully" + "\n Voucher Number : " + code,
					HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> editPaymentVoucher(PaymentVoucher obj, String jwt) throws Exception {
		try {
			PaymentVoucher paymentVoucher = paymentVoucherRepo.findById(obj.getId()).get();
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			paymentVoucher.getEmpId().add(empId);
			paymentVoucher.setMainHead(obj.getMainHead());
			paymentVoucher.setSubHead(obj.getSubHead());
			paymentVoucher.setNarration(obj.getNarration());

			paymentVoucherRepo.save(paymentVoucher);

			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public PaymentVoucher getVoucherByVoucherNo(String voucherNo) throws Exception {
		try {
			PaymentVoucher paymentVoucher = paymentVoucherRepo.findByVoucherNo(voucherNo).get();
			if (paymentVoucher == null) {
				throw new FileNotFoundException("No data found!");
			}
			return paymentVoucher;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<PaymentVoucher> getVoucherByOfficeName(String officeName) throws Exception {
		try {
			return paymentVoucherRepo.findByOfficeName(officeName);

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<PaymentVoucher> getVouchersForCashUpdate(String officeName, LocalDate fromDate, LocalDate toDate)
			throws Exception {
		try {
			return paymentVoucherRepo.findByOfficeName(officeName).stream().filter(item -> {
				return item.getPvType().equals("Cash Payment Voucher") && !item.getDate().isBefore(fromDate)
						&& item.getVoucherStatus().equals("Approved") && !item.getDate().isAfter(toDate)
						&& item.getPaidOn() == null;
			}).collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> paymentVoucherCashUpdate(List<PaymentVoucher> obj, String jwt) throws Exception {
		try {
			obj.forEach(item -> {
				PaymentVoucher paymentVoucher = paymentVoucherRepo.findById(item.getId()).get();
				String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
				paymentVoucher.getEmpId().add(empId);
				paymentVoucher.setPaidOn(item.getPaidOn());
				paymentVoucherRepo.save(paymentVoucher);
			});

			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<PaymentVoucher> getVouchersForOnlineUpdate(String officeName, LocalDate fromDate, LocalDate toDate)
			throws Exception {
		try {
			return paymentVoucherRepo.findByOfficeName(officeName).stream().filter(item -> {
				return item.getPvType().equals("Online Payment Voucher") && !item.getDate().isBefore(fromDate)
						&& !item.getDate().isAfter(toDate) && item.getUtrNumber() == null && item.getStatus() == null
						&& item.getOnlineDate() == null && item.getVoucherStatus().equals("Approved");
			}).collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private AdjustmentReceiptVoucherService adjustmentReceiptVoucherService;

	@Autowired
	private InterTransferReceipts contraVoucherService;

	@Override
	public ResponseEntity<String> paymentVoucherOnlineUpdate(List<PaymentVoucher> obj, String jwt) throws Exception {
		try {
			obj.forEach(item -> {
				PaymentVoucher paymentVoucher = paymentVoucherRepo.findById(item.getId()).get();
				String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
				paymentVoucher.getEmpId().add(empId);
				paymentVoucher.setUtrNumber(item.getUtrNumber());
				paymentVoucher.setOnlineDate(item.getOnlineDate());
				paymentVoucher.setStatus(item.getStatus());
				if (item.getStatus().equals("Amount Returned")) {
					try {
						AdjustmentReceiptVoucher AdjustmentReceiptVoucher = new AdjustmentReceiptVoucher(null, null,
								item.getVoucherNo(), item.getOfficeName(), null, null, null, null, item.getOnlineDate(),
								"Reversed", null, null, item.getAmount(), "Other Online", item.getUtrNumber(),
								item.getOnlineDate(), item.getBranchName(), item.getMainHead(), item.getSubHead(),
								item.getNarration(), "No", null, item.getAccountType(), item.getAccountNo(),
								item.getBranchName(), item.getOnlineDate(), null, null, null, null);
						adjustmentReceiptVoucherService.saveAdjustmentReceiptVoucher(AdjustmentReceiptVoucher, jwt);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if (item.getContraEntry().equals("Yes")) {
						try {
							contraVoucherService.createInterTransferAdjVoucher(item, jwt);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				paymentVoucherRepo.save(paymentVoucher);
			});

			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<PaymentVoucher> getVouchersForChequeUpdate(String officeName, LocalDate fromDate, LocalDate toDate)
			throws Exception {
		try {
			return paymentVoucherRepo.findByOfficeName(officeName).stream().filter(item -> {
				return item.getPvType().equals("Cheque Payment Voucher") && !item.getDate().isBefore(fromDate)
						&& !item.getDate().isAfter(toDate) && item.getChequeNumber() == null
						&& item.getChequeDate() == null && item.getIssueBankName() == null
						&& item.getVoucherStatus().equals("Approved") && item.getSettledDate() == null
						&& item.getStatus() == null;
			}).collect(Collectors.toList());
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> paymentVoucherChequeUpdate(List<PaymentVoucher> obj, String jwt) throws Exception {
		try {
			obj.forEach(item -> {
				PaymentVoucher paymentVoucher = paymentVoucherRepo.findById(item.getId()).get();
				String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
				paymentVoucher.getEmpId().add(empId);
				paymentVoucher.setChequeDate(item.getChequeDate());
				paymentVoucher.setChequeNumber(item.getChequeNumber());
				paymentVoucher.setIssueBankName(item.getIssueBankName());
				paymentVoucher.setSettledDate(item.getSettledDate());
				paymentVoucher.setStatus(item.getStatus());
				if (!item.getStatus().equals("Amount Returned")) {
					if (item.getContraEntry().equals("Yes")) {
						try {
							contraVoucherService.createInterTransferAdjVoucher(item, jwt);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				paymentVoucherRepo.save(paymentVoucher);
			});

			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private MasterService masterService;

	@Override
	public DataForPaymentVoucher getDataForPaymentVoucher(String officeName, String accountType, String accountNo,
			String jwt, String mainHead, String paidTo, LocalDate date, String pvType) throws Exception {
		try {
			DataForPaymentVoucher data = new DataForPaymentVoucher();
			if (officeName != null && !officeName.isEmpty()) {
				data.setBeneficiaryNameList(masterService.getBeneficiaryListByOfficeName(jwt, officeName).stream()
						.filter(item -> item.getBeneficiaryApplicableToHoAccount().contains(mainHead))
						.map(item -> item.getBeneficiaryName()).collect(Collectors.toSet()));

				List<BankInfo> bankInfo = masterService.getBankInfoByOfficeNameHandler(jwt, officeName);
				List<PaymentVoucher> pendingPvs = paymentVoucherRepo.findPendingDataByOfficeName(officeName).stream()
						.filter(item -> item.getPvType().equals(pvType)).collect(Collectors.toList());
				if (!pendingPvs.isEmpty()) {
					throw new Exception("Approve previous Payment voucher!");
				}
				if (paidTo != null && !paidTo.isEmpty()) {
					List<BeneficiaryMaster> collect = masterService.getBeneficiaryListByOfficeName(jwt, officeName)
							.stream().filter(item -> item.getBeneficiaryName().equals(paidTo))
							.collect(Collectors.toList());
					if (collect.isEmpty()) {
						throw new Exception("No Beneficiary Found For " + paidTo + " in " + officeName);
					}
					data.setBeneficiaryAccountNo(collect.get(0).getAccountNo());
				}
				data.setAccountTypeList(bankInfo.stream().map(BankInfo::getAccountType).collect(Collectors.toSet()));

				if (accountType != null && !accountType.isEmpty()) {
					data.setAccountNumList(bankInfo.stream().filter(item -> item.getAccountType().equals(accountType))
							.map(BankInfo::getAccountNumber).collect(Collectors.toList()));

					if (accountNo != null && !accountNo.isEmpty()) {
						data.setBranchName(bankInfo.stream()
								.filter(item -> item.getAccountType().equals(accountType)
										&& item.getAccountNumber().equals(Long.valueOf(accountNo)))
								.map(BankInfo::getBranchName).collect(Collectors.toList()).get(0));
					}
				}
				if (pvType.equals("Cash Payment Voucher") || pvType.equals("Online Payment Voucher")
						|| pvType.equals("Cheque Payment Voucher")) {
					data.setBalance(getAvlBalance(pvType, date, officeName, accountNo));
				}
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private Double getAvlBalance(String pvType, LocalDate date, String officeName, String accountNo) {
		if (pvType.equals("Cash Payment Voucher")) {
			List<ClosingBalanceTable> obData;
			int n = 0;
			do {
				LocalDate previousDate = date.minusDays(n++);
				obData = closingBalanceRepo.findByOfficeNameAndDate(officeName, previousDate).stream()
						.filter(item -> item.getCashBalance() != null).collect(Collectors.toList());
			} while (obData.isEmpty());
			return obData.get(0).getCashBalance();
		} else {
			if (accountNo != null && !accountNo.isEmpty()) {
				List<ClosingBalanceTable> obData;
				int n = 0;
				do {
					LocalDate previousDate = date.minusDays(n++);
					obData = closingBalanceRepo.findByOfficeNameAndDate(officeName, previousDate).stream()
							.filter(item -> item.getAccNo() != null && item.getAccNo().equals(Long.valueOf(accountNo)))
							.collect(Collectors.toList());
				} while (obData.isEmpty());
				return obData.get(0).getBankBalance();
			} else {
				return 0.0;
			}
		}
	}

	@Autowired
	private ClosingBalanceRepo closingBalanceRepo;

	@Override
	public void updateClosingBalance(PaymentVoucher obj) throws Exception {
		try {
			if (obj.getPvType().equals("Cash Payment Voucher")) {
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
									obj.getAmount(), null, null, null));
							break;
						}
					}
					closingBalanceRepo.save(new ClosingBalanceTable(null, obj.getOfficeName(), obj.getDate(),
							cb.get(0).getCashBalance() - obj.getAmount(), null, null, null));
				} else {
					cb.get(0).setCashBalance(cb.get(0).getCashBalance() - obj.getAmount());
					closingBalanceRepo.save(cb.get(0));
				}
				updateCbAfterDate(obj.getOfficeName(), obj.getDate(), obj.getAmount(), "cash", null);
			} else {
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
							closingBalanceRepo.save(new ClosingBalanceTable(null, obj.getOfficeName(), obj.getDate(),
									null, obj.getAmount(), obj.getAccountType(), obj.getAccountNo()));
							break;
						}
					}
					closingBalanceRepo.save(new ClosingBalanceTable(null, obj.getOfficeName(), obj.getDate(), null,
							cb.get(0).getBankBalance() - obj.getAmount(), obj.getAccountType(), obj.getAccountNo()));
				} else {
					cb.get(0).setBankBalance(cb.get(0).getBankBalance() - obj.getAmount());
					closingBalanceRepo.save(cb.get(0));
				}
				updateCbAfterDate(obj.getOfficeName(), obj.getDate(), obj.getAmount(), "bank", obj.getAccountNo());
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private void updateCbAfterDate(String officeName, LocalDate date, Double amount, String type, Long accountNo) {
		if (type.equals("cash")) {
			List<ClosingBalanceTable> cbData = closingBalanceRepo.findByOfficeName(officeName).stream()
					.filter(item -> item.getCashBalance() != null && item.getDate().isAfter(date))
					.collect(Collectors.toList());
			cbData.forEach(item -> {
				item.setCashBalance(item.getCashBalance() - amount);
			});
			closingBalanceRepo.saveAll(cbData);
		} else {
			List<ClosingBalanceTable> cbData = closingBalanceRepo
					.findByOfficeName(officeName).stream().filter(item -> item.getCashBalance() == null
							&& item.getAccNo().equals(accountNo) && item.getDate().isAfter(date))
					.collect(Collectors.toList());
			cbData.forEach(item -> {
				item.setBankBalance(item.getBankBalance() - amount);
			});
			closingBalanceRepo.saveAll(cbData);
		}
	}

	@Override
	public void revertSupplierAdvancePv(PaymentVoucher obj, String jwt) throws Exception {
		try {
			PaymentVoucher paymentVoucher = paymentVoucherRepo.findById(obj.getId()).get();
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			paymentVoucher.getEmpId().add(empId);
			paymentVoucher.setVoucherStatus("Rejected");
			paymentVoucherRepo.save(paymentVoucher);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
