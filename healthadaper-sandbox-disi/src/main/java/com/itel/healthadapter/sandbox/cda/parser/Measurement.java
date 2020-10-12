package com.itel.healthadapter.sandbox.cda.parser;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

/**
 * Classe che modella una misurazione programmata
 *
 * @author Matteo Gramellini
 */
public class Measurement {

    /**
     * id della misurazione
     */
    private BigInteger measurementId;
    /**
     * id del paziente a cui è associata la misurazione programmata
     */
    private BigInteger patientId;
    /**
     * idparametro vitale da misurare
     */
    private int vitalParameterId;
    /**
     * lista parametri misurabili associati al parametro vitale
     */
    private List<HashMap<Integer, HashMap<String, String>>> measurableParameters;
    /**
     * fascia di misurazione
     */
    private int bandId;
    /**
     * orario misurazione
     */
    private String measuringTime;
    /**
     * orario promemoria
     */
    private String reminderTime;
    /**
     * giorno della settimana in cui effettuare la misurazione
     */
    private DayOfWeekType dayOfWeek;
    /**
     * notifica associata alla misurazione programmata
     */
    private int notificationId;
    /**
     * indica se il risultato della misurazione potrebbe variare a causa
     * dell’assunzione di determinati farmaci prescritti
     */
    private boolean isCustom;
    /**
     * eventuale nota per la misurazione
     */
    private String note;
    /**
     * data inizio programmazione
     */
    private String programmingStart;
    /**
     * data fine programmazione
     */
    private String programmingEnd;
    /**
     * tipo misurazione 1: PROGRAMMATA 2: PROGRAMMATA
     */
    private int measuramentType;

    public Measurement() {
    }

    /**
     *
     * @param measurementId id della misurazione
     * @param patientId id del paziente a cui è associata la misurazione
     * programmata
     * @param vitalParameterId id parametro vitale da misurare
     * @param measurableParameters lista dei parametri misurabili assocciati al
     * parametro vitale
     * @param bandId id della fascia di misurazione
     * @param measuringTime orario misurazione
     * @param reminderTime orario promemoria
     * @param dayOfWeek giorno della settimana in cui effettuare la misurazione
     * @param notificationId id notifica associata alla misurazione programmata
     * @param isCustom indica se il risultato della misurazione potrebbe variare
     * a causa dell’assunzione di determinati farmaci prescritti
     * @param note eventuale nota per la misurazione
     * @param programmingStart data inizio programmazione
     * @param programmingEnd data fine programmazione
     * @param measuramentType tipo di misurazione
     */
    public Measurement(BigInteger measurementId, BigInteger patientId, int vitalParameterId, List<HashMap<Integer, HashMap<String, String>>> measurableParameters, int bandId, String measuringTime, String reminderTime, DayOfWeekType dayOfWeek, int notificationId, boolean isCustom, String note, String programmingStart, String programmingEnd, int measuramentType) {
        setMeasurementId(measurementId);
        setPatientId(patientId);
        setVitalParameterId(vitalParameterId);
        setMeasurableParameters(measurableParameters);
        setBandId(bandId);
        setMeasuringTime(measuringTime);
        setReminderTime(reminderTime);
        setDayOfWeek(dayOfWeek);
        setNotificationId(notificationId);
        setIsCustom(isCustom);
        setNote(note);
        setProgrammingStart(programmingStart);
        setProgrammingEnd(programmingEnd);
        setMeasuramentType(measuramentType);
    }

    public BigInteger getMeasurementId() {
        return measurementId;
    }

    private void setMeasurementId(BigInteger measurementId) {
        this.measurementId = measurementId;
    }

    public BigInteger getPatientId() {
        return patientId;
    }

    private void setPatientId(BigInteger patientId) {
        this.patientId = patientId;
    }

    public int getVitalParameterId() {
        return vitalParameterId;
    }

    private void setVitalParameterId(int vitalParameterId) {
        this.vitalParameterId = vitalParameterId;
    }

    public List<HashMap<Integer, HashMap<String, String>>> getMeasurableParameters() {
        return measurableParameters;
    }

    private void setMeasurableParameters(List<HashMap<Integer, HashMap<String, String>>> measurableParameters) {
        this.measurableParameters = measurableParameters;
    }

    public int getBandId() {
        return bandId;
    }

    private void setBandId(int bandId) {
        this.bandId = bandId;
    }

    public String getMeasuringTime() {
        return measuringTime;
    }

    private void setMeasuringTime(String measuringTime) {
        this.measuringTime = measuringTime;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    private void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public DayOfWeekType getDayOfWeek() {
        return dayOfWeek;
    }

    private void setDayOfWeek(DayOfWeekType dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getNotificationId() {
        return notificationId;
    }

    private void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public boolean isIsCustom() {
        return isCustom;
    }

    private void setIsCustom(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public String getNote() {
        return note;
    }

    private void setNote(String note) {
        this.note = note;
    }

    public String getProgrammingStart() {
        return programmingStart;
    }

    private void setProgrammingStart(String programmingStart) {
        this.programmingStart = programmingStart;
    }

    public String getProgrammingEnd() {
        return programmingEnd;
    }

    private void setProgrammingEnd(String programmingEnd) {
        this.programmingEnd = programmingEnd;
    }

    public int getMeasuramentType() {
        return measuramentType;
    }

    private void setMeasuramentType(int measuramentType) {
        this.measuramentType = measuramentType;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "measurementId=" + measurementId +
                ", patientId=" + patientId +
                ", vitalParameterId=" + vitalParameterId +
                ", measurableParameters=" + measurableParameters +
                ", bandId=" + bandId +
                ", measuringTime='" + measuringTime + '\'' +
                ", reminderTime='" + reminderTime + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", notificationId=" + notificationId +
                ", isCustom=" + isCustom +
                ", note='" + note + '\'' +
                ", programmingStart='" + programmingStart + '\'' +
                ", programmingEnd='" + programmingEnd + '\'' +
                ", measuramentType=" + measuramentType +
                '}';
    }
}
