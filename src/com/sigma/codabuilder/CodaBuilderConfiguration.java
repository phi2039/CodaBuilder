/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codabuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author clance
 */
public class CodaBuilderConfiguration extends Properties {
  protected final Map<String,CodaBuilderTemplateDefinition> templateDefs = new HashMap<>();
  protected final String templateStorePath;  

  // TODO: Lots of validation
  public CodaBuilderConfiguration(String fileName) throws FileNotFoundException, IOException {
    FileInputStream configFile = new FileInputStream(fileName);
    load(configFile);
    
    templateStorePath = getProperty("template.store_path","");
    String templateProvider = getProperty("template.provider_company","");
    String[] templateTypeNames = getProperty("template.types","").split(",");
    for (String type : templateTypeNames) {
      CodaBuilderTemplateDefinition templateDef = new CodaBuilderTemplateDefinition();
      templateDef.setProviderCompany(templateProvider);
      templateDef.setInstanceType(type);
      String instanceNamesString = getProperty("template." + type, null);
      if (instanceNamesString != null)
        templateDef.setInstanceNames(instanceNamesString.split(","));
      String clearedPropsString = getProperty("template." + type + ".clear_props", null);
      if (clearedPropsString != null)
        templateDef.setClearedProps(clearedPropsString.split(","));      
      templateDefs.put(type, templateDef);
    }
  }

  public CodaBuilderTemplateDefinition getTemplateDefinition(String templateType) {
    return templateDefs.get(templateType);
  }
  
  public String getTemplateStorePath() {
    return templateStorePath;
  }
  
  public String[] getTemplateTypes() {
    return templateDefs.keySet().toArray(new String[0]);
  }
}
