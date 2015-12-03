/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codabuilder;

import com.sigma.codaclient.codarouter.CodaRouter;
import com.sigma.codaclient.common.CodaClientConfiguration;
import com.sigma.codaclient.common.CodaFrameworkConfiguration;
import com.sigma.codaclient.services.company.CompanyAddress;

/**
 *
 * @author clance
 */
public class CodaBuilderApp {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    
    try {
      // Make sure namespaces are specified in response files (needed for marshaller) - Global setting
      CodaRouter.setNamespaces(true);

      CodaClientConfiguration clientConfig = new CodaClientConfiguration("config/client.properties");
      CodaBuilderConfiguration builderConfig = new CodaBuilderConfiguration("config/builder.properties");
      CodaFrameworkConfiguration frameworkConfig = new CodaFrameworkConfiguration("config/framework.properties");

      CodaBuilder builder = new CodaBuilder(clientConfig, builderConfig, frameworkConfig);
      
//      builder.exportTemplateObjects("cgidev");

      // TODO: login to "no company" for the first company, then log-in to a real company
//      builder.initInstance("codatunnel", builderConfig.getTemplateStorePath());

      CompanyAddress address = new CompanyAddress();
      address.setLine(1, "123 Main");
      address.setLine(2, "Suite 505");
      address.setPostCode("123456");
      address.setCountryCode("USA");
      builder.createCompany("codatunnel", builderConfig.getTemplateStorePath(), "MODELCO3", "Model Company", "Model Company", "USD", "USD", address, 2015, 2020);
      
    } catch (Exception ex) {
			System.out.print(ex.getLocalizedMessage());
			ex.printStackTrace();
    }
  }
}
