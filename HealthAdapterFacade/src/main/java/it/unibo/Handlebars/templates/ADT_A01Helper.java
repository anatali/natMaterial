package it.unibo.Handlebars.templates;

import org.hl7.fhir.dstu3.model.Patient;

import ca.uhn.hl7v2.model.v23.datatype.XPN;
import ca.uhn.hl7v2.model.v23.message.ADT_A01;
import ca.uhn.hl7v2.model.v23.segment.PID;

//HandleBars extracts helper definitions using reflection:
public class ADT_A01Helper {
	
	public String adtpatientname( ADT_A01 context) {
		String pname = ""+context.getPID().getPatientName()[0].getGivenName();
		return pname;
	}
	public String adtfamilyname( ADT_A01 context) {
		String pname = context.getPID().getPatientName()[0].getFamilyName().getValue();
		return pname;
	}
	public String familyname( ADT_A01 context) {
		String pname = context.getPID().getPatientName(0).getFamilyName().getValue();
		return pname;
	}
	public String patientname( ADT_A01 context) {
		String pname = ""+context.getPID().getPatientName(0).getGivenName().getValue();
		return pname;
	}

}
