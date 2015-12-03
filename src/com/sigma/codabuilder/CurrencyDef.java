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
public class CurrencyDef {
  private final String code;
  private final String name;
  private final String shortName;
  private final String singularUnit;
  private final String pluralUnit;
  private final String singularDecimal;
  private final String pluralDecimal;
  private final String abbrevUnit;
  private final String abbrevDecimal;
  private final Integer decimalPlaces;

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getShortName() {
    return shortName;
  }

  public String getSingularUnit() {
    return singularUnit;
  }

  public String getPluralUnit() {
    return pluralUnit;
  }

  public String getSingularDecimal() {
    return singularDecimal;
  }

  public String getPluralDecimal() {
    return pluralDecimal;
  }

  public String getAbbrevUnit() {
    return abbrevUnit;
  }

  public String getAbbrevDecimal() {
    return abbrevDecimal;
  }

  public Integer getDecimalPlaces() {
    return decimalPlaces;
  }
    
  public CurrencyDef(String code, String name, String shortName, String singularUnit, String pluralUnit, String singularDecimal,
          String pluralDecimal, String abbrevUnit,String abbrevDecimal, Integer decimalPlaces) {

  this.code = code;
  this.name = name;
  this.shortName = shortName;
  this.singularUnit = singularUnit;
  this.pluralUnit = pluralUnit;
  this.singularDecimal = singularDecimal;
  this.pluralDecimal = pluralDecimal;
  this.abbrevUnit = abbrevUnit;
  this.abbrevDecimal = abbrevDecimal;
  this.decimalPlaces = decimalPlaces;       
  }    
}
