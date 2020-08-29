package it.unibo.Handlebars.templates;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.message.ADT_A01;
import ca.uhn.hl7v2.util.Hl7InputStreamMessageIterator;
import it.unibo.Handlebars.Person;

public class TestCvt {
	//From https://www.baeldung.com/handlebars	
	private static Handlebars handlebars = new Handlebars();
	
	public static void elabFromFile() throws Exception {
		File file                          = new File("C:/Progetti/natmaterial/HealthAdapterFacade/FHIR-Converter/examples/sample-data/hl7v2/ADT01-23.hl7");
		BufferedInputStream is             = new BufferedInputStream(new FileInputStream(file));
 		Hl7InputStreamMessageIterator iter = new Hl7InputStreamMessageIterator(is);
		
		while (iter.hasNext()) {			
			System.out.println("----------------------------------- " );
			Message next = iter.next();
			System.out.println("TYPE="+next.printStructure().substring(0, 8) );
			System.out.println("VERSION="+ next.getVersion() );
			exploreMsg(next);
		}
	}
	
	public static void exploreMsg(Message m) throws HL7Exception {
		String mtype = m.printStructure().substring(0, 7);
		switch(mtype) {
			case "ADT_A01" : cvtA01Msg( (ADT_A01)m );break;
			case "ADT_A04" : System.out.println("wait a moment for "+mtype );break;
			default        : System.out.println("sorry ..."+mtype  + " unknown " );
		}
	}

	public static String templateFile_parameterInMap() {
		try {
			Template template     = handlebars.compile( "templatesHbs/cvt" );	//.hbs 
			Map<String, String> parameterMap = new HashMap<>();
			parameterMap.put("foo", "Disi Test on Handlebars with Map and cvt.bls");
			String templateString = template.apply(parameterMap);
			System.out.println(templateString);
			return templateString;
		} catch (IOException e) {
			String s = "templateFile_parameterInMap error " + e.getMessage();
			System.out.println(s ); 
			return s; 
		}		
	}

	
	public static void cvtA01Msg( ADT_A01 m ) {
		try {
//			templateFile_parameterInMap();
			Template template     = handlebars.compile( "templatesHbs/ADT_A01NaiveForJava" );	 
			Map<String, String> parameterMap = new HashMap<>();
			parameterMap.put("patientName", ""+m.getPID().getPatientName()[0].getGivenName());
			String templateString = template.apply(parameterMap);
 
//			String templateString = template.apply(m);
 			System.out.println( templateString ); 
		} catch (Exception e) {
			String s = "error " + e.getMessage();
			System.out.println(s ); 
		}				
	}	
	
	public static void main(String[] args) throws Exception {
		elabFromFile();
	}

}
