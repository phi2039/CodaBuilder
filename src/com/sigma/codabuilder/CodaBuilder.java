/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codabuilder;

import com.coda.efinance.schemas.balancemaster.BalanceMaster;
import com.coda.efinance.schemas.company.Company;
import com.coda.efinance.schemas.country.CountryMaster;
import com.coda.efinance.schemas.currency.Currency;
import com.coda.efinance.schemas.documentmaster.DocumentMaster;
import com.coda.efinance.schemas.entitymaster.Entity;
import com.sigma.codaclient.codarouter.CodaFileObjectStore;
import com.sigma.codaclient.codarouter.CodaLogicalServerType;
import com.sigma.codaclient.codarouter.CodaObjectStore;
import com.sigma.codaclient.codarouter.CodaObjectStoreType;
import com.sigma.codaclient.codarouter.CodaRouter;
import com.sigma.codaclient.common.CodaClientConfiguration;
import com.sigma.codaclient.common.CodaClientProperties;
import com.sigma.codaclient.common.CodaFrameworkConfiguration;
import com.sigma.codaclient.db.CodaDbConnection;
import com.sigma.codaclient.services.common.CodaGenericService;
import com.sigma.codaclient.services.common.CodaGlobalKey;
import com.sigma.codaclient.services.common.CodaKey;
import com.sigma.codaclient.services.common.CodaLocalKey;
import com.sigma.codaclient.services.common.CodaPropertyProvider;
import com.sigma.codaclient.services.common.CodaServiceDefinition;
import com.sigma.codaclient.services.company.CompanyAddress;
import com.sigma.codaclient.services.company.CompanyBalance;
import com.sigma.codaclient.services.company.CompanyBalanceType;
import com.sigma.codaclient.services.element.ElementKey;
import com.sigma.codaclient.services.year.YearService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author clance
 */
public class CodaBuilder {
  
  protected class ObjectChannel<T,K extends CodaKey> {
    public CodaObjectStore<T,K> sourceStore;
    public CodaObjectStore<T,K> destStore;
    
  }
  
  private final Map<String,CodaServiceDefinition> serviceDefs = new HashMap<>();
  private final Map<String,CodaRouter> routers = new HashMap<>();
  private final CodaClientConfiguration clientConfig;
  private final CodaBuilderConfiguration builderConfig;
  private final CodaFrameworkConfiguration frameworkConfig;
  private final String templateCompanyCode = "CB_TEMPLATE";
  
  public CodaBuilder(CodaClientConfiguration clientConfig, CodaBuilderConfiguration builderConfig, CodaFrameworkConfiguration frameworkConfig) {
    this.clientConfig = clientConfig;
    this.builderConfig = builderConfig;
    this.frameworkConfig = frameworkConfig;
  }
  
  private CodaRouter getRouter(String profileName, CodaLogicalServerType lsvType) throws Exception {
    if (routers.containsKey(profileName)) {
      CodaRouter router = routers.get(profileName);
      if (router.getLsvType() == lsvType)
        return router;
      router.changeLsv(lsvType);
      return router;
    }
    
    CodaClientProperties sourceProfile = clientConfig.getProfile(profileName);
    CodaRouter router = new CodaRouter(sourceProfile,true, false);      
    router.connect(lsvType);
    router.authenticate();
    routers.put(profileName, router);
    return router;
  }
  
  private CodaServiceDefinition getServiceDef(String name) throws Exception {
    if (serviceDefs.containsKey(name))
      return serviceDefs.get(name);
    
    CodaServiceDefinition def = frameworkConfig.getServiceDefinition(name);
    serviceDefs.put(name, def);
    return def;
  }
  
  private CodaObjectStore getStore(CodaObjectStoreType storeType, CodaServiceDefinition serviceDef, String spec) throws Exception {
    CodaObjectStore store = null;
    switch (storeType) {
      case FILE:
        return new CodaFileObjectStore(spec, serviceDef);
      case ROUTER:
        CodaRouter router = getRouter(spec, serviceDef.getLsvType());
        return new CodaGenericService(router, serviceDef);
      default:
        throw new Exception ("Unknown object store type");
    }
  }

  private CodaObjectStore getStore(CodaObjectStoreType storeType, String instanceType, String spec) throws Exception {
    CodaServiceDefinition serviceDef = getServiceDef(instanceType);
    return getStore(storeType, serviceDef, spec);
  }  
  
