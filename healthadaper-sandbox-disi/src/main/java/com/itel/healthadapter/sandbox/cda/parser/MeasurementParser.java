package com.itel.healthadapter.sandbox.cda.parser;

import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.impl.IVL_PQImpl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Matteo Gramellini
 */
class MeasurementParser {

    public static List<Measurement> getMeasurements(/*boolean isNewCDA, String taxCode, */Section section) {

        /**
         * numero inventario kit
         */
        Integer kitInventoryNumber = null;

        /**
         * id della misurazione programmata
         */
        BigInteger measurementId = null;

        /**
         * parametro vitale da misurare
         */
        int vitalParameterId = -1;

        /**
         * lista parametri misurabili associati al parametro vitale
         */
        List<HashMap<Integer, HashMap<String, String>>> measurableParameters;
        /**
         * fascia di misurazione
         */
        int bandId = -1;
        /**
         * orario misurazione
         */
        String measuringTime = null;
        /**
         * orario promemoria
         */
        String reminderTime = null;
        /**
         * giorno della settimana in cui effettuare la misurazione
         */
        DayOfWeekType dayOfWeek = null;
        /**
         * notifica associata alla misurazione programmata
         */
        int notificationId = -1;
        /**
         * indica se il risultato della misurazione potrebbe variare a causa
         * dell’assunzione di determinati farmaci prescritti
         */
        boolean isCustom = false;
        /**
         * eventuale nota per la misurazione
         */
        String note = null;
        /**
         * data inizio programmazione
         */
        String programmingStart = null;
        /**
         * data fine programmazione
         */
        String programmingEnd = null;
        /**
         * tipo misurazione 1: PROGRAMMATA 2: ISTANTANEA
         */
        int measuramentType = -1;

//        BigInteger patientId = BaseManagement.getPatientIdByTaxCode(taxCode);

        List<Measurement> scheduledMeasurementToInserts = new ArrayList<>();

        Entry measurementsEntry = section.getEntries().get(0);
        if (measurementsEntry != null) {
            Observation mObservation = measurementsEntry.getObservation();
            if (mObservation != null) {
                for (EntryRelationship er : mObservation.getEntryRelationships()) {
                    Organizer organizer = er.getOrganizer();
                    if (organizer == null) {
                        //numero inventario kit
                        if (er.getObservation() != null) {
                            CD cd = er.getObservation().getCode();
                            if (cd.getCodeSystem().equals(Catalog.PRJ_TELE_KIT_INVENTORY_NUMBER)) {
                                kitInventoryNumber = Integer.parseInt(cd.getCode());
                            }
                        } else {
                            System.err.println("CDAManagament::getMeasurements() osservation del inventario kit e' nullo");
                        }
                    } else {
                        //misurazione
                        Measurement scheduledMeasurementToInsert;
                        if (organizer.getCode().getCodeSystem().equals(Catalog.PRJ_TELE_VITAL_PARAMETER)) {
                            vitalParameterId = Integer.parseInt(organizer.getCode().getCode());
                        }

                        measurableParameters = new ArrayList<>();
                        // Parametri misurabili
                        for (Component4 component : organizer.getComponents()) {
                            Observation param = component.getObservation();
                            if (param != null) {
                                CD cd = param.getCode();
                                switch (cd.getCodeSystem()) {
                                    case Catalog.PRJ_TELE_SCHEDULED_ID:
                                        measurementId = new BigInteger(cd.getCode());
                                        break;

                                    case Catalog.PRJ_TELE_MEASURABLE_PARAMETER:

                                        int measurableParameterId = Integer.parseInt(cd.getCode());

                                        IVL_PQImpl value = (IVL_PQImpl) param.getReferenceRanges().get(0).getObservationRange().getValue();
                                        String thresholdMin = String.valueOf(value.getLow().getValue());
                                        String thresholdMax = String.valueOf(value.getHigh().getValue());

                                        HashMap<Integer, HashMap<String, String>> measurableParameter = new HashMap<>();
                                        HashMap<String, String> measurableParameterThresholds = new HashMap<>();
                                        if (thresholdMin != null && !thresholdMin.isEmpty() && !thresholdMin.equals("null") && thresholdMax != null && !thresholdMax.isEmpty() && !thresholdMax.equals("null")) {
                                            measurableParameterThresholds.put("valoreMinimo", thresholdMin);
                                            measurableParameterThresholds.put("valoreMassimo", thresholdMax);
                                        }
                                        measurableParameter.put(measurableParameterId, measurableParameterThresholds);
                                        measurableParameters.add(measurableParameter);
                                        break;

                                    case Catalog.PRJ_TELE_BAND:
                                        bandId = Integer.parseInt(cd.getCode());
                                        break;

                                    case Catalog.PRJ_TELE_DAY_OF_WEEK:
                                        dayOfWeek = DayOfWeekType.getDayOfWeekType(Integer.parseInt(cd.getCode()));
                                        break;

                                    case Catalog.PRJ_TELE_TIME_AND_REMINDER:
                                        if (cd.getCode().equals("1")) {
                                            reminderTime = param.getEffectiveTime().getCenter().getValue();
                                        } else if (cd.getCode().equals("2")) {
                                            measuringTime = param.getEffectiveTime().getCenter().getValue();
                                        }
                                        break;

                                    case Catalog.PRJ_TELE_NOTIFY:
                                        notificationId = Integer.parseInt(cd.getCode());
                                        break;

                                    case Catalog.PRJ_TELE_START_END_DATE_PROGRAMMING:
                                        programmingStart = param.getEffectiveTime().getLow().getValue();
                                        programmingEnd = param.getEffectiveTime().getHigh().getValue();

                                        break;

                                    case Catalog.PRJ_TELE_SCHEDULED_TYPE:
                                        measuramentType = Integer.parseInt(cd.getCode());
                                        break;

                                    case Catalog.PRJ_TELE_SCHEDULED_NOTE:
                                        note = param.getMethodCodes().get(0).getOriginalText().getText();
                                        break;
                                }
                            } else {
                                System.err.println("CDAManagament::getMeasurements() parametro misurazione e' nullo");
                                return null;
                            }
                        }
                        // TODO PatientId
                        scheduledMeasurementToInsert = new Measurement(measurementId, null/*patientId*/, vitalParameterId, measurableParameters, bandId, measuringTime, reminderTime, dayOfWeek, notificationId, isCustom, note, programmingStart, programmingEnd, measuramentType);
                        scheduledMeasurementToInserts.add(scheduledMeasurementToInsert);
                    }
                }
            } else {
                System.err.println("CDAManagament::getMeasurements() observation misurazione e' nullo");
                return null;
            }
        } else {
            System.err.println("CDAManagament::getMeasurements() entry misurazione e' nullo");
            return null;
        }

        return scheduledMeasurementToInserts;

//        if (!scheduledMeasurementToInserts.isEmpty()) {
//            HashMap<BigInteger, Integer> successMap = new HashMap<>();
//            for (MeasurementToInsert currentScheduledMeasurement : scheduledMeasurementToInserts) {
//                if (MeasurementsManagement.getScheduledMeasurementById(currentScheduledMeasurement.getMeasurementId()) == null) {
//                    response = MeasurementsManagement.saveMeasurement(currentScheduledMeasurement);
//                    if (response.getStatus() == ResponseStatus.ERROR) {
//                        //cancello le misurazioni inserite con successo precedentemente
//                        successMap.entrySet().forEach((entry) -> {
//                            MeasurementsManagement.deleteMeasurementById(entry.getKey(), entry.getValue());
//                        });
//                        return response;
//                    } else if (response.getStatus() == ResponseStatus.OK) {
//                        successMap.put(currentScheduledMeasurement.getMeasurementId(), currentScheduledMeasurement.getMeasuramentType());
//                    }
//                } else {
//                    if (!isNewCDA) {
//                        LogEx.trace(5, "ng", "CDAManagament::getMeasurements() aggiorno la misurazione con id: " + currentScheduledMeasurement.getMeasurementId());
//                        //devo aggionare la misurazione
//                        response = MeasurementsManagement.updateMeasurement(currentScheduledMeasurement);
//                        if (response.getStatus() == ResponseStatus.ERROR) {
//                            //cancello le misurazioni inserite con successo precedentemente
//                            successMap.entrySet().forEach((entry) -> {
//                                DosesManagement.deleteDoseById(entry.getKey(), entry.getValue());
//                            });
//                            return response;
//                        } else if (response.getStatus() == ResponseStatus.OK) {
//                            successMap.put(currentScheduledMeasurement.getMeasurementId(), currentScheduledMeasurement.getMeasuramentType());
//                        }
//                    } else {
//                        LogEx.trace(5, "ng", "CDAManagament::getMeasurements() la misurazione con id: " + currentScheduledMeasurement.getMeasurementId() + " e' gia presente nel db");
//                        //non faccio nulla e scarto la misurazione
//                    }
//                }
//            }
//        } else {
//            LogEx.trace(5, "ng", "CDAManagament::getMeasurements() la lista delle misurazioni e' vuota");
//            //response.setResponseDescription("Errore durante il parsing del CDA, nessuna misurazione presente per il paziente con id: " + patientId);
//        }
//
//        //controllo se il kit è già associato al paziente
//        if (!DevicesAndKitsManagement.checkPhysicalKitAndPatientAssociation(kitInventoryNumber, patientId)) {
//            //devo associare il kit al paziente
//            if (!DevicesAndKitsManagement.savePhysicalKitAndPatientAssociation(patientId, kitInventoryNumber)) {
//                LogEx.error(null, "CDAManagament::getMeasurements() errore durante l'associazione del numero inventario kit " + kitInventoryNumber + " al paziente con codice fiscale: " + taxCode);
//                response.setResponseDescription("Errore durante l'associazione del numero inventario kit al paziente con codice fiscale: " + taxCode);
//                response.setCodeSystem(com.itel.cda.parser.Catalog.PRJ_TELE_KIT_INVENTORY_NUMBER);
//                response.setCode(BigInteger.valueOf(kitInventoryNumber));
//                response.setStatus(ResponseStatus.ERROR);
//            }
//        }
//
//        response.setStatus(ResponseStatus.OK);
//        return response;
    }
}
