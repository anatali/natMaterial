// -------------------------------------------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License (MIT). See LICENSE in the repo root for license information.
// -------------------------------------------------------------------------------------------------


var path 				= require('path');
var fs 					= require('fs');
var Promise 			= require('promise');
var compileCache 		= require('memory-cache');
var constants 			= require('../constants/constants');
var errorCodes 			= require('../error/error').errorCodes;
var errorMessage 		= require('../error/error').errorMessage;
var HandlebarsConverter = require('../handlebars-converter/handlebars-converter');
var WorkerUtils 		= require('./workerUtils');
var dataHandlerFactory	= require('../dataHandler/dataHandlerFactory');
const {createNamespace} = require("cls-hooked");
var session 			= createNamespace(constants.CLS_NAMESPACE);

var rebuildCache 		= true;


function GetHandlebarsInstance(dataTypeHandler, templatesMap) {
    // New instance should be created when using templatesMap
    let needToUseMap = templatesMap && Object.entries(templatesMap).length > 0 && templatesMap.constructor === Object;
    var instance = HandlebarsConverter.instance(needToUseMap ? true : rebuildCache,
        dataTypeHandler,
        path.join(constants.TEMPLATE_FILES_LOCATION, dataTypeHandler.dataType),
        templatesMap);
    rebuildCache = needToUseMap ? true : false; // New instance should be created also after templatesMap usage

    return instance;
}

function expireCache() {
    rebuildCache = true;
    compileCache.clear();
}


/*
dataTypeHandler 		: hl7v2.js
dataContext.msg.v2.data : contiene il body del msg HL7 privato di MSH|, EVN|, PID|etc
dataContext.msg.v2.data : contiene MSH,EVN,PID,NK1,PV1,GT1,DG1,IN1,IN2,IN1,IN2,IN1

result 					: ha i campi resourceType,type,entry
result.resourceType		: Bundle	
result.type				: transaction		
result.entry		    : [object Object],[object Object],[object Object],[object Object],[object Object]

LINE 72: getConversionResultMetadata definito in hl7v2.js chiama parseCoverageReport 
che restituisce un oggetto con i campi: unusedSegments,invalidAccess,fhirResource

*/

function generateResult(dataTypeHandler, dataContext, template) {
console.log("worker generateResult dataTypeHandler " + Object.keys(dataTypeHandler));			//BY AN (dataType)
console.log("worker generateResult dataContext " + Object.keys(dataContext));					//BY AN (msg)
console.log("worker generateResult dataContext.msg " + Object.keys(dataContext.msg) );			//BY AN (v2)
console.log("worker generateResult dataContext.msg.v2 " + Object.keys(dataContext.msg.v2) );	//BY AN (data,meta)
console.log("worker generateResult dataContext.msg.v2.data " + dataContext.msg.v2.data );		//BY AN
console.log("worker generateResult dataContext.msg.v2.meta " + dataContext.msg.v2.meta );		//BY AN ( MSH,EVN,PID,NK1,PV1,GT1,DG1,IN1,IN2,IN1,IN2,IN1)
 
 	 var resultBYAN = template(dataContext);
 //console.log("worker generateResult resultBYAN " + Object.keys(resultBYAN) );		//BY AN (molti numerici)
 
     var result = dataTypeHandler.postProcessResult(resultBYAN);
 console.log("worker generateResult result " + Object.keys(result));				//BY AN (resourceType,type,entry)
 console.log("worker generateResult result.resourceType " + result.resourceType );	//BY AN (  Bundle )
 console.log("worker generateResult result.type " + result.type );					//BY AN ( transaction )
 console.log("worker generateResult result.entry " + result.entry);					//BY AN ( [object Object],[object Object],[object Object],[object Object],[object Object])
     
     //copia tutte le proprietà enumerabili da uno o più oggetti di origine in un oggetto di destinazione. 
     //Restituisce l'oggetto di destinazione.
     var objBYAN = Object.assign(dataTypeHandler.getConversionResultMetadata(dataContext.msg), { 'fhirResource': result });
 console.log("worker generateResult objBYAN " + Object.keys(objBYAN));		//BY AN (  unusedSegments,invalidAccess,fhirResource )
     return objBYAN;
}