  protected <T,K extends CodaKey> ObjectChannel<T,K> createChannel(String instanceType, CodaObjectStoreType sourceType, String sourceName, CodaObjectStoreType destType, String destName) throws Exception {
    ObjectChannel<T,K> channel = new ObjectChannel<>();
    channel.sourceStore = getStore(sourceType, instanceType, sourceName);
    channel.destStore = getStore(destType, instanceType, destName);
    return channel;
  }

  protected CodaDbConnection getDbConnection(String profileName) throws Exception {
    CodaClientProperties clientProfile = clientConfig.getProfile(profileName);
    CodaDbConnection dbConn = new CodaDbConnection(clientProfile.getDbHost(), clientProfile.getDbPort(), clientProfile.getDbServiceId(), clientProfile.getDbUser().getUserName(), clientProfile.getDbUser().getPassword());
    dbConn.connect();
    return dbConn;
  }
  
  public void exportTemplateObjects(String profileName) throws Exception {
    
    exportTemplateObjects(builderConfig.getTemplateTypes(), CodaObjectStoreType.ROUTER, profileName, CodaObjectStoreType.FILE, builderConfig.getTemplateStorePath());
  }
  
  protected void exportTemplateObjects(String[] templateTypes, CodaObjectStoreType sourceType, String sourceName, CodaObjectStoreType destType, String destName) throws Exception {
  
    for (String templateType : templateTypes) {
      CodaServiceDefinition serviceDef = getServiceDef(templateType);
      CodaObjectStore sourceStore = getStore(sourceType, serviceDef, sourceName);
      CodaObjectStore destStore = getStore(destType, serviceDef, destName);

      CodaBuilderTemplateDefinition templateDef = builderConfig.getTemplateDefinition(templateType);
      
      // Create property map for key values
      String[] keyFields = serviceDef.getStoreKeyFields();
      Map<String,Object> keyValues = new HashMap<>();
      keyValues.put("companyCode", templateDef.getProviderCompany());

      // Change company for local templates
      Map<String,Object> newValues = new HashMap<>();
      if (keyFields.length > 1)
        newValues.put("cmpCode", templateCompanyCode);

      List<String> clearedProps = Arrays.asList(templateDef.getClearedProps());

      // For each object type, get the template object, change the required property values, and create it in the destination store
      for (String instanceName : templateDef.getInstanceNames()) {
        keyValues.put("code", instanceName); // Template object name
        CodaKey key = createKey(keyFields,keyValues);
        try {
          Object instance = sourceStore.getObject(key);
          // Change the company code if this is a local or more granular key
          if (CodaLocalKey.class.isAssignableFrom(key.getClass()))
            destStore.setProperties(instance, newValues);
          if (!clearedProps.isEmpty())
            destStore.clearProperties(instance, clearedProps);
          destStore.createObject(instance);
        } catch (Exception ex) { 
          System.out.print(ex.getMessage());
        // TODO: Error logging
        }
      }
    }
  }
  
  private CodaKey createKey(String[] keyFields, Map<String,Object> keyValues) throws Exception {
    CodaKey key = null;
    switch (keyFields.length) {
    case 1: // Assume GlobalKey
      key = new CodaGlobalKey((String)keyValues.get("code"));
      break;
    case 2: // Assume LocalKey
      key = new CodaLocalKey((String)keyValues.get("companyCode"), (String)keyValues.get("code"));
      break;
    case 3: // Try alternate key(s)...
      if (keyFields[2].toLowerCase().equals("level")) { // Is this an element key?
        key = new ElementKey((String)keyValues.get("companyCode"), (String)keyValues.get("code"), (Integer)keyValues.get("level"));
        break;
      }
      // Otherwise fall through...
    default:
      throw new Exception("Unknown key type.");
    }
    return key;
  }
  
  protected <T,K extends CodaKey> void createOrUpdateObject(CodaObjectStore<T,K> objectStore, T instance) throws Exception {

      K key = objectStore.getObjectKey(instance);
      T destObject = null;
      try { // See if the object already exists
        destObject = objectStore.getObject(key);
      } catch (Exception ex) { } // Entity doesn't exist
      
      // If the object exists, copy the source properties into it and update
      if (destObject != null) {
        CodaPropertyProvider props = objectStore.getPropertyProvider();
        // See if this object type has a timestamp
        // If it does, get the value and copy it to the new instance
        if (props.hasProperty("timeStamp")) {
          Object timeStamp = props.getValue("timeStamp", destObject);
          props.setValue("timeStamp", timeStamp, instance);
        }
        objectStore.updateObject(instance);
      }
      else {
        objectStore.createObject(instance);
      }
  }
  
