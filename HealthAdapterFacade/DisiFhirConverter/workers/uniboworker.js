/* 
 -------------------------------------------------------------------------------------------------
 REFACTORING
 npm install handlebars
 npm install uuid			.v3 ???
 npm install antlr4
 npm install deepmerge
 npm install underscore
 npm install path
 -------------------------------------------------------------------------------------------------
 */
var path 				= require('path');
var constants 			= require('../constants/constants');
var fs 					= require('fs');
var Handlebars 			= require('handlebars');
var dataHandlerFactory	= require('../dataHandler/dataHandlerFactory');
var HandlebarsConverter = require('../handlebars-converter/handlebars-converter');


var workData = new Object();
var handlebarInstance ;
var dataTypeHandler;


function GetHandlebarsInstance(dataTypeHandler, templatesMap) {
console.log("\nunibo worker GetHandlebarsInstance " + constants.TEMPLATE_FILES_LOCATION );			 
    var instance = HandlebarsConverter.instance(true,
        dataTypeHandler,
        path.join(constants.TEMPLATE_FILES_LOCATION, dataTypeHandler.dataType),
        templatesMap);
    return instance;
}

function generateUUID(urlNamespace) {
    return "todouuidv3"; //uuidv3(''.concat(urlNamespace), uuidv3.URL);
}

function generateResult( dataTypeHandler,dataContext, template ) {
console.log("\n============================ uniboworker GENERATE RESULT ===========================================");

/*
console.log("uniboworker generateResult template " + template );									//BY AN (quello 'standard')
console.log("uniboworker generateResult dataTypeHandler " + Object.keys(dataTypeHandler));			//BY AN (dataType)
console.log("uniboworker generateResult dataContext.msg " + Object.keys(dataContext.msg) );			//BY AN (v2)
console.log("uniboworker generateResult dataContext.msg.v2 " + Object.keys(dataContext.msg.v2) );	//BY AN (data,meta)

*/ 
//console.log("uniboworker generateResult dataContext " + Object.keys(dataContext));					//BY AN (msg)
//console.log("uniboworker generateResult dataContext.msg.v2.meta " + dataContext.msg.v2.meta );		//BY AN ( MSH,EVN,PID,NK1,PV1,GT1,DG1,IN1,IN2,IN1,IN2,IN1)
//console.log("uniboworker generateResult dataContext.msg.v2.data " + dataContext.msg.v2.data );		//BY AN (body del msg HL7 privato di MSH|, EVN|, PID|etc)
 	 
	var resultBYAN = template(dataContext); 	 
/*
 * Qui invoca handlebars-helpers.js getFirstSegments e poi esegue  stored-fun che registra le risorse
 */
//console.log(" ----------------------- uniboworker template executed  ------- " + dataContext.msg.v2.meta ); //BY AN
//console.log("uniboworker generateResult resultBYAN " + Object.keys(resultBYAN).length );		//BY AN (molti numerici, 26566)
 
     var result = dataTypeHandler.postProcessResult(resultBYAN);
 
  /*     
 console.log("uniboworker generateResult result " + Object.keys(result));				//BY AN (resourceType,type,entry)
 console.log("uniboworker generateResult result.resourceType " + result.resourceType );	//BY AN (  Bundle )
 console.log("uniboworker generateResult result.type " + result.type );					//BY AN ( transaction )
 console.log("uniboworker generateResult result.entry " + result.entry);					//BY AN ( [object Object],[object Object],[object Object],[object Object],[object Object])
*/     
//copia tutte le proprietà enumerabili da uno o più oggetti di origine in un oggetto di destinazione, che Restituisce 

     //prima esegue  getConversionResultMetadata poi parseCoverageReport
     //var objBYAN = Object.assign( dataContext.msg, { 'fhirResource': resultBYAN });
     var objBYAN = Object.assign(dataTypeHandler.getConversionResultMetadata(dataContext.msg), { 'fhirResource': result });
// console.log("\n============================ uniboworker GENERATE RESULT objBYAN " + Object.keys(objBYAN));		
// (  unusedSegments,invalidAccess,fhirResource )
     return objBYAN;
}

 
 