WorkerUtils.workerTaskProcessor((msg) => {
    return new Promise((fulfill, reject) => {
        session.run(() => {
            switch (msg.type) {
                case '/api/convert/:srcDataType':
                    {
                        try {
console.log("worker /api/convert/:srcDataType");			//BY AN
console.log("worker msg.type " + msg.type);					//BY AN	(/api/convert/:srcDataType)
                            const base64RegEx = /^[a-zA-Z0-9/\r\n+]*={0,2}$/;

                            if (!base64RegEx.test(msg.srcDataBase64)) {
                                reject({ 'status': 400, 'resultMsg': errorMessage(errorCodes.BadRequest, "srcData is not a base 64 encoded string.") });
                            }

                            if (!base64RegEx.test(msg.templateBase64)) {
                                reject({ 'status': 400, 'resultMsg': errorMessage(errorCodes.BadRequest, "Template is not a base 64 encoded string.") });
                            }

                            var templatesMap = undefined;
                            if (msg.templatesOverrideBase64) {
                                if (!base64RegEx.test(msg.templatesOverrideBase64)) {
                                    reject({ 'status': 400, 'resultMsg': errorMessage(errorCodes.BadRequest, "templatesOverride is not a base 64 encoded string.") });
                                }
                                templatesMap = JSON.parse(Buffer.from(msg.templatesOverrideBase64, 'base64').toString());
                            }


                            var templateString = "";
                            if (msg.templateBase64) {
                                templateString = Buffer.from(msg.templateBase64, 'base64').toString();
                            }

                            try {
                                var b = Buffer.from(msg.srcDataBase64, 'base64');
                                var s = b.toString();
                            }
                            catch (err) {
                                reject({ 'status': 400, 'resultMsg': errorMessage(errorCodes.BadRequest, `Unable to parse input data. ${err.message}`) });
                            }
  console.log("worker msg.srcDataType " + msg.srcDataType);							//BY AN (hl7v2)
                            var dataTypeHandler   = dataHandlerFactory.createDataHandler(msg.srcDataType);
 
 console.log("worker dataTypeHandler " + Object.keys(dataTypeHandler));				//BY AN ( dataType)
 console.log("worker dataTypeHandler.dataType " + dataTypeHandler.dataType);		//BY AN  (hl7v2 )
 
                            let handlebarInstance = GetHandlebarsInstance(dataTypeHandler, templatesMap);
                            session.set(constants.CLS_KEY_HANDLEBAR_INSTANCE, handlebarInstance);
                            session.set(constants.CLS_KEY_TEMPLATE_LOCATION, path.join(constants.TEMPLATE_FILES_LOCATION, dataTypeHandler.dataType));

                            dataTypeHandler.parseSrcData(s)		//See hl7v2.js
                                .then((parsedData) => {
                                    var dataContext = { msg: parsedData };
 console.log("worker s " + s);											//BY AN (HL7 input data)
 console.log("worker parsedData " + parsedData);						//BY AN	 [object Object]
 console.log("worker dataContext.msg " + dataContext.msg);				//BY AN	 [object Object]
 
                                    if (templateString == null || templateString.length == 0) {
                                        var result = Object.assign(dataTypeHandler.getConversionResultMetadata(dataContext.msg),
                                            JSON.parse(JSON.stringify(dataContext.msg)));
 console.log("worker result " + result);				//BY AN

                                        fulfill({ 'status': 200, 'resultMsg': result });
                                    }
                                    else {
/* 
COMPILAZIONE DI  templateString (IL TEMPLATE) CHE FORNISCE   (preprocessing  -inherited da dataHandler.js - non sembra fare nulla)
function ret(context, execOptions) {
    if (!compiled) {
      compiled = compileInput();
    }
    return compiled.call(this, context, execOptions);
  }
*/                               
                                        var template = handlebarInstance.compile(dataTypeHandler.preProcessTemplate(templateString));

console.log("worker templateString=" + templateString);	//BY AN
console.log("worker template=> " + template);				//BY AN

                                        try {
                                        	var BYANgenratedRsult = generateResult(dataTypeHandler, dataContext, template);
 
 console.log("worker BYANgenratedRsult " + Object.keys(BYANgenratedRsult) );								//BY AN (unusedSegments,invalidAccess,fhirResource)
 console.log("worker BYANgenratedRsult.fhirResource " +  Object.keys(BYANgenratedRsult.fhirResource) );		//BY AN (resourceType,type,entry) 
 console.log("worker fhirResource.resourceType " + BYANgenratedRsult.fhirResource.resourceType );			//BY AN (  Bundle )
 console.log("worker fhirResource.result.type " + BYANgenratedRsult.fhirResource.type );					//BY AN ( transaction )
 console.log("worker fhirResource.result.entry " +  Object.keys(BYANgenratedRsult.fhirResource.entry) );	//BY AN (0,1,2,3,4 )
 
for( i=0;i<5;i++){
 console.log("worker fhirResource.result.entry["+i+"] " + Object.keys(BYANgenratedRsult.fhirResource.entry[i]));		//BY AN ( fullUrl,resource,request )
} 

for( i=0;i<5;i++){
 console.log("worker fhirResource.result.entry["+i+"]..resource.resourceType " +  BYANgenratedRsult.fhirResource.entry[i].resource.resourceType);		//BY AN ( fullUrl,resource,request )
 console.log("worker fhirResource.result.entry["+i+"]..resource.id " +  BYANgenratedRsult.fhirResource.entry[i].resource.id);		//BY AN ( fullUrl,resource,request )
} 
/*
 console.log("worker entry[0].fullUrl " + BYANgenratedRsult.fhirResource.entry[0].fullUrl);		//BY AN ( urn:uuid:64bac34e-e611-3549-848b-89416176aa0b )
 console.log("worker entry[0].resource " + Object.keys(BYANgenratedRsult.fhirResource.entry[0].resource));		//BY AN (  resourceType,id,identifier,name,birthDate,gender,address,telecom,communication   )
 console.log("worker entry[0].request " + Object.keys(BYANgenratedRsult.fhirResource.entry[0].request));		//BY AN (  method,url )

 console.log("worker entry[1].resource " + Object.keys(BYANgenratedRsult.fhirResource.entry[1].resource));		//BY AN (  resourceType,id,class,status,location,hospitalization,participant,serviceType,identifier,period,subject,diagnosis   )
 console.log("worker entry[1].request " + Object.keys(BYANgenratedRsult.fhirResource.entry[1].request));		//BY AN (  method,url )

 console.log("worker entry[0].resource.resourceType " + BYANgenratedRsult.fhirResource.entry[0].resource.resourceType);		//BY AN ( Patient   )
 console.log("worker entry[0].resource.id " + BYANgenratedRsult.fhirResource.entry[0].resource.id);		//BY AN (    )
 console.log("worker entry[0].resource.address " + BYANgenratedRsult.fhirResource.entry[0].resource.address);		//BY AN (    )

 console.log("worker entry[1].resource.resourceType " + BYANgenratedRsult.fhirResource.entry[1].resource.resourceType);		//BY AN ( Encounter   )
 console.log("worker entry[1].resource.id " + BYANgenratedRsult.fhirResource.entry[1].resource.id);		//BY AN (    )
 console.log("worker entry[1].resource.diagnosis " + BYANgenratedRsult.fhirResource.entry[1].resource.diagnosis);		//BY AN (    )
*/
                                            fulfill({ 'status': 200, 'resultMsg': BYANgenratedRsult });
                                        }
                                        catch (err) {
                                            reject({ 'status': 400, 'resultMsg': errorMessage(errorCodes.BadRequest, "Unable to create result: " + err.toString()) });
                                        }
                                    }

                                })
                                .catch(err => {
                                    reject({ 'status': 400, 'resultMsg': errorMessage(errorCodes.BadRequest, `Unable to parse input data. ${err.toString()}`) });
                                });
                        }
                        catch (err) {
                            reject({ 'status': 400, 'resultMsg': errorMessage(errorCodes.BadRequest, `${err.toString()}`) });
                        }
                    }
                    break;

                case '/api/convert/:srcDataType/:template':
                    {
console.log("worker /api/convert/:srcDataType:template");	//BY AN

                        let srcData           = msg.srcData;
                        let templateName      = msg.templateName;
                        let srcDataType       = msg.srcDataType;
                        let dataTypeHandler   = dataHandlerFactory.createDataHandler(srcDataType);
                        let handlebarInstance = GetHandlebarsInstance(dataTypeHandler);
                        session.set(constants.CLS_KEY_HANDLEBAR_INSTANCE, handlebarInstance);
                        session.set(constants.CLS_KEY_TEMPLATE_LOCATION, path.join(constants.TEMPLATE_FILES_LOCATION, dataTypeHandler.dataType));

                        if (!srcData || srcData.length == 0) {
                            reject({ 'status': 400, 'resultMsg': errorMessage(errorCodes.BadRequest, "No srcData provided.") });
                        }

                        const getTemplate = (templateName) => {
                            return new Promise((fulfill, reject) => {
                                var template = compileCache.get(templateName);
                                if (!template) {
                                    fs.readFile(path.join(constants.TEMPLATE_FILES_LOCATION, srcDataType, templateName), (err, templateContent) => {
                                        if (err) {
                                            reject({ 'status': 404, 'resultMsg': errorMessage(errorCodes.NotFound, "Template not found") });
                                        }
                                        else {
                                            try {
                                                template = handlebarInstance.compile(dataTypeHandler.preProcessTemplate(templateContent.toString()));
                                                compileCache.put(templateName, template);
                                                fulfill(template);
                                            }
                                            catch (convertErr) {
                                                reject({
                                                    'status': 400,
                                                    'resultMsg': errorMessage(errorCodes.BadRequest,
                                                        "Error during template compilation. " + convertErr.toString())
                                                });
                                            }
                                        }
                                    });
                                }
                                else {
                                    fulfill(template);
                                }
                            });
                        };

                        dataTypeHandler.parseSrcData(srcData)
                            .then((parsedData) => {
                                var dataContext = { msg: parsedData };
                                getTemplate(templateName)
                                    .then((compiledTemplate) => {
                                        try {
                                            fulfill({
                                                'status': 200, 'resultMsg': generateResult(dataTypeHandler, dataContext, compiledTemplate)
                                            });
                                        }
                                        catch (convertErr) {
                                            reject({
                                                'status': 400,
                                                'resultMsg': errorMessage(errorCodes.BadRequest,
                                                    "Error during template evaluation. " + convertErr.toString())
                                            });
                                        }
                                    }, (err) => {
                                        reject(err);
                                    });
                            })
                            .catch(err => {
                                reject({ 'status': 400, 'resultMsg': errorMessage(errorCodes.BadRequest, `Unable to parse input data. ${err.toString()}`) });
                            });
                    }
                    break;

                case 'templatesUpdated':
                    {
                        expireCache();
                        fulfill();
                    }
                    break;

                case 'constantsUpdated':
                    {
                        constants = JSON.parse(msg.data);
                        expireCache();
                        fulfill();
                    }
                    break;
            }
        });
    });
});
