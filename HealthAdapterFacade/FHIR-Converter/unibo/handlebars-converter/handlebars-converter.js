// -------------------------------------------------------------------------------------------------
// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License (MIT). See LICENSE in the repo root for license information.
// -------------------------------------------------------------------------------------------------

var fs         = require('fs');
var Handlebars = require('handlebars');
var helpers    = require('./handlebars-helpers').external;

var handlebarsInstances = {};

var BYANCount = 0;

module.exports.instance = function (createNew, dataHandler, templateFilesLocation, currentContextTemplatesMap) {
console.log("%%%%%%%%%%%%% handlebars-converter %%%%%%%%%%%%%%%%%%%%%%%%%%" + templateFilesLocation);		//BY AN		
    if (createNew) {
        handlebarsInstances = {};
    }

    let dataType = dataHandler.dataType;
    if (!handlebarsInstances[dataType]) {
        handlebarsInstances[dataType] = Handlebars.create();
        var origResolvePartial        = handlebarsInstances[dataType].VM.resolvePartial;
/*
 * STORED FUN
 */
        handlebarsInstances[dataType].VM.resolvePartial = function (partial, context, options) {
/*
 * Viene chiamata 94 volte. Registra il codice dei template contenuti in templateFilesLocation
 */
BYANCount++;           //BYAN (1..94)
           //BYAN (1..94)
//console.log("handlebars-converter options=" + Object.keys(options) );	 //BY AN (name,hash,data,helpers,partials,decorators)		 
//console.log("================== handlebars-converter stored-fun ===== " + BYANCount ); //BY AN
            if (!options.partials[options.name]) {
                try {
                    var content;
                    if (currentContextTemplatesMap && options.name in currentContextTemplatesMap) {
                        content = currentContextTemplatesMap[options.name];
console.log("handlebars-converter stored-fun get content from map:" + options.name + " count=" + BYANCount );	//BY AN		 
                    }
                    else {
                        content = fs.readFileSync(templateFilesLocation + "/" + options.name);
console.log("unibo handlebars-converter stored-fun get content from file:" +  options.name + " count=" + BYANCount );	//BY AN		 
                    }
                    var preprocessedContent = dataHandler.preProcessTemplate(content.toString());
                    handlebarsInstances[dataType].registerPartial(options.name, preprocessedContent);
console.log("unibo handlebars-converter stored-fun registerPartial:" + options.name );					//BY AN		 

                    // Need to set partial entry here due to a bug in Handlebars (refer # 70386).
                    /* istanbul ignore else  */
                    if (!options.partials[options.name]) {
                        options.partials[options.name] = preprocessedContent;
                    }
                } catch (err) {
                    throw new Error(`Referenced partial template ${options.name} not found on disk : ${err}`);
                }
            }//if (!options.partials[options.name]) 
//console.log("handlebars-converter stored-fun count=" + BYANCount + " partial=" + partial);		//BY AN	(undefined)	 
            
            var orpBYAN = origResolvePartial(partial, context, options);
//if( partial != undefined ) console.log("handlebars-converter stored-fun count=" + BYANCount + " partial=" + partial);	    //BY AN	( )	 
/*
console.log("handlebars-converter dataType=" + dataType);					//BY AN	()	 
//console.log("handlebars-converter orpBYAN="  + Object.keys(orpBYAN));		//BY AN	(0,1,...,283)
console.log("handlebars-converter orpBYAN="  +  orpBYAN);		//BY AN		
*/
//console.log("handlebars-converter stored-fun return with count=" + BYANCount );	    //BY AN	( )	
            return orpBYAN;
        };//function

        helpers.forEach(h => {
//console.log("register " + h.name);	//BY AN		(if,eq,ne,...multiply,divide)
            handlebarsInstances[dataType].registerHelper(h.name, h.func);
        });
    }//if (!handlebarsInstances[dataType]) 

console.log("%%%%%%%%%%%%% handlebars-converter %%%%%%%%%%%%%%%%%%%%%%%%%% "   );		//BY AN	
//console.log("%%%%%%%%%%%%% handlebars-converter %%%%%%%%%%%%%%%%%%%%%%%%%% " + Object.keys(handlebarsInstances[dataType]) );		//BY AN	
/* 
 * helpers,partials,decorators,__esModule,HandlebarsEnvironment,
 * VERSION,COMPILER_REVISION,LAST_COMPATIBLE_COMPILER_REVISION,REVISION_CHANGES,
 * log,createFrame,logger,SafeString,Exception,Utils,escapeExpression,
 * VM,template,compile,precompile,AST,Compiler,JavaScriptCompiler,Parser,parse,parseWithoutProcessing
*/

	return handlebarsInstances[dataType];
};
