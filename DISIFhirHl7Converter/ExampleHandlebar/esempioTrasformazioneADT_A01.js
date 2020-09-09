/*
===================================================================================
esempioTrasformazioneADT_A01.js

Realizza una trasformazione usando i componenti DisiFhirConverter

Lo scopo e' porre in evidenza i passi necessari per realizzare una
trasformazione usando Handlebar nella versione Node.

Le varie fasi sono rese esplicite nella operazione doJob
===================================================================================
*/
var handlebars = require('handlebars');
var fs 		   = require('fs');
var hl7v2 	   = require('../DisiFhirConverter/hl7v2/hl7v2');
var helpers    = require('../DisiFhirConverter/handlebars-converter/handlebars-helpers').external;
var constants  = require('../DisiFhirConverter/constants/constants');

var templateFilesLocation = "C:/Progetti/natmaterial/DISIFhirHl7Converter/DisiFhirConverter/templates/hl7v2" ;
var BYANCount 			  = 0;
var origResolvePartial    = handlebars.VM.resolvePartial;

var dataTypeHandler    	;
var hl7msg  		   	;
var template			;
var parserPromise		;

module.exports.getHandlebars = function(){
	return handlebars; //origResolvePartial;
}
module.exports.getTemplateLocation = function(){
	return templateFilesLocation;
}
/*
-------------------------------------------------------------------
DOJOB
-------------------------------------------------------------------
*/
function doJob(){
	console.log( "MY templateFilesLocation=" + templateFilesLocation );
	dataTypeHandler   = new hl7v2();
	/*
                var getNamespace       = require('cls-hooked').getNamespace;
				console.log(constants.CLS_NAMESPACE); 
                var session 	 	   = getNamespace(constants.CLS_NAMESPACE);
				console.log(session); 
                session.set(constants.CLS_KEY_HANDLEBAR_INSTANCE, handlebarInstance);
                session.set(constants.CLS_KEY_TEMPLATE_LOCATION, path.join(constants.TEMPLATE_FILES_LOCATION, dataTypeHandler.dataType));
 	*/
	registerTheHelpers();
	planRegistrationOfThePartialHelpers();

	readAndCompileTheTemplate();

	readTheHl7Message();
	parserPromise = parseHl7Msg();

	performTheTransformation(parserPromise);
}



/*
-------------------------------------------------------------------
REGISTER THE HELEPRS
-------------------------------------------------------------------
*/
function registerTheHelpers(){
	console.log( "%%% RESGISTER THE HELPERS"   );
	helpers.forEach(h => {	//h.name (if,eq,ne,...multiply,divide)
		handlebars.registerHelper(h.name, h.func);
	});
}
/*
-------------------------------------------------------------------
PLAN THE REGISTRATION BY NEED OF THE PARTIALS
-------------------------------------------------------------------
*/
function planRegistrationOfThePartialHelpers(){
		console.log( "%%% PLAN THE REGISTRATION BY NEED OF THE PARTIALS "   );
        handlebars.VM.resolvePartial = function (partial, context, options) {	//STORED-FUN
		var templateLoc = templateFilesLocation + "/" + options.name;
		console.log( "......... planRegistrationOfThePartialHelpers "  + options.name + " " +BYANCount );
 		//console.log( "OPTION:" + options.name  + " DIR=" + templateLoc); 
             if (! options.partials[options.name] ) {
                try {
                    var content = fs.readFileSync( templateLoc );
                    var preprocessedContent = dataTypeHandler.preProcessTemplate(content.toString());
                    // Need to set partial entry here due to a bug in Handlebars (refer # 70386).
                     if (! options.partials[options.name] ) {
                     	 if( ! Object.keys( options.partials ).includes(options.name) ){
 							console.log("......... after readFileSync:" + options.name + "  count=" + BYANCount);					 
					 		handlebars.registerPartial(options.name, preprocessedContent);  
 		                    options.partials[options.name] = preprocessedContent;
							BYANCount++;           //numero dei template registrati
						}
                    }
  			 		//console.log(  "......... Object.keys( options.partials ): " );		 
					//console.log(  Object.keys( options.partials )  );	//Resources/Patient.hbs,DataType/CX.hbs ...
                } catch (err) {
                    throw new Error(`Referenced partial template ${options.name} not found on disk : ${err}`);	 
                }
            }//if (!options.partials[options.name]) 
            else{
				console.log( "......... ALREADY REGISTERED "  + options.name  );
			} 
             var orpBYAN = origResolvePartial(partial, context, options);	//(0,1,...,283)
 			 //console.log(  orpBYAN );		//il template
            return orpBYAN;
        };//function
}

//Operazione di registrazione non automatizzata
function registerThePartialHelpersCumbersome(){
	var partialpatientcontent = fs.readFileSync(__dirname + "/Resources/Patient.hbs", 'utf8');
	//console.log( partialpatientcontent );
	var partialpdatacxcontent = fs.readFileSync(__dirname + "/DataType/CX.hbs", 'utf8');
	var partialpdatacodecontent = fs.readFileSync(__dirname + "/DataType/_code.hbs", 'utf8');
	var partialpdatadlncontent = fs.readFileSync(__dirname + "/DataType/DLN.hbs", 'utf8');
	
	handlebars.registerPartial( "Resources/Patient.hbs", partialpatientcontent  );	
	handlebars.registerPartial( "DataType/CX.hbs", partialpdatacxcontent  );	
	handlebars.registerPartial( "DataType/_code.hbs", partialpdatacodecontent  );	
	handlebars.registerPartial( "DataType/DLN.hbs", partialpdatadlncontent  );		
}

/*
-------------------------------------------------------------------
READ THE DATA
-------------------------------------------------------------------
*/
function readTheHl7Message(){
	console.log( "%%% READ THE HL7 MESSAGE "   );
	hl7msg  	= fs.readFileSync('ADT01-23.hl7', 'utf8');	
	//console.log(hl7msg) ;	
}

function readAndCompileTheTemplate(){
	console.log( "%%% READ THE TEMPLATE "   );
	templateStr	 = fs.readFileSync('ADT_A01Slim.hbs', 'utf-8');	
	//console.log(templateStr) ;	
	template     = handlebars.compile(templateStr);
}

/*
-------------------------------------------------------------------
PARSING THE HL7 MESSAGE
-------------------------------------------------------------------
*/
function  parseHl7Msg(){
	console.log( "%%% PARSE THE HL7 MESSAGE"   );
		
	return dataTypeHandler.parseSrcData( hl7msg );	
}
 

/*
-------------------------------------------------------------------
ELABORATE THE PARSED HL7 MESSAGE
-------------------------------------------------------------------
*/
function performTheTransformation(parserPromise){
	console.log(" ");
	console.log( "%%% PERFORM THE TRANSFORMATION "   );
	parserPromise.then((parsedData) => {
                       	var dataContext = { msg: parsedData };
						console.log("dataContext=" ) ;
						console.log( dataContext) ;
 						var result      = template(dataContext);
						console.log(" ");
						console.log(" ");
						console.log("----------------------------------------------- ");
						console.log("%%% RESULT OF THE TRANSFORMATION ");
						console.log("----------------------------------------------- ");
						//console.log(result)			
						fs.writeFileSync("result.txt", result);		
				 });	

}
  
doJob();

//USAGE: node exADT