  public void initInstance(String profileName, String templateStorePath) throws Exception {

    // Entities
    //////////////////////////////
    ObjectChannel<Entity,CodaGlobalKey> entityChannel = createChannel("entity", CodaObjectStoreType.FILE, templateStorePath, CodaObjectStoreType.ROUTER, profileName);

    // Create or update all entity masters
    for(Entity e : entityChannel.sourceStore.getObjectStream()) {
      createOrUpdateObject(entityChannel.destStore, e);
    }
    
    // Countries
    //////////////////////////////
    ObjectChannel<CountryMaster,CodaGlobalKey> countryChannel = createChannel("country", CodaObjectStoreType.FILE, templateStorePath, CodaObjectStoreType.ROUTER, profileName);

    // Create or update all country masters
    for(CountryMaster c : countryChannel.sourceStore.getObjectStream()) {
      createOrUpdateObject(countryChannel.destStore, c);
    }    
  }

  public void createCompany(String profileName, String templateStorePath, String code, String name, String shortName, String localCurrency, String funcCurrency, CompanyAddress address, int baseYear, int maxYear) throws Exception {
    
    CodaRouter router = getRouter(profileName, CodaLogicalServerType.FINANCIALS);
    
    // Company Base
    //////////////////////////////
    CodaObjectStore<Company,CodaGlobalKey> companyService = getStore(CodaObjectStoreType.ROUTER, "company", profileName);

    String prevCompany = router.getCompanyCode();
    
    // Create the skeleton company
    Map<String,Object> companyProps = new HashMap<>();
    companyProps.put("code", code);
    companyProps.put("name", name);
    companyProps.put("shortName", shortName);
    companyProps.put("address", address); // Custom handler
    companyService.createObject(companyProps);
    companyProps.clear();
    
    // Currencies
    //////////////////////////////
    
    // Log in to the new company and create the default currencies
    // TODO: Change login company automatically
    router.changeCompany(code);
    
    ObjectChannel<Currency,CodaLocalKey> currencyChannel = createChannel("currency", CodaObjectStoreType.FILE, templateStorePath, CodaObjectStoreType.ROUTER, profileName);
    
    Currency loc = null;
    Currency func = null;
    for (Currency c : currencyChannel.sourceStore.getObjectStream(new String[] {templateCompanyCode})) {
      c.setCmpCode(code);
      currencyChannel.destStore.createObject(c);
      if (c.getCode().equals(localCurrency))
        loc = c;
      if (c.getCode().equals(funcCurrency))
        func = c;
    }
    if (loc == null)
      throw new Exception("Local currency, " + localCurrency + " not found in template currency list");
    if (func == null)
      throw new Exception("Functional currency, " + funcCurrency + " not found in template currency list");
    
    // Create "dummy" LOC currency if local and functional currencies are the same
    if (funcCurrency.equals(localCurrency)) {
      // Get a copy of the local currency
      func = currencyChannel.sourceStore.getObject(new CodaLocalKey(templateCompanyCode,localCurrency));
      func.setCmpCode(code);
      func.setCode("LOC");
      func.setName("Local Currency");
      func.setShortName("Local Currency");
      currencyChannel.destStore.createObject(func);
    }

    // Set ledger currencies for company
    
    companyProps.put("homeCurr",loc.getCode());
    companyProps.put("dualCurr",func.getCode());

    // Commit the changes to the new company
    companyService.updateObject(new CodaGlobalKey(code), companyProps);
    companyProps.clear();
    
    // Balance Masters
    //////////////////////////////

    // Create default balance masters
    boolean hasActual = false;
    boolean hasTurnover = false;
    ObjectChannel<BalanceMaster,CodaLocalKey> balanceChannel = createChannel("balancemaster", CodaObjectStoreType.FILE, templateStorePath, CodaObjectStoreType.ROUTER, profileName);
    for(BalanceMaster bm : balanceChannel.sourceStore.getObjectStream(new String[] {templateCompanyCode})) {
      bm.setCmpCode(code);
      switch (bm.getCode()) {
        case "ACTUAL":
          hasActual = true;
          break;
        case "TURNOVER":
          hasTurnover = true;
          break;
      }
      balanceChannel.destStore.createObject(bm);
    }

    if (!hasActual)
      throw new Exception("Missing ACTUAL balance master template");
    if (!hasTurnover)
      throw new Exception("Missing TURNOVER balance master template");

    List<CompanyBalance> balances = Arrays.asList(
            new CompanyBalance(CompanyBalanceType.HOME_ACTUALS, true, (short)0, Arrays.asList((short)0, (short)15)), 
            new CompanyBalance(CompanyBalanceType.DUAL_ACTUALS, true, (short)0, Arrays.asList((short)0, (short)15)), 
            new CompanyBalance(CompanyBalanceType.FOREIGN_ACTUALS, false, (short)0, null), 
            new CompanyBalance(CompanyBalanceType.ELEMENT_ACTUALS, true, (short)1, Arrays.asList((short)0, (short)1)),
            new CompanyBalance(CompanyBalanceType.HOME_TURNOVER, true, (short)0, Arrays.asList((short)0, (short)16)), 
            new CompanyBalance(CompanyBalanceType.DUAL_TURNOVER, true, (short)0, Arrays.asList((short)0, (short)16)), 
            new CompanyBalance(CompanyBalanceType.FOREIGN_TURNOVER, false, (short)0, null),
            new CompanyBalance(CompanyBalanceType.ELEMENT_TURNOVER, false, (short)0, null), 
            new CompanyBalance(CompanyBalanceType.QUANTITIES, true, (short)0, Arrays.asList((short)0))
    );
    companyProps.put("actBalCode", "ACTUAL");
    companyProps.put("turnBalCode", "TURNOVER");
    companyProps.put("balances", balances); // Custom handler
    
    // Document Masters
    //////////////////////////////
    
    boolean hasMatching = false;
    boolean hasDisperse = false;
    ObjectChannel<DocumentMaster,CodaLocalKey> documentChannel = createChannel("documentmaster", CodaObjectStoreType.FILE, templateStorePath, CodaObjectStoreType.ROUTER, profileName);
    for(DocumentMaster dm : documentChannel.sourceStore.getObjectStream(new String[] {templateCompanyCode})) {
      dm.setCmpCode(code);
      switch (dm.getCode()) {
        case "MATCHING":
          hasMatching = true;
          break;
        case "DISPERSE":
          hasDisperse = true;
          break;
      }
      documentChannel.destStore.createObject(dm);
    }
    
    if (!hasMatching)
      throw new Exception("Missing MATCHING document master template");
    if (!hasDisperse)
      throw new Exception("Missing DISPERSE document master template");
    
    companyProps.put("defMatchingCode", "MATCHING");
    companyProps.put("defDisperseCode", "DISPERSE");
    
    // Entities
    //////////////////////////////    

    // TODO: Check for required entity templates during load
    companyProps.put("address_categories", "ADDRESS"); // Custom handler
    companyProps.put("element_statuses", "STATUS"); // Custom handler
    companyProps.put("reason_codes", "REASON"); // Custom handler
    companyProps.put("diary_action_codes", "ACTION"); // Custom handler
    companyProps.put("resolution_codes", "RESOLUTION"); // Custom handler
    companyProps.put("reporting_code_1", "CORP CODE"); // Custom handler
    companyProps.put("reporting_code_2", "GENERAL"); // Custom handler
    companyProps.put("reporting_code_3", "INVCURRENCY"); // Custom handler    
    
    // Commit all changes to the new company
    companyService.updateObject(new CodaGlobalKey(code), companyProps);
    companyProps.clear();
    
    // Years/Periods
    //////////////////////////////
    // Validate inputs
    if (maxYear < baseYear)
      throw new Exception("Invalid years: max year, " + maxYear + ", is less than base year, " + baseYear);
    
    CodaDbConnection dbConn = getDbConnection(profileName);
    YearService yearService = new YearService(getRouter(profileName, CodaLogicalServerType.FINANCIALS), dbConn);

    // Create each year
    for (Integer year = baseYear ; year <= maxYear; year++) {
      yearService.addYear(code, year, year.toString(), year.toString(), YearService.PeriodInterval.PERIOD_MONTHEND, 7);
    }
    
    // Set current year/period for each security group (1-3)
    for (int g = 1; g <= 3; g++)
      yearService.setCurrentPeriod(code, g, baseYear, 1);
    
    // TODO: Assets years/periods
    
    router.changeCompany(prevCompany);
  }
  
//  public void deleteCompany(String profileName, String code) throws Exception {
  
  /*
  DELETE FROM ...
  OAS_DOCUMENT
  OAS_BALMAST
  OAS_CURRENCY
  OAS_CURLIST
  OAS_CMPENTLIST
  OAS_CMPPROC
  */
//  }

  protected void clearSchema(String profileName) {
    CodaDbConnection dbConn = getDbConnection(profileName);
    
  }
  
}
