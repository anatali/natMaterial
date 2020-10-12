package com.itel.healthadapter.sandbox.cda.parser;

/**
 *
 * @author Matteo Gramellini
 */
public class Catalog {

    //Sotto ramo dedicato alle organizzazioni associate ad HL7 Italia
    public static final String ORG_HL7IT = "2.16.840.1.113883.2.9.2";

    //Radice OID locale rilasciato da HL7 Italia a: Regione Lombardia
    public static final String ROOT_LOMBARDIA_REGION = ORG_HL7IT + "." + "30";

    //Radice dei Progetti (Creare sottorami per i singoli progetti) Regione Lombardia
    public static final String PRJ_LOMBARDIA_REGION = ROOT_LOMBARDIA_REGION + "." + "3";
    
    //Radice dei Progetti (Creare sottorami per i singoli progetti) Regione Lombardia
    public static final String PRJ_LOMBARDIA_REGION_V2 = ROOT_LOMBARDIA_REGION + "." + "6";
    
    //Root Progetto CRS-SISS
    public static final String PRJ_CRS_SISS = PRJ_LOMBARDIA_REGION + "." + "1";
    
    //Root Sottoprogetti CRS-SISS
    public static final String SUB_PRJ_CRS_SISS = PRJ_CRS_SISS + "." + "3";
    
    //Root Sottoprogetti CRS-SISS
    public static final String TELEMEDICINE_PROJECT = SUB_PRJ_CRS_SISS + "." + "13";
    //
    public static final String TELEMEDICINE_PROJECT_SYSTEM_ATTRIBUTE_DATE = TELEMEDICINE_PROJECT + "." + "6.5";
    //
    public static final String SECTIONS_CODE_SYSTEM = TELEMEDICINE_PROJECT + "." + "6.1";
    //
    public static String PATIENT_ID = "2.16.840.1.113883.2.9.4.3.2";
    
    
    /**********************************************************************/
     //Sotto ramo dedicato alleorganizzazioni associate ad HL7 Italia v1
    public static final String ORG_HL7IT_V2 = "2.16.840.1.113883.2.9.3";
    
    //Radice OID locale rilasciato da HL7 Italia a: Regione Emilia Romagna
    public static final String ROOT_EMILIAROMAGNA_REGION = ORG_HL7IT_V2 + "." + "12";
    
    //Radice dei Progetti (Creare sottorami per i singoli progetti) Regione Emilia Romagna
    public static final String PRJ_EMILIAROMAGNA_REGION = ROOT_EMILIAROMAGNA_REGION + "." + "4";
    
    //Root Progetto TELE
    public static final String PRJ_TELE = PRJ_EMILIAROMAGNA_REGION + "." + "42";
    
    //Paramatro vitale
    public static final String PRJ_TELE_VITAL_PARAMETER = PRJ_TELE + "." + "1";
    
    //Paramatro misurabile
    public static final String PRJ_TELE_MEASURABLE_PARAMETER = PRJ_TELE + "." + "2";
    
    //Fascia
    public static final String PRJ_TELE_BAND = PRJ_TELE + "." + "3";
    
    //Giorno della settimana
    public static final String PRJ_TELE_DAY_OF_WEEK = PRJ_TELE + "." + "5";
    
    //orario promemoria e oriario misurazione
    public static final String PRJ_TELE_TIME_AND_REMINDER  = PRJ_TELE + "." + "6";
    
    //Notifica
    public static final String PRJ_TELE_NOTIFY = PRJ_TELE + "." + "7";
    
    //Data Inizio e fine programmazione
    public static final String PRJ_TELE_START_END_DATE_PROGRAMMING = PRJ_TELE + "." + "8";
    
    //Tipo misurazione\assunzione\attività
    public static final String PRJ_TELE_SCHEDULED_TYPE = PRJ_TELE + "." + "9";
    
    //Quantità
    public static final String PRJ_TELE_DOSE_QUANTITY = PRJ_TELE + "." + "10";
   
    //Attività
    public static final String PRJ_TELE_ACTIVITY = PRJ_TELE + "." + "11";
    
    //Numero inventario Kit
    public static final String PRJ_TELE_KIT_INVENTORY_NUMBER = PRJ_TELE + "." + "12";

    //Template domande
    public static final String PRJ_TELE_QUESTIONS_TEMPLATE = PRJ_TELE + "." + "13";
    
    //Id Misurazione/Assunzione/Attività/Questionario
    public static final String PRJ_TELE_SCHEDULED_ID = PRJ_TELE + "." + "14";
    
    // Note
    public static final String PRJ_TELE_SCHEDULED_NOTE = PRJ_TELE + "." + "15";
    
    //Farmaco
    public static final String PRJ_TELE_DRUG = PRJ_TELE + "." + "16";

    /*************************************************************/
    
    public static final String SYSTEM_ATTRIBUTE_CODE = "1";

    public static final String PROGRAMMAZIONE_CLINICO_ASSISTENZIALE_CODE = "4";

    public static final String SCHEDULED_DOSE_CODE = "5";

    public static final String SCHEDULED_MEASUREMENT_CODE = "6";

    public static final String SCHEDULED_ACTIVITY_CODE = "7";
    
    public static final String SURVEY_CODE = "8"; 
}
