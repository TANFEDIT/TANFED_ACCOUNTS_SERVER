package com.tanfed.accounts.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.tanfed.accounts.model.AccountsMaster;
import com.tanfed.accounts.model.BankInfo;
import com.tanfed.accounts.model.BeneficiaryMaster;
import com.tanfed.accounts.model.BuyerFirmInfo;
import com.tanfed.accounts.model.ProductMaster;
import com.tanfed.accounts.model.SupplierInfo;
import com.tanfed.accounts.model.TaxInfo;



@FeignClient(name = "BASICINFO-SERVICE", url = "http://localhost:8082")
public interface MasterService {

	@GetMapping("/api/accountsmaster/fetchaccountsmasterlist")
	public List<AccountsMaster> accountsMasterListHandler(@RequestHeader("Authorization") String jwt);
	
	@GetMapping("/api/accountsmaster/fetchtaxinfolist")
	public List<TaxInfo> findTaxInfoListHandler(@RequestHeader("Authorization") String jwt);
	
	@GetMapping("/api/accountsmaster/fetchbeneficiarymasterlist")
	public List<BeneficiaryMaster> getBeneficiaryListByOfficeName(@RequestHeader("Authorization") String jwt, @RequestParam String officeName);
	
	@GetMapping("/api/accountsmaster/fetchbeneficiaryname")
	public List<String> getBeneficiaryNameListByOfficeName(@RequestHeader("Authorization") String jwt, @RequestParam String officeName);
	
	@GetMapping("/api/basic-info/fetchbanklist")
	public List<BankInfo> getBankInfoByOfficeNameHandler(@RequestHeader("Authorization") String jwt, @RequestParam String officeName);
	
	@GetMapping("/api/inventrymaster/fetchproductdata")
	public List<ProductMaster> getProductDataHandler(@RequestHeader("Authorization") String jwt) throws Exception;
	
	@GetMapping("/api/basic-info/fetchBuyerbyoffice")
	public List<BuyerFirmInfo> getBuyerDataByOfficeNameHandler(@RequestParam String officeName, @RequestHeader("Authorization") String jwt) throws Exception;
	
	@GetMapping("/api/basic-info/fetchsupplierdata")
	public SupplierInfo getSupplierInfoBySupplierNameHandler(@RequestParam String supplierName, @RequestHeader("Authorization") String jwt) throws Exception;
	
	@GetMapping("/api/basic-info/fetchbuyerfirmdata")
	public BuyerFirmInfo getBuyerFirmByFirmNameHandler(@RequestHeader("Authorization") String jwt, @RequestParam String ifmsId) throws Exception;
	
	@GetMapping("/api/inventrymaster/fetchproduct")
	public ProductMaster getProductDataByProductNameHandler(@RequestHeader("Authorization") String jwt, @RequestParam String productName) throws Exception;
}
