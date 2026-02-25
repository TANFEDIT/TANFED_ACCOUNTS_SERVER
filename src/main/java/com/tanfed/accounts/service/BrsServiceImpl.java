package com.tanfed.accounts.service;

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

import com.tanfed.accounts.components.MasterDataManager;
import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.dto.DataForBrs;
import com.tanfed.accounts.entity.BRS;
import com.tanfed.accounts.entity.ClosingBalanceTable;
import com.tanfed.accounts.model.BankInfo;
import com.tanfed.accounts.repository.BrsRepo;
import com.tanfed.accounts.repository.ClosingBalanceRepo;

@Service
public class BrsServiceImpl implements BrsService {

	@Autowired
	private BrsRepo brsRepo;

	@Autowired
	private ClosingBalanceRepo closingBalanceRepo;

	@Autowired
	private MasterDataManager masterDataManager;
	private static Logger logger = LoggerFactory.getLogger(BrsServiceImpl.class);

	@Override
	public ResponseEntity<String> saveBrsEntity(BRS obj, String jwt) throws Exception {
		try {
			obj.getPassbookTranscations().forEach(i -> {
				i.setBrs(obj);
				i.setParticularsType("Passbook");
			});
			obj.getDaybookTranscations().forEach(i -> {
				i.setBrs(obj);
				i.setParticularsType("Daybook");
			});
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.setEmpId(Arrays.asList(empId));
			brsRepo.save(obj);
			return new ResponseEntity<String>("Created Successfully", HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public DataForBrs fetchDataForBrs(String officeName, String accountType, String accountNo, String branchName,
			String jwt, LocalDate reconciliationDate) throws Exception {
		try {
			DataForBrs data = new DataForBrs();
			if (officeName != null && !officeName.isEmpty()) {
				List<BankInfo> bankInfoData = masterDataManager.fetchBankInfoData(jwt).stream()
						.filter(i -> i.getOfficeName().equals(officeName)).collect(Collectors.toList());
				data.setAccountTypeList(
						bankInfoData.stream().filter(i -> !i.getAccountType().equals("Non PDS A/c Fert"))
								.map(i -> i.getAccountType()).collect(Collectors.toSet()));
				if (accountType != null && !accountType.isEmpty()) {
					data.setAccountNoList(bankInfoData.stream().filter(i -> i.getAccountType().equals(accountType))
							.map(i -> i.getAccountNumber()).collect(Collectors.toSet()));
					if (accountNo != null && !accountNo.isEmpty()) {
						data.setBranchNameList(bankInfoData.stream()
								.filter(i -> i.getAccountType().equals(accountType)
										&& i.getAccountNumber().equals(Long.valueOf(accountNo)))
								.map(i -> i.getBranchName()).collect(Collectors.toSet()));
						logger.info(branchName);
						if (branchName != null && !branchName.isEmpty()) {
							List<ClosingBalanceTable> cb;
							int n = 0;
							do {
								LocalDate prevDate = reconciliationDate.minusDays(n++);
								cb = closingBalanceRepo.findByOfficeNameAndDate(officeName, prevDate).stream()
										.filter(item -> item.getCashBalance() == null
												&& item.getAccType().equals(accountType)
												&& item.getAccNo().equals(Long.valueOf(accountNo))
												&& item.getBranchName().equals(branchName))
										.collect(Collectors.toList());
								if (n == 365) {
									break;
								}
							} while (cb.isEmpty());
							data.setDaybookBalance(cb.isEmpty() ? 0.0 : cb.get(0).getBankBalance());
						}
					}
				}
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
