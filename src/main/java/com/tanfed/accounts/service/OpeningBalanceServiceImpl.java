package com.tanfed.accounts.service;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.entity.*;
import com.tanfed.accounts.model.*;
import com.tanfed.accounts.repository.*;

@Service
public class OpeningBalanceServiceImpl implements OpeningBalanceService {

	@Autowired
	private OpeningBalanceRepo openingBalanceRepo;

	@Autowired
	private ClosingBalanceRepo closingBalanceRepo;

	@Autowired
	private BillsGstObRepo billsGstObRepo;

	@Autowired
	private InventryService inventryService;

	@Override
	public ResponseEntity<String> saveOpeningBalance(OpeningBalanceDto obj, String jwt) throws Exception {
		try {

//			Setting values to entity object from dto and user data
			OpeningBalance data = new OpeningBalance();
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);

			if (obj.getOpeningBalanceFor().equals("Cash")) {
				data.setEmpId(Arrays.asList(empId));
				data.setOfficeName(obj.getOfficeName());

				data.setOpeningBalanceFor(obj.getOpeningBalanceFor());
				data.setOpDate(obj.getOpDate());

				data.setCashBalanceDate(obj.getCashBalanceDate());
				data.setAmount(obj.getAmount());
				data.setFiveHundred(obj.getFiveHundred());
				data.setTwoHundred(obj.getTwoHundred());
				data.setOneHundred(obj.getOneHundred());
				data.setFifty(obj.getFifty());
				data.setTwenty(obj.getTwenty());
				data.setTen(obj.getTen());
				data.setCoins(obj.getCoins());

//				Saving data in database
				openingBalanceRepo.save(data);
				closingBalanceRepo.save(new ClosingBalanceTable(null, obj.getOfficeName(), obj.getCashBalanceDate(),
						obj.getAmount(), null, null, null));
			}

			if (obj.getOpeningBalanceFor().equals("Bank")) {

				for (BankOpeningBalanceArray temp : obj.getBankData()) {
					OpeningBalance tempObj = new OpeningBalance();
					tempObj.setOfficeName(obj.getOfficeName());
					tempObj.setEmpId(Arrays.asList(empId));
					tempObj.setOpeningBalanceFor(obj.getOpeningBalanceFor());
					tempObj.setOpDate(obj.getOpDate());

					tempObj.setBankBalanceDate(temp.getBankBalanceDate());
					tempObj.setBankName(temp.getBankName());
					tempObj.setBranchName(temp.getBranchName());
					tempObj.setAccountType(temp.getAccountType());
					tempObj.setAccountNumber(temp.getAccountNumber());
					tempObj.setPassbookAmount(temp.getPassbookAmount());
					tempObj.setDayBookAmount(temp.getDayBookAmount());

//					Saving data in database
					openingBalanceRepo.save(tempObj);

					FundTransfer ftObj = new FundTransfer();
					ftObj.setOfficeName(obj.getOfficeName());
					ftObj.setBranchName(temp.getBranchName());
					ftObj.setAccountNo(temp.getAccountNumber());
					ftObj.setOpeningBalance(temp.getPassbookAmount());
					ftObj.setClosingBalance(temp.getPassbookAmount());
					ftObj.setDateOfTransfer(obj.getOpDate());
					ftObj.setCollection(0.0);
					ftObj.setTotal(0.0);
					ftObj.setCurrentTransfer(0.0);
					ftObj.setBankCharges(0.0);
					ftObj.setOthers(0.0);
					ftObj.setDate(temp.getBankBalanceDate());
					inventryService.saveFundTransferHandler(ftObj, jwt);
				}
			}

			return new ResponseEntity<String>("Created Successfully", HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> editOpeningBalance(OpeningBalance obj) throws Exception {
		try {
			OpeningBalance openingBalance = openingBalanceRepo.findById(obj.getId()).get();

			openingBalance.setOpDate(obj.getOpDate());
			openingBalance.setBankBalanceDate(obj.getBankBalanceDate());
			openingBalance.setBankName(obj.getBankName());
			openingBalance.setBranchName(obj.getBranchName());
			openingBalance.setAccountType(obj.getAccountType());
			openingBalance.setAccountNumber(obj.getAccountNumber());
			openingBalance.setPassbookAmount(obj.getPassbookAmount());
			openingBalance.setDayBookAmount(obj.getDayBookAmount());
			openingBalance.setCashBalanceDate(obj.getCashBalanceDate());
			openingBalance.setAmount(obj.getAmount());
			openingBalance.setFiveHundred(obj.getFiveHundred());
			openingBalance.setTwoHundred(obj.getTwoHundred());
			openingBalance.setOneHundred(obj.getOneHundred());
			openingBalance.setFifty(obj.getFifty());
			openingBalance.setTwenty(obj.getTwenty());
			openingBalance.setTen(obj.getTen());
			openingBalance.setCoins(obj.getCoins());

			openingBalanceRepo.save(openingBalance);

			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<OpeningBalance> getOpeningBalancesByOfficeName(String officeName) throws Exception {
		try {
			List<OpeningBalance> byOfficeName = openingBalanceRepo.findByOfficeName(officeName);

			if (byOfficeName == null) {
				throw new FileNotFoundException("No data found!");
			}
			return byOfficeName;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public ResponseEntity<String> saveBillsGstOpeningBalance(BillsGstOb obj, String jwt) throws Exception {
		try {
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.setEmpId(Arrays.asList(empId));
			billsGstObRepo.save(obj);
			return new ResponseEntity<String>("Created Successfully", HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private SundryCrObRepo sundryCrObRepo;

	@Autowired
	private SundryDrCrTableRepo sundryDrCrTableRepo;

	@Override
	public ResponseEntity<String> saveSundryCrOb(List<SundryCrOb> obj, String jwt) throws Exception {
		try {
			obj.forEach(item -> {
				String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
				item.setVoucherStatus("Pending");
				item.setEmpId(Arrays.asList(empId));
				sundryCrObRepo.save(item);
				SundryDrCrTable sundryCreditorsTable = sundryDrCrTableRepo
						.findByMonthAndSubHeadAndOfficeNameAndFormType("APRIL 2025", item.getSubHead(),
								item.getOfficeName(), "Cr");
				if (sundryCreditorsTable != null) {
					sundryCreditorsTable.setCb(sundryCreditorsTable.getCb() + item.getAmount());
				}
				sundryDrCrTableRepo.save(sundryCreditorsTable == null
						? new SundryDrCrTable(null, "Cr", "APRIL 2025", item.getSubHead(), item.getOfficeName(), 0.0,
								0.0, 0.0, 0.0, item.getAmount())
						: sundryCreditorsTable);
			});
			return new ResponseEntity<String>("Created Successfully!", HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private SundryDrObRepo sundryDrObRepo;

	@Override
	public ResponseEntity<String> saveSundryDrOb(List<SundryDrOb> obj, String jwt) throws Exception {
		try {
			obj.forEach(item -> {
				String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
				item.setVoucherStatus("Pending");
				item.setEmpId(Arrays.asList(empId));
				sundryDrObRepo.save(item);
				SundryDrCrTable sundryDebtorsTable = sundryDrCrTableRepo.findByMonthAndSubHeadAndOfficeNameAndFormType(
						"APRIL 2025", item.getSubHead(), item.getOfficeName(), "Dr");
				if (sundryDebtorsTable != null) {
					sundryDebtorsTable.setCb(sundryDebtorsTable.getCb() + item.getAmount());
				}
				sundryDrCrTableRepo.save(sundryDebtorsTable == null
						? new SundryDrCrTable(null, "Dr", "APRIL 2025", item.getSubHead(), item.getOfficeName(), 0.0,
								0.0, 0.0, 0.0, item.getAmount())
						: sundryDebtorsTable);
			});
			return new ResponseEntity<String>("Created Successfully!", HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public String accObValidate(String officeName) throws Exception {
		try {
			List<OpeningBalance> obData = openingBalanceRepo.findByOfficeName(officeName);
			List<OpeningBalance> cashOb = obData.stream().filter(item -> item.getOpeningBalanceFor().equals("Cash"))
					.collect(Collectors.toList());
			List<OpeningBalance> bankOb = obData.stream().filter(item -> item.getOpeningBalanceFor().equals("Bank"))
					.collect(Collectors.toList());
			if (obData.isEmpty()) {
				return "Update Cash and Bank Opening Balance!";
			} else if (cashOb.isEmpty()) {
				return "Update Cash Opening Balance!";
			} else if (bankOb.isEmpty()) {
				return "Update Bank Opening Balance!";
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public String billsAccObValidate(String officeName) throws Exception {
		try {
			List<SundryDrOb> sDrObData = sundryDrObRepo.findByOfficeName(officeName);
			List<SundryCrOb> sCrObData = sundryCrObRepo.findByOfficeName(officeName);
			if (sDrObData.isEmpty() && sCrObData.isEmpty()) {
				return "Update SundryDebtors and SundryCreditors Opening Balance!";
			} else if (sDrObData.isEmpty()) {
				return "Update SundryDebtors Opening Balance!";
			} else if (sCrObData.isEmpty()) {
				return "Update SundryCreditors Opening Balance!";
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public void updateClosingBalance(OpeningBalance obj) throws Exception {
		try {
			List<ClosingBalanceTable> cb = closingBalanceRepo
					.findByOfficeNameAndDate(obj.getOfficeName(), obj.getBankBalanceDate()).stream()
					.filter(item -> item.getCashBalance() == null && item.getAccType().equals(obj.getAccountType())
							&& item.getAccNo().equals(obj.getAccountNumber()))
					.collect(Collectors.toList());
			if (cb.isEmpty()) {
				closingBalanceRepo.save(new ClosingBalanceTable(null, obj.getOfficeName(), obj.getBankBalanceDate(),
						null, obj.getPassbookAmount(), obj.getAccountType(), obj.getAccountNumber()));
			} else {
				cb.get(0).setBankBalance(cb.get(0).getBankBalance() + obj.getPassbookAmount());
				closingBalanceRepo.save(cb.get(0));
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

}