console.log("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"  );
console.log("============================ uniboworker init ==========================================="  );

var hl7msg = "MSH|^~\\&|AccMgr|1|||20050110045504||ADT^A01|599102|P|2.3||| \n"+
 "EVN|A01|20050110045502|||||  \r\n" +
 "PID|1||10006579^^^1^MR^1||DUCK^DONALD^D||19241010|M||1|111 DUCK ST^^FOWL^CA^999990000^^M|1|8885551212|8885551212|1|2||40007716^^^AccMgr^VN^1|123121234|||||||||||NO \r\n" +
 "NK1|1|DUCK^HUEY|SO|3583 DUCK RD^^FOWL^CA^999990000|8885552222||Y|||||||||||||| \r\n" +
 "PV1|1|I|PREOP^101^1^1^^^S|3|||37^DISNEY^WALT^^^^^^AccMgr^^^^CI|||01||||1|||37^DISNEY^WALT^^^^^^AccMgr^^^^CI|2|40007716^^^AccMgr^VN|4|||||||||||||||||||1||G|||20050110045253|||||| \r\n" +
 "GT1|1|8291|DUCK^DONALD^D||111^DUCK ST^^FOWL^CA^999990000|8885551212||19241010|M||1|123121234||||#Cartoon Ducks Inc|111^DUCK ST^^FOWL^CA^999990000|8885551212||PT| \r\n" + 
 "DG1|1|I9|71596^OSTEOARTHROS NOS-L/LEG ^I9|OSTEOARTHROS NOS-L/LEG ||A| \r\n" + 
 "IN1|1|MEDICARE|3|MEDICARE|||||||Cartoon Ducks Inc|19891001|||4|DUCK^DONALD^D|1|19241010|111^DUCK ST^^FOWL^CA^999990000|||||||||||||||||123121234A||||||PT|M|111 DUCK ST^^FOWL^CA^999990000|||||8291 \r\n" + 
 "IN2|1||123121234|Cartoon Ducks Inc|||123121234A|||||||||||||||||||||||||||||||||||||||||||||||||||||||||8885551212 \r\n" + 
 "IN1|2|NON-PRIMARY|9|MEDICAL MUTUAL CALIF.|PO BOX 94776^^HOLLYWOOD^CA^441414776||8003621279|PUBSUMB|||Cartoon Ducks Inc||||7|DUCK^DONALD^D|1|19241010|111 DUCK ST^^FOWL^CA^999990000|||||||||||||||||056269770||||||PT|M|111^DUCK ST^^FOWL^CA^999990000|||||8291 \r\n" + 
 "IN2|2||123121234|Cartoon Ducks Inc||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||8885551212 \r\n" + 
 "IN1|3|SELF PAY|1|SELF PAY|||||||||||5||1 "  ;
  


function init(){
 	workData.msg            = hl7msg;
 	workData.srcDataType	= "hl7v2";
 	
// 	Handlebars.registerHelper('getFirstSegments',  getFirstSegments);  	
// 	Handlebars.registerHelper('generateUUID', 	   generateUUID);
// 	Handlebars.registerHelper('getSegmentLists',   getSegmentLists);
   
 	console.log("DIR NAME=" + __dirname);
// 	handlebarsInstances = {};
// 	handlebarsInstances["hl7v2"] = Handlebars.create();
 	

 	/*
 	var origResolvePartial      = handlebarsInstances[dataType].VM.resolvePartial;
 	
 	Handlebars.registerPartial('patient',"{{ ID }}" );
 	*/
 	/*
    helpers.forEach(h => {
    	console.log("register " + h.name);	
    	handlebarsInstances["hl7v2"].registerHelper(h.name, h.func);
    });
	*/
 	
	//var templateFile   = "C:/Progetti/natmaterial/HealthAdapterFacade/FHIR-Converter/templates/hl7v2/ADT_A01.hbs";
	var templateFile   = constants.TEMPLATE_FILES_LOCATION +"/hl7v2/ADT_A01.hbs"; 
	var templateString = fs.readFile(templateFile, 'utf-8', (err, data) => { 
		if (err) throw err; 
		//console.log(data); 
		workData.templateString = data;

//		console.log("HL7 msg=\n" + workData.msg);			 
//		console.log("\n\nTEMPLATE STRING=\n ");			 // + workData.templateString VEDI IL FILE in templates

		dataTypeHandler   = dataHandlerFactory.createDataHandler(workData.srcDataType);		
		//console.log("dataTypeHandler " + Object.keys(dataTypeHandler));	//(dataType)
//		console.log("dataTypeHandler.dataType " + dataTypeHandler.dataType );	//(hl7v2)
		handlebarInstance = GetHandlebarsInstance(dataTypeHandler, null);
		convert( workData );
	
	}); 
}

