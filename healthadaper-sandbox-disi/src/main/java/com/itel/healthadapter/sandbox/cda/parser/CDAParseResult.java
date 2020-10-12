package com.itel.healthadapter.sandbox.cda.parser;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface CDAParseResult {
    String patientTaxCode();
    List<Measurement> plannedMeasurements();
}