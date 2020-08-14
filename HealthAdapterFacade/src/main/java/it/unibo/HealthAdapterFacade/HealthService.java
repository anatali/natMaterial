package it.unibo.HealthAdapterFacade;

import org.springframework.stereotype.Service;
 

@Service
public class HealthService {
	
	public static enum HealthCenterType{ FHIR, HL7, OTHER};
	
	public static final String createPatientUri  		="/createPatient";
	public static final String searchPatientUri  		="/searchPatient";
	public static final String readResourceUri   		="/readResource";
	public static final String deleteResourceUri 		="/deleteResource";
	public static final String selectHealthCenterUri    ="/selectHealthCenter";
	private HealthServiceInterface service;
	
	public HealthService() {	//Autoconfiguration, just to start ...		
 		System.out.println("HealthService Autoconfiguration " );
		buildHealthService( HealthCenterType.FHIR, "https://hapi.fhir.org/baseR4" );
	}
	
	public HealthServiceInterface setHealthService(String choice, String serverAddr) { 
		if( choice.equals("FHIR") ) buildHealthService( HealthCenterType.FHIR, serverAddr );
		else if( choice.equals("HL7") ) buildHealthService( HealthCenterType.HL7, serverAddr );
		return getdHealthService();
	}

	
	public HealthServiceInterface getdHealthService() { return service; }
	
	//Called by a system configuration
	public void buildHealthService( HealthCenterType hct, String serverBase ) {
		System.out.println("buildHealthService " + hct + " serverBase=" + serverBase);
		switch( hct ) {
			case FHIR  : service =  new HealthServiceFhir( serverBase );break;
			case HL7   : service =  new HealthServiceHL7( serverBase );break;
			default    : service =  new HealthServiceFhir( serverBase );
		}		
	}
 	
 
	
//	public String prettyFormat(String input, int indent) {
//	    try {
//	        Source xmlInput = new StreamSource(new StringReader(input));
//	        StringWriter stringWriter = new StringWriter();
//	        StreamResult xmlOutput = new StreamResult(stringWriter);
//	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//	        transformerFactory.setAttribute("indent-number", indent);
//	        Transformer transformer = transformerFactory.newTransformer(); 
//	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//	        transformer.transform(xmlInput, xmlOutput);
//	        return xmlOutput.getWriter().toString();
//	    } catch (Exception e) {
//	    	System.out.println("prettyFormat ERROR " + e.getMessage());
//	    	return "";
// 	    }
//	}
	
}
