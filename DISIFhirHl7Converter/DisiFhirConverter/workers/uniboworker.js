// -------------------------------------------------------------------------------------------------
// Copyright (c) Disi-Unibo. All rights reserved.
// Licensed under the MIT License (MIT). See LICENSE in the repo root for license information.
// -------------------------------------------------------------------------------------------------
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
var uuidv3 				= require('uuid');
const {createNamespace} = require("cls-hooked");
var session 			= createNamespace(constants.CLS_NAMESPACE);

var workData = new Object();
var handlebarInstance ;
var dataTypeHandler;
var hl7DataFileName = "ADT01-23"; // "ADT01-28";

function callMeFromJava(a,b) {
	return "callMeFromJava done:" + a + " " + b;
}

function GetHandlebarsInstance(dataTypeHandler, templatesMap) {
console.log("\nunibo worker GetHandlebarsInstance " + constants.TEMPLATE_FILES_LOCATION );			 
    var instance = HandlebarsConverter.instance(true,
        dataTypeHandler,
        path.join(constants.TEMPLATE_FILES_LOCATION, dataTypeHandler.dataType),
        templatesMap);
    return instance;
}

function generateUUID(urlNamespace) {
    return uuidv3(''.concat(urlNamespace), uuidv3.URL); //"todouuidv3"; //
}

function generateResult( dataTypeHandler,dataContext, template ) {
console.log("\n============================ uniboworker GENERATE RESULT ===========================================");
console.log("worker generateResult dataContext.msg.v2.data " + dataContext.msg.v2.data );		 
console.log("worker generateResult dataContext.msg.v2.meta " + dataContext.msg.v2.meta ); //MSH,EVN,PID,NK1,PV1,GT1,DG1,IN1,IN2,IN1,IN2,IN1)
 	 
	var resultcvt = template(dataContext); 	 	//molti campi, 26566

	//Qui invoca handlebars-helpers.js getFirstSegments e poi esegue  stored-fun che registra le risorse 
     var result = dataTypeHandler.postProcessResult(resultcvt);	//campi resourceType,type,entry

     //prima esegue  getConversionResultMetadata poi parseCoverageReport
     console.log("---------------------------------------------------------------------------------------------------");
     for( i=0; i<result.entry.length; i++) {
     	 console.log("uniboworker generateResult entry["+i+"].resource.resourceType=" + 
				result.entry[i].resource.resourceType +"	keys=" + Object.keys(result.entry[i].resource));
     }
     

//copia tutte le proprietà enumerabili da uno o più oggetti di origine in un oggetto di destinazione, che Restituisce 
     var objBYAN = Object.assign(dataTypeHandler.getConversionResultMetadata(dataContext.msg), { 'fhirResource': result });
// console.log("\n============================ uniboworker GENERATE RESULT objBYAN " + Object.keys(objBYAN));		
// (  unusedSegments,invalidAccess,fhirResource )
     return objBYAN;
}


function readDataFromFile( fname ) {
	console.log("readDataFromFile	" + fname);
	return new Promise((fulfill, reject) => {
		fs.readFile( fname, 'utf-8', (err, data) => {
			if( err ) { console.log(  err.toString() ); }
			else { fulfill(data); }
		});
	})
}



function doconvert(workData, response){
	session.run( () => {
	try {
		dataTypeHandler   = dataHandlerFactory.createDataHandler(workData.srcDataType);		
		handlebarInstance = GetHandlebarsInstance(dataTypeHandler, null);		
			
			session.set(constants.CLS_KEY_HANDLEBAR_INSTANCE, handlebarInstance);	//BYAN Session
			session.set(constants.CLS_KEY_TEMPLATE_LOCATION, path.join(constants.TEMPLATE_FILES_LOCATION, dataTypeHandler.dataType));

		console.log("\n============================ uniboworker doconvert COMPILE  ");
 		var template = handlebarInstance.compile(workData.templateString);
		//console.log("COMPILED TEMPLATE=\n " + template);			
		dataTypeHandler.parseSrcData(workData.msg)		//See hl7v2.js
            	.then((parsedData) => {
                       	var dataContext = { msg: parsedData };
                        console.log("\n============================ uniboworker  doconvert PARSED DATA  ");
        				var genratedRsult 	= generateResult( dataTypeHandler, dataContext, template );
        				var outS 			= JSON.stringify(genratedRsult.fhirResource, null, 2);        				
         				fs.writeFile(hl7DataFileName+".txt", outS, (err) => {  if (err) throw err; }) 
        				console.log("uniboworker convert outS=" + outS);
        				if( response != null ){
							response.write( outS );
        					response.end();
						}
            	})
				.catch (err => {
                    console.log(  err.toString() );
                });
	}catch (err) {
		console.log("convert error=\n"+err);
    }
	});
}//doconvert


module.exports.convert= function( templateString, HL7Msg, response ) {
	//console.log("uniboworker convert " + HL7Msg);
	workData.srcDataType	= "hl7v2";
	workData.templateString = templateString;
	workData.msg            = HL7Msg;
	doconvert(workData, response);
}

 

 

//console.log("\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& uniboworker init &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"  );

//module.exports = convert;
 


