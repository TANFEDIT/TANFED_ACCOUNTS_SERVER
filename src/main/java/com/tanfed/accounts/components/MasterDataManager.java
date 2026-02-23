package com.tanfed.accounts.components;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tanfed.accounts.model.AccountsMaster;
import com.tanfed.accounts.model.BankInfo;
import com.tanfed.accounts.model.BeneficiaryMaster;
import com.tanfed.accounts.model.BuyerFirmInfo;
import com.tanfed.accounts.model.ProductMaster;
import com.tanfed.accounts.model.SupplierInfo;
import com.tanfed.accounts.model.TaxInfo;
import com.tanfed.accounts.service.MasterService;

@Component
public class MasterDataManager {

	@Autowired
	private MasterService masterService;

	public static List<AccountsMaster> accMasterData = new ArrayList<AccountsMaster>();
	public static List<TaxInfo> taxData = new ArrayList<TaxInfo>();
	public static List<BeneficiaryMaster> beneficiaryData = new ArrayList<BeneficiaryMaster>();
	public static List<BankInfo> bankData = new ArrayList<BankInfo>();
	public static List<ProductMaster> productData = new ArrayList<ProductMaster>();
	public static List<BuyerFirmInfo> buyerData = new ArrayList<BuyerFirmInfo>();
	public static List<SupplierInfo> supplierData = new ArrayList<SupplierInfo>();

	public List<AccountsMaster> fetchAccMasterData(String jwt) {
		if (accMasterData.isEmpty()) {
			accMasterData.addAll(masterService.accountsMasterListHandler(jwt));
		}
		return accMasterData;
	}

	public List<TaxInfo> fetchTaxInfoData(String jwt) {
		if (taxData.isEmpty()) {
			taxData.addAll(masterService.findTaxInfoListHandler(jwt));
		}
		return taxData;
	}

	public List<BeneficiaryMaster> fetchBeneficiaryMasterData(String jwt) {
		if (beneficiaryData.isEmpty()) {
			beneficiaryData.addAll(masterService.getBeneficiaryListByOfficeName(jwt));
		}
		return beneficiaryData;
	}

	public List<BankInfo> fetchBankInfoData(String jwt) {
		if (bankData.isEmpty()) {
			bankData.addAll(masterService.getBankInfoByOfficeNameHandler(jwt));
		}
		return bankData;
	}

	public List<ProductMaster> fetchProductMasterData(String jwt) throws Exception {
		if (productData.isEmpty()) {
			productData.addAll(masterService.getProductDataHandler(jwt));
		}
		return productData;
	}

	public List<BuyerFirmInfo> fetchBuyerFirmInfoData(String jwt) throws Exception {
		if (buyerData.isEmpty()) {
			buyerData.addAll(masterService.getBuyerDataByOfficeNameHandler(jwt));
		}
		return buyerData;
	}

	public List<SupplierInfo> fetchSupplierInfoData(String jwt) throws Exception {
		if (supplierData.isEmpty()) {
			supplierData.addAll(masterService.getSupplierInfoHandler(jwt));
		}
		return supplierData;
	}
}
