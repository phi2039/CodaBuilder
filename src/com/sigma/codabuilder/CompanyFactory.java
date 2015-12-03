/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codabuilder;

import java.util.List;

/**
 *
 * @author clance
 */
public class CompanyFactory {
  
  public void addCurrency(CurrencyDef currency) {
     throw new UnsupportedOperationException("Not implemented");
  }

  public void addDocumentType() {
     throw new UnsupportedOperationException("Not implemented");
  }
 
  public void createCompany(String code, String name, String shortName) {
     throw new UnsupportedOperationException("Not implemented");
//    CompanyService2 cmpSvc = new CompanyService2(router);
//    // Create the skeleton company
//    cmpSvc.createCompany(companyCode, name, shortName);
//    // Log in to the new company and create the default currencies
//    router.changeCompany(companyCode);
//    CurrencyService currSvc = new CurrencyService(router);
//    for (CurrencyDef c : currencies) {
//      currSvc.createObject(companyCode, c.code, c.name, c.shortName, c.decimalPlaces, c.singularUnit, c.pluralUnit, c.abbrevUnit, c.singularDecimal, c.pluralDecimal, c.abbrevDecimal);
//    }
//    
//    BalanceMasterService2 bmSvc = new BalanceMasterService2(router);
//    bmSvc.addBalanceMaster(companyCode, "ACTUAL", "Actual Balance", "Actual Balance", 2, true, false);
//    bmSvc.addBalanceMaster(companyCode, "TURNOVER", "Turnover Balance", "Turnover Balance", 2, true, false);
//    
//    DocumentMasterService2 dmSvc = new DocumentMasterService2(router);
//    dmSvc.addDocumentMaster(companyCode, "DISPERSE", "Disperse", "Disperse");
//    dmSvc.addDocumentMaster(companyCode, "MATCHING", "Matching", "Matching");
//    
//    List<CompanyBalance> balances = Arrays.asList(
//            new CompanyBalance(CompanyBalanceType.HOME_ACTUALS, true, (short)0, Arrays.asList((short)0, (short)15)), 
//            new CompanyBalance(CompanyBalanceType.DUAL_ACTUALS, true, (short)0, Arrays.asList((short)0, (short)15)), 
//            new CompanyBalance(CompanyBalanceType.FOREIGN_ACTUALS, false, (short)0, null), 
//            new CompanyBalance(CompanyBalanceType.ELEMENT_ACTUALS, true, (short)1, Arrays.asList((short)0, (short)1)),
//            new CompanyBalance(CompanyBalanceType.HOME_TURNOVER, true, (short)0, Arrays.asList((short)0, (short)16)), 
//            new CompanyBalance(CompanyBalanceType.DUAL_TURNOVER, true, (short)0, Arrays.asList((short)0, (short)16)), 
//            new CompanyBalance(CompanyBalanceType.FOREIGN_TURNOVER, false, (short)0, null),
//            new CompanyBalance(CompanyBalanceType.ELEMENT_TURNOVER, false, (short)0, null), 
//            new CompanyBalance(CompanyBalanceType.QUANTITIES, true, (short)0, Arrays.asList((short)0))
//    );
//    
//    // Update the company with new values
//    List<CompanyPropertyValue> props = Arrays.asList(
//            new CompanyPropertyValue(CompanyProperty.HOME_CUR, "USD"), 
//            new CompanyPropertyValue(CompanyProperty.DUAL_CUR, "LOC"),
//            new CompanyPropertyValue(CompanyProperty.ACTUAL_BALANCE_CODE, "ACTUAL"),
//            new CompanyPropertyValue(CompanyProperty.MATCHING_DOCCODE, "MATCHING"),
//            new CompanyPropertyValue(CompanyProperty.DISPERSE_DOCCODE, "DISPERSE"),
//            new CompanyPropertyValue(CompanyProperty.TURNOVER_BALANCE_CODE, "TURNOVER"),
//            new CompanyPropertyValue(CompanyProperty.ADDRESS_CATEGORIES, "ADDRESS"),
//            new CompanyPropertyValue(CompanyProperty.ELEMENT_STATUSES, "STATUS"),
//            new CompanyPropertyValue(CompanyProperty.COMPANY_BALANCES, balances)
//    );
//    cmpSvc.updateCompany(companyCode, props);
//    
//  }
//  
//  private static void TestCompanyService(CodaRouter router) throws Exception {
//    CompanyService2 svc = new CompanyService2(router);
//    Object companyDef = svc.getCompanyDef("MODELCO");
//    
//    System.out.println(String.format("Company[%s] - Home Currency: %s, Dual Currency: %s", 
//            CompanyService2.getPropertyString(companyDef, CompanyProperty.COMPANY_CODE),
//            CompanyService2.getPropertyString(companyDef, CompanyProperty.HOME_CUR), 
//            CompanyService2.getPropertyString(companyDef, CompanyProperty.DUAL_CUR)));
//    List<CompanyBalance> balances = CompanyService2.getPropertyList(companyDef, CompanyProperty.COMPANY_BALANCES);
//    
//    for (CompanyBalance balance : balances) {
//      System.out.print(String.format("Balance[%s] - Required: %b, Level: %d", balance.getType().toString(), balance.isRequired(), balance.getElmLevel()));
//      if (balance.isRequired())
//      {
//        System.out.print(", Reporting Basis: ");
//        for (short basis : balance.getReportingBasis()) {
//          String bitmap = "";
//          for (short i = 0; i < 8; i++) {
//            bitmap += ((basis & (short)(0x1 << i)) > 0) ? "1" : "0";
//          }
//          System.out.print(bitmap + "|");
//        }
//      }
//      System.out.println();
//    }    
  }
}
