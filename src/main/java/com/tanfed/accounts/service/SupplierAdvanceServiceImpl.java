package com.tanfed.accounts.service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanfed.accounts.components.MasterDataManager;
import com.tanfed.accounts.config.JwtTokenValidator;
import com.tanfed.accounts.entity.SupplierAdvance;
import com.tanfed.accounts.model.BeneficiaryMaster;
import com.tanfed.accounts.model.JvAndPvObj;
import com.tanfed.accounts.model.ProductMaster;
import com.tanfed.accounts.model.TermsPrice;
import com.tanfed.accounts.repository.SupplierAdvanceRepo;
import com.tanfed.accounts.response.DataForSupplierAdvance;
import com.tanfed.accounts.utils.CodeGenerator;

@Service
public class SupplierAdvanceServiceImpl implements SupplierAdvanceService {

	@Autowired
	private MasterDataManager masterService;

	@Autowired
	private SupplierAdvanceRepo supplierAdvanceRepo;

	@Autowired
	private InventryService inventryService;

	@Autowired
	private CodeGenerator codeGenerator;

	private static Logger logger = LoggerFactory.getLogger(SupplierAdvanceServiceImpl.class);

	@Override
	public ResponseEntity<String> saveSupplierAdvance(SupplierAdvance obj, String jwt) throws Exception {
		try {
			String empId = JwtTokenValidator.getEmailFromJwtToken(jwt);
			obj.setEmpId(Arrays.asList(empId));
			String supplierAdvanceNo = codeGenerator.generateSupplierAdvanceNo(obj.getActivity());
			obj.setSupplierAdvanceNo(supplierAdvanceNo);
			obj.setVoucherStatus("Pending");
			obj.setAvlAmountForCheckMemo(obj.getNetAdvanceValueAfterOthers());
			obj.setAvlQtyForCheckMemo(obj.getQty());
			supplierAdvanceRepo.save(obj);
			return new ResponseEntity<String>(
					"Created Successfully" + "\nSupplier Advance Number: " + supplierAdvanceNo, HttpStatus.CREATED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public DataForSupplierAdvance getDataForSupplierAdvance(String officeName, String activity, String supplierName,
			String productName, String termsMonth, String termsNo, String advanceMonth, String jwt, LocalDate date)
			throws Exception {
		try {
			DataForSupplierAdvance data = new DataForSupplierAdvance();
			if (advanceMonth != null && !advanceMonth.isEmpty()) {
				data.setSagData(supplierAdvanceRepo.findAll().stream().filter(item -> {
					String month = String.format("%s%s%04d",
							item.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH), " ",
							item.getDate().getYear());
					return month.equals(advanceMonth) && !item.getVoucherStatus().equals("Rejected");
				}).collect(Collectors.toList()));
			}
			if (activity != null && !activity.isEmpty()) {
				logger.info(activity);
				List<ProductMaster> productDataHandler = masterService.fetchProductMasterData(jwt);
				data.setSupplierNameList(productDataHandler.stream().filter(
						item -> item.getActivity().equals(activity) && !item.getSupplierName().startsWith("TANFED"))
						.map(ProductMaster::getSupplierName).collect(Collectors.toSet()));

				if (supplierName != null && !supplierName.isEmpty()) {
					data.setProductNameList(productDataHandler.stream().filter(
							item -> item.getActivity().equals(activity) && item.getSupplierName().equals(supplierName))
							.map(ProductMaster::getProductName).collect(Collectors.toList()));

					List<BeneficiaryMaster> beneficiaryMaster = masterService.fetchBeneficiaryMasterData(jwt).stream()
							.filter(item -> item.getOfficeName().equals(officeName)
									&& item.getBeneficiaryName().equals(supplierName))
							.collect(Collectors.toList());
					if (beneficiaryMaster.isEmpty()) {
						throw new Exception("Create Beneficiary Master For " + supplierName);
					}
					data.setAccountsNo(beneficiaryMaster.get(0).getAccountNo());

					if (productName != null && !productName.isEmpty()) {
						ProductMaster productMaster = masterService.fetchProductMasterData(jwt).stream()
								.filter(i -> i.getProductName().equals(productName)).collect(Collectors.toList())
								.get(0);
						data.setProductCategory(productMaster.getProductCategory());
						data.setProductGroup(productMaster.getProductGroup());
						data.setPacking(productMaster.getPacking());
						data.setStandardUnits(productMaster.getStandardUnits());
						data.setSupplierGst(productMaster.getSupplierGst());
						data.setHsnCode(productMaster.getHsnCode());
						data.setTermsMonthList(inventryService.getTermsDataHandler(jwt).stream()
								.filter(item -> item.getMasterData().getProductName().equals(productName)
										&& !item.getMasterData().getValidFrom().isAfter(date)
										&& !item.getMasterData().getValidTo().isBefore(date))
								.map(item -> item.getMasterData().getTermsForMonth()).collect(Collectors.toList()));
						if (termsMonth != null && !termsMonth.isEmpty()) {
							data.setTermsNoList(inventryService.getTermsDataHandler(jwt).stream()
									.filter(item -> item.getMasterData().getProductName().equals(productName)
											&& item.getMasterData().getTermsForMonth().equals(termsMonth))
									.map(item -> item.getTermsNo()).collect(Collectors.toList()));
							if (termsNo != null && !termsNo.isEmpty()) {
								TermsPrice termsPrice = inventryService.getTermsDataByTermsNoHandler(termsNo, jwt);
								data.setPurchasePricing(termsPrice.getPurchaseTermsPricing());
							}
						}
					}
				}
			}
			return data;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Autowired
	private PaymentVoucherService paymentVoucherService;

	@Autowired
	private JournalVoucherService journalVoucherService;

	@Override
	public ResponseEntity<String> updatePvAndJv(JvAndPvObj obj, String supplierAdvanceNo, String jwt) throws Exception {
		try {
			if (obj.getPv() != null) {
				SupplierAdvance supplierAdvance = supplierAdvanceRepo.findBySupplierAdvanceNo(supplierAdvanceNo).get();
				obj.getPv().setIdNo(supplierAdvanceNo);
				paymentVoucherService.savePaymentVoucher(obj.getPv(), jwt);
				supplierAdvance.setPvData(obj.getPv());
				supplierAdvanceRepo.save(supplierAdvance);
			}
			if (obj.getJv() != null) {
				SupplierAdvance supplierAdvance = supplierAdvanceRepo.findBySupplierAdvanceNo(supplierAdvanceNo).get();
				obj.getJv().setIdNo(supplierAdvanceNo);
				journalVoucherService.saveJournalVoucher(obj.getJv(), jwt);
				supplierAdvance.setJvData(obj.getJv());
				supplierAdvanceRepo.save(supplierAdvance);
			}
			return new ResponseEntity<String>("Updated Successfully", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public List<SupplierAdvance> fetchOutstandingAdvancesByProduct(String productName) throws Exception {
		try {
			if (productName != null && !productName.isEmpty()) {
				return supplierAdvanceRepo.findByProductName(productName);
			} else {
				return supplierAdvanceRepo.findAll();
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public void revertPvAndJv(SupplierAdvance obj, String jwt) throws Exception {
		try {
			if (obj != null) {
				obj.getPvData().setVoucherStatus(obj.getVoucherStatus());
				paymentVoucherService.revertSupplierAdvancePv(obj.getPvData(), jwt);
				obj.getJvData().setVoucherStatus(obj.getVoucherStatus());
				journalVoucherService.revertSupplierAdvanceJv(obj.getJvData(), jwt);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public void updateAvlQtyAndAmount(String supplierAdvanceNo, double qty, double amount) throws Exception {
		try {
			SupplierAdvance supplierAdvance = supplierAdvanceRepo.findBySupplierAdvanceNo(supplierAdvanceNo).get();
			supplierAdvance.setAvlAmountForCheckMemo(supplierAdvance.getAvlAmountForCheckMemo() - amount);
			supplierAdvance.setAvlQtyForCheckMemo(supplierAdvance.getAvlQtyForCheckMemo() - qty);
			supplierAdvanceRepo.save(supplierAdvance);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}
}
