package it.unibo.Handlebars.templates;
/*
 * USING VERSION v23
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.datatype.*;
import ca.uhn.hl7v2.model.v23.segment.*;
import ca.uhn.hl7v2.model.v23.message.*;
import ca.uhn.hl7v2.util.Hl7InputStreamMessageIterator;


public class ReadMessagesFromFile {

	public static void exploreMsg(Message m) throws HL7Exception {
		String mtype = m.printStructure().substring(0, 7);
		switch(mtype) {
			case "ADT_A01" : exploreA01Msg( (ADT_A01)m );break;
			case "ADT_A04" : System.out.println("wait a moment for "+mtype );break;
			default        : System.out.println("sorry ..."+mtype  + " unknown " );
		}
	}
	
	public static void exploreA01Msg(ADT_A01 m) {
        MSH msh = m.getMSH();
        String msgType    = msh.getMessageType().getMessageType().getValue();
        String msgTrigger = msh.getMessageType().getTriggerEvent().getValue();

        System.out.println("msgType=" + msgType + " trigger=" + msgTrigger);
        
        System.out.println("... " + m.getPID().getName());

        XPN[] patientName = m.getPID().getPatientName();
        String familyName = patientName[0].getFamilyName().getValue();
        System.out.println(familyName);
        System.out.println(patientName[0].getGivenName());		
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws FileNotFoundException, HL7Exception {

		// Open an InputStream to read from the file
		File file      = new File("C:/Progetti/natmaterial/HealthAdapterFacade/FHIR-Converter/examples/sample-data/hl7v2/ADT01-23.hl7");
//		InputStream is = new FileInputStream(file);
		InputStream is = new BufferedInputStream(new FileInputStream(file));
 		Hl7InputStreamMessageIterator iter = new Hl7InputStreamMessageIterator(is);
		
		while (iter.hasNext()) {			
			System.out.println("----------------------------------- " );
			Message next = iter.next();
			System.out.println("TYPE="+next.printStructure().substring(0, 8) );
			System.out.println("VERSION="+ next.getVersion() );
			exploreMsg(next);
		}
	}

}