function convert(msg){

	try {
		//const base64RegEx = /^[a-zA-Z0-9/\r\n+]*={0,2}$/;
		//console.log("=== convert base64RegEx " + base64RegEx);
		
//		var dataTypeHandler   = dataHandlerFactory.createDataHandler(msg.srcDataType);		
//		console.log("dataTypeHandler " + Object.keys(dataTypeHandler));
//		handlebarInstance = GetHandlebarsInstance(dataTypeHandler, null);

 		
		console.log("\n============================ uniboworker COMPILE ===========================================");
 		var template = handlebarInstance.compile(workData.templateString);
		//console.log("COMPILED TEMPLATE=\n " + template);	
		
		

		dataTypeHandler.parseSrcData(msg.msg)		//See hl7v2.js
            	.then((parsedData) => {
                  		console.log("dataContext parsedData:" + Object.keys(parsedData));
                      	var dataContext = { msg: parsedData };
                        console.log("\n============================ PARSED INPUT DATA ===========================================");
//                        console.log("uniboworker dataContext.msg.v2.meta \n" + dataContext.msg.v2.meta );		//BY AN ( MSH,EVN,PID,NK1,PV1,GT1,DG1,IN1,IN2,IN1,IN2,IN1)
//                        console.log("uniboworker dataContext.msg.v2.data \n" + dataContext.msg.v2.data );		//BY AN (body del msg HL7 privato di MSH|, EVN|, PID|etc)
//                        console.log("\n\n ");
                		
//                        var result  = template( dataContext  );
//                		console.log("RESULT=\n " + result);	

        				var BYANgenratedRsult = generateResult( dataTypeHandler,dataContext, template );
/*
        				console.log("\n============================ worker GENERATED RESULT ===========================================");
        				console.log("worker keys= " + Object.keys(BYANgenratedRsult.fhirResource.entry) );	//BY AN 
        				console.log("worker legth=" +  BYANgenratedRsult.fhirResource.entry.length );	//BY AN 
 
        				for( i=0;i<BYANgenratedRsult.fhirResource.entry.length;i++){
        				 console.log("worker entry["+i+"].resource " + Object.keys(BYANgenratedRsult.fhirResource.entry[i])); //BY AN   + Object.keys(BYANgenratedRsult.fhirResource.entry[i].resource)
        				} 

        				for( i=0;i<BYANgenratedRsult.fhirResource.entry.length;i++){
        				 console.log("\n============================ worker GENERATED RESULT entry["+i+"] =======================================");
        				 console.log("worker entry["+i+"].resource.resourceType " + BYANgenratedRsult.fhirResource.entry[i].resource.resourceType);		//BY AN  
        				 console.log("worker entry["+i+"].resource.is " + BYANgenratedRsult.fhirResource.entry[i].resource.id );		//BY AN  
        				}           	
*/        				
        				var outS = JSON.stringify(BYANgenratedRsult.fhirResource, null, 2);
        				
        				fs.writeFile('DisiOutput.txt', outS, (err) => {  if (err) throw err; }) 
//        				console.log( outS );
            	
            	
            	})
				.catch (err => {
                    console.log(  err.toString() );
                });
				

		
	}catch (err) {
		console.log("convert error=\n"+err);
    }
}//convert

 
init();
 


