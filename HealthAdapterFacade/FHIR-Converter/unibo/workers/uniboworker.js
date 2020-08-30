/* 
 -------------------------------------------------------------------------------------------------
 REFACTORING
 npm install handlebars
 npm install uuid			.v3 ???
 	FOR INPUT
 npm install antlr4
 npm install deepmerge
 npm install underscore
 -------------------------------------------------------------------------------------------------
 */
var fs 					= require('fs');
var Handlebars 			= require('handlebars');
var dataHandlerFactory	= require('../dataHandler/dataHandlerFactory');


//var HandlebarsConverter = require('../handlebars-converter/handlebars-helpers');
var workData = new Object();

function generateUUID(urlNamespace) {
    return "todouuidv3"; //uuidv3(''.concat(urlNamespace), uuidv3.URL);
}

function getSegmentLists(msg, ...segmentIds) {
    try {
        return getSegmentListsInternal(msg, ...segmentIds);
    }
    catch (err) {
        throw `helper "getSegmentLists" : ${err}`;
    }
}


function getFirstSegments(msg, ...segmentIds) {
            try {
console.log("getFirstSegments segmentIds=\n" + segmentIds);	
console.log("getFirstSegments msg" + Object.keys(msg));
                var ret 		= {};
                var inSegments 	= {};
                for (var s = 0; s < segmentIds.length - 1; s++) { //-1 because segmentsIds includes the full message at the end
                    inSegments[segmentIds[s]] = true;
                }
                for (var i = 0; i < msg.meta.length; i++) {
                    if (inSegments[msg.meta[i]] && !ret[msg.meta[i]]) {
                        ret[msg.meta[i]] = msg.data[i];
                    }
                }
                return ret;
            }
            catch (err) {
                throw `helper "getFirstSegments" : ${err}`;
            }
 }      


var getSegmentListsInternal = function (msg, ...segmentIds) {
    var ret = {};
    for (var s = 0; s < segmentIds.length - 1; s++) { //-1 because segmentsIds includes the full message at the end
        var segOut = [];
        for (var i = 0; i < msg.meta.length; i++) {
            if (msg.meta[i] == segmentIds[s]) {
                segOut.push(msg.data[i]);
            }
        }
        ret[segmentIds[s]] = segOut;
    }
    return ret;
};

console.log("\n============================ uniboworker START ==========================================="  );

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
  


function start(){
 	workData.msg            = hl7msg;
 	workData.srcDataType	= "hl7v2";
 	
 	Handlebars.registerHelper('getFirstSegments',  getFirstSegments);
 	Handlebars.registerHelper('generateUUID', 	   generateUUID);
 	Handlebars.registerHelper('getSegmentLists',   getSegmentLists);
 	
	//var templateFile   = "C:/Progetti/natmaterial/HealthAdapterFacade/FHIR-Converter/templates/hl7v2/ADT_A01.hbs";
	var templateFile   = "ADT_A01.hbs"; 
	var templateString = fs.readFile(templateFile, 'utf-8', (err, data) => { 
		if (err) throw err; 
		//console.log(data); 
		workData.templateString = data;
		convert( workData );
	}); 
}

function convert(msg){

	try {
		console.log("HL7 msg=\n" + workData.msg);			 
		console.log("TEMPLATE STRING=\n " + workData.templateString);			 
		//const base64RegEx = /^[a-zA-Z0-9/\r\n+]*={0,2}$/;
		//console.log("=== convert base64RegEx " + base64RegEx);
		
 		var template = Handlebars.compile(workData.templateString);
		console.log("TEMPLATE=\n " + template);	
		
		var dataTypeHandler   = dataHandlerFactory.createDataHandler(msg.srcDataType);
		
		console.log("dataTypeHandler " + Object.keys(dataTypeHandler));

		dataTypeHandler.parseSrcData(msg.msg)		//See hl7v2.js
            	.then((parsedData) => {
                       	var dataContext = { res: parsedData };
                       	console.log("dataContext " + Object.keys(dataContext));
                        console.log("\n============================ PARSED INPUT DATA ===========================================");
                        console.log("worker dataContext.res.v2.meta \n" + dataContext.res.v2.meta );		//BY AN ( MSH,EVN,PID,NK1,PV1,GT1,DG1,IN1,IN2,IN1,IN2,IN1)
                        console.log("worker dataContext.res.v2.data \n" + dataContext.res.v2.data );		//BY AN (body del msg HL7 privato di MSH|, EVN|, PID|etc)
                        console.log("\n\n ");
                		
                        var result  = template( dataContext  );
                		console.log("RESULT=\n " + result);	

				})
				.catch (err => {
                    console.log(  err.toString() );
                });
		
	}catch (err) {
		console.log("convert error=\n"+err);
    }
}//convert

 
start();
 


