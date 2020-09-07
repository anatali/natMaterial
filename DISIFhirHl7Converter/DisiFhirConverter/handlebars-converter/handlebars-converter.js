// -------------------------------------------------------------------------------------------------
// Copyright (c) Disi-Unibo. All rights reserved.
// Licensed under the MIT License (MIT). See LICENSE in the repo root for license information.
// -------------------------------------------------------------------------------------------------

var fs         = require('fs');
var Handlebars = require('handlebars');
var helpers    = require('./handlebars-helpers').external;

var handlebarsInstances = {};

var BYANCount = 0;

module.exports.instance = function (createNew, dataHandler, templateFilesLocation, currentContextTemplatesMap) {
console.log("unibo handlebars-converter  " + templateFilesLocation);		//BY AN		
    if (createNew) {
        handlebarsInstances = {};
    }

    let dataType = dataHandler.dataType;
    if (!handlebarsInstances[dataType]) {
        handlebarsInstances[dataType] = Handlebars.create();
        var origResolvePartial        = handlebarsInstances[dataType].VM.resolvePartial;

        handlebarsInstances[dataType].VM.resolvePartial = function (partial, context, options) {	//STORED-FUN
// Viene chiamata 94 volte. Registra il codice dei template contenuti in templateFilesLocation
BYANCount++;           //BYAN (1..94)
//console.log("%%%%%%%%%%%%% handlebars-converter BYANCount=" + BYANCount );
//console.log("handlebars-converter options=" + Object.keys(options) );	 //(name,hash,data,helpers,partials,decorators)		 
            if (!options.partials[options.name]) {
                try {
                    var content;
                    if (currentContextTemplatesMap && options.name in currentContextTemplatesMap) {
                        content = currentContextTemplatesMap[options.name];
console.log("handlebars-converter stored-fun get content from map:" + options.name + " count=" + BYANCount );	//BY AN		 
                    }
                    else {
                        content = fs.readFileSync(templateFilesLocation + "/" + options.name);
//console.log("unibo handlebars-converter stored-fun get content from file:" +  options.name + " count=" + BYANCount ); 
                    }
                    var preprocessedContent = dataHandler.preProcessTemplate(content.toString());
                    handlebarsInstances[dataType].registerPartial(options.name, preprocessedContent);
console.log("unibo handlebars-converter stored-fun registerPartial:" + options.name + "  count=" + BYANCount);					 

                    // Need to set partial entry here due to a bug in Handlebars (refer # 70386).
                    /* istanbul ignore else  */
                    if (!options.partials[options.name]) {
                        options.partials[options.name] = preprocessedContent;
                    }
                } catch (err) {
                    throw new Error(`Referenced partial template ${options.name} not found on disk : ${err}`);
                }
            }//if (!options.partials[options.name]) 
             
            var orpBYAN = origResolvePartial(partial, context, options);	//(0,1,...,283)
             return orpBYAN;
        };//function

console.log("-----------------------------------------------------------------------------------------------------" );					 
console.log("unibo handlebars-converter registerHelper for handlebars-helpers.js: if,eq,ne,...multiply,divide" );					 
console.log("-----------------------------------------------------------------------------------------------------" );					 
        helpers.forEach(h => {	//h.name (if,eq,ne,...multiply,divide)
            handlebarsInstances[dataType].registerHelper(h.name, h.func);
        });
    }//if (!handlebarsInstances[dataType]) 

 

//    console.log("%%%%%%%%%%%%% handlebars-converter " + handlebarsInstances[dataType].VM.resolvePartial );
          
	return handlebarsInstances[dataType];
};
