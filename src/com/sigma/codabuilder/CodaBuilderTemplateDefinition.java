/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codabuilder;

/**
 *
 * @author clance
 */
public class CodaBuilderTemplateDefinition {
  protected String providerCompany;
  protected String instanceType;
  protected String[] instanceNames;
  protected String[] clearedProps;  

  public CodaBuilderTemplateDefinition(String providerCompany, String instanceType, String[] instanceNames, String[] clearedProps) {
    this.providerCompany = providerCompany;
    this.instanceType = instanceType;
    this.instanceNames = instanceNames;
    this.clearedProps = clearedProps;
  }
  
  public CodaBuilderTemplateDefinition() {
    this.providerCompany = "";
    this.instanceType = "";
    this.instanceNames = new String[0];
    this.clearedProps = new String[0];
  }  

  public String getProviderCompany() {
    return providerCompany;
  }

  public void setProviderCompany(String providerCompany) {
    this.providerCompany = providerCompany;
  }

  public String getInstanceType() {
    return instanceType;
  }

  public void setInstanceType(String instanceType) {
    this.instanceType = instanceType;
  }

  public String[] getInstanceNames() {
    return instanceNames;
  }

  public void setInstanceNames(String[] instanceNames) {
    this.instanceNames = instanceNames;
  }

  public String[] getClearedProps() {
    return clearedProps;
  }

  public void setClearedProps(String[] clearedProps) {
    this.clearedProps = clearedProps;
  }
}
