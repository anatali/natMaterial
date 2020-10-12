package com.itel.healthadapter.sandbox.cda.parser;

/**
 * Giorni della settimana
 *
 * @author Matteo Gramellini
 */
public enum DayOfWeekType {

    UNKNOWN(0),
    SUNDAY(1),
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),
    SATURDAY(7);

    /**
     * valore intero dell'enumerativo per la serializzazione
     */
    private final int value;

    DayOfWeekType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DayOfWeekType getDayOfWeekType(int dayOfWeekType) {
        switch (dayOfWeekType) {
            case 1:
                return SUNDAY;
            case 2:
                return MONDAY;
            case 3:
                return TUESDAY;
            case 4:
                return WEDNESDAY;
            case 5:
                return THURSDAY;
            case 6:
                return FRIDAY;
            case 7:
                return SATURDAY;
            default:
                return UNKNOWN;
        }
    }
}
