package com.itel.healthadapter.sandbox.cda.parser;

import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.Patient;
import org.eclipse.mdht.uml.cda.PatientRole;
import org.eclipse.mdht.uml.cda.Section;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.eclipse.mdht.uml.cda.util.ValidationResult;
import org.eclipse.mdht.uml.hl7.datatypes.CE;

import java.io.InputStream;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Jean Claude Correale
 */
public class CDAParser {

    private Map<String, BiConsumer<Section, ImmutableCDAParseResult.Builder>> sectionHandlers = Map.of(
        Catalog.SCHEDULED_MEASUREMENT_CODE, (section, builder) -> builder.plannedMeasurements(MeasurementParser.getMeasurements(section))
        // TODO Handle other sections
    );

    public CDAParseResult parseCDA(InputStream inputStream) throws CDAParserException {
        ClinicalDocument clinicalDocument = returnValidClinicalDocumentOrThrow(inputStream);
        ImmutableCDAParseResult.Builder cdaParseResultBuilder = ImmutableCDAParseResult.builder();
        
        parsePatientTaxCode(clinicalDocument, cdaParseResultBuilder);

        EList<Section> sections = clinicalDocument.getAllSections();
        sections.forEach(handleSection(cdaParseResultBuilder));

        return cdaParseResultBuilder.build();
    }

    private void parsePatientTaxCode(ClinicalDocument clinicalDocument, ImmutableCDAParseResult.Builder cdaParseResultBuilder) throws CDAParserException {
        if (clinicalDocument.getPatientRoles().size() != 1)
            throw new CDAParserException("Expecting only one PatientRole in the CDA");
        PatientRole patientRole = clinicalDocument.getPatientRoles().get(0);
        cdaParseResultBuilder.patientTaxCode(patientRole.getIds().get(0).getExtension());
    }

    private Consumer<Section> handleSection(ImmutableCDAParseResult.Builder cdaParseResultBuilder) {
        return section -> {
            CE code = section.getCode();
            BiConsumer<Section, ImmutableCDAParseResult.Builder> handler = sectionHandlers.get(code.getCode());
            if (handler != null) handler.accept(section, cdaParseResultBuilder);
        };
    }

    private ClinicalDocument returnValidClinicalDocumentOrThrow(InputStream inputStream) throws CDAParserException {
        // TODO Validation is not done for now due to an issue that occurs when calling CDAParser from RestController
        ValidationResult validationResult = new ValidationResult();
        ClinicalDocument clinicalDocument;
//        try {
//            clinicalDocument = CDAUtil.load(inputStream, validationResult);
//        }
        try {
            clinicalDocument = CDAUtil.load(inputStream);
        }
        catch (Exception exception) {
            throw new CDAParserException("Something went wrong while parsing the CDA", exception);
        }
//        if (!isValid(validationResult)) throw new CDAParserException("CDA is not valid");
        if (clinicalDocument == null) throw new CDAParserException("Something went wrong while parsing the CDA");
        return clinicalDocument;
    }

    /**
     * @author Matteo Gramellini
     * @param result
     * @return
     */
    private static boolean isValid(ValidationResult result) {

        System.out.println("Maintenance::checkDocumentValidity - Number of Schema Validation Diagnostics: " + result.getSchemaValidationDiagnostics().size());
        System.out.println("Maintenance::checkDocumentValidity - Number of EMF Resource Diagnostics: " + result.getEMFResourceDiagnostics().size());
        System.out.println("Maintenance::checkDocumentValidity - Number of EMF Validation Diagnostics: " + result.getEMFValidationDiagnostics().size());
        System.out.println("Maintenance::checkDocumentValidity - Number of Total Diagnostics: " + result.getAllDiagnostics().size());

        result.getErrorDiagnostics().forEach((diagnostic) -> {
            System.err.println("Maintenance::checkDocumentValidity: " + diagnostic.getMessage());
        });
        result.getWarningDiagnostics().forEach((diagnostic) -> {
            System.out.println("Maintenance::checkDocumentValidity: " + diagnostic.getMessage());
        });

        if (!result.hasErrors()) {
            System.out.println("Maintenance::checkDocumentValidity - Il CDA ricevuto e' valido");
            return true;
        } else {
            System.out.println("Maintenance::checkDocumentValidity - Il CDA ricevuto non risulta valido");
            return false;
        }
    }
}