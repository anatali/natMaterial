var path 				= require('path');
var constants 			= require('../constants/constants');
var fs 					= require('fs');
var Handlebars 			= require('handlebars');
var dataHandlerFactory	= require('../dataHandler/dataHandlerFactory');
var HandlebarsConverter = require('../handlebars-converter/handlebars-converter');


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
    return "todouuidv3"; //uuidv3(''.concat(urlNamespace), uuidv3.URL);
}

function generateResult( dataTypeHandler,dataContext, template ) {
console.log("\n============================ uniboworker GENERATE RESULT ===========================================");
 	 
	var resultcvt = template(dataContext); 	 	//molti campi, 26566

	//Qui invoca handlebars-helpers.js getFirstSegments e poi esegue  stored-fun che registra le risorse
  
     var result = dataTypeHandler.postProcessResult(resultcvt);	//campi resourceType,type,entry
     //prima esegue  getConversionResultMetadata poi parseCoverageReport
     for( i=0; i<result.entry.length; i++) {
     	 console.log("entry["+i+"].resource.resourceType=" + result.entry[i].resource.resourceType +
    			 "	keys=" + Object.keys(result.entry[i].resource));
     }
     

//copia tutte le proprietà enumerabili da uno o più oggetti di origine in un oggetto di destinazione, che Restituisce 
     var objBYAN = Object.assign(
    		 dataTypeHandler.getConversionResultMetadata(dataContext.msg), { 'fhirResource': result });
// console.log("\n============================ uniboworker GENERATE RESULT objBYAN " + Object.keys(objBYAN));		
// (  unusedSegments,invalidAccess,fhirResource )
     return objBYAN;
}

 function readDataFromFile( fname ) {
 	return new Promise( (fulfill, reject) => { 
		fs.readFile( fname, 'utf-8', ((err, data) => {
			if( err ) { console.log(  err.toString() ); }
			else { fulfill(data); }
		}));		 
	})
}



