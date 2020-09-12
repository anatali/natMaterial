// -------------------------------------------------------------------------------------------------
// Copyright (c) Disi-Unibo. All rights reserved.
// Licensed under the MIT License (MIT). See LICENSE in the repo root for license information.
// -------------------------------------------------------------------------------------------------

var  worker			= require('../workers/uniboworker');
var constants 		= require('../constants/constants');
var hl7DataFileName = "ADT01-23"; // "ADT01-28";
var fs 				= require('fs');

function readDataFromFile( fname ) {
	console.log("%%% rununiboworker readDataFromFile:" + fname);
	return new Promise((fulfill, reject) => {
		fs.readFile( fname, 'utf-8', (err, data) => {
			if( err ) { console.log(  err.toString() ); }
			else { fulfill(data); }
		});
	})
}
 
function init( templateFile ){
	console.log("");
 	console.log("%%% rununiboworker STARTS"  );
 	console.log("%%% rununiboworker CURRENT DIRECTORY:" + __dirname);
 	var hl7Datafile    = constants.HL7_DATAFILES_LOCATION  + "/hl7v2/"+hl7DataFileName+".hl7";
	
	readDataFromFile( hl7Datafile )
		.then( (data) => {
			//console.log("hl7data "  + data );
			//workData.msg = data;
			readDataFromFile( templateFile )
			.then( (templateString) => {
				//workData.templateString = templateString;
				worker.convert(templateString, data, null);
			});
		});
}

 

init( constants.TEMPLATE_FILES_LOCATION + "/hl7v2/ADT_A01.hbs" );
/*
setTimeout(function(){
	console.log("\n============================ uniboworker REDOING  ");
	init( constants.TEMPLATE_FILES_LOCATION + "/hl7v2/ADT_A01Slim.hbs" )	
}, 1000);

setTimeout(function(){
	console.log("\n============================ uniboworker REDOING  ");
	init( constants.TEMPLATE_FILES_LOCATION + "/hl7v2/ADT_A01Slim.hbs" )	
}, 2000);

*/