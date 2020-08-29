// -------------------------------------------------------------------------------------------------
// REFACTORING
// -------------------------------------------------------------------------------------------------
console.log("\n============================ uniboworker START ===========================================" );

var hl7msg = "MSH|^~\&|AccMgr|1|||20050110045504||ADT^A01|599102|P|2.3||| \n"+
 "EVN|A01|20050110045502|||||  \n" +
 "PID|1||10006579^^^1^MR^1||DUCK^DONALD^D||19241010|M||1|111 DUCK ST^^FOWL^CA^999990000^^M|1|8885551212|8885551212|1|2||40007716^^^AccMgr^VN^1|123121234|||||||||||NO \n" +
 "NK1|1|DUCK^HUEY|SO|3583 DUCK RD^^FOWL^CA^999990000|8885552222||Y|||||||||||||| \n" +
 "PV1|1|I|PREOP^101^1^1^^^S|3|||37^DISNEY^WALT^^^^^^AccMgr^^^^CI|||01||||1|||37^DISNEY^WALT^^^^^^AccMgr^^^^CI|2|40007716^^^AccMgr^VN|4|||||||||||||||||||1||G|||20050110045253|||||| ";

/*
GT1|1|8291|DUCK^DONALD^D||111^DUCK ST^^FOWL^CA^999990000|8885551212||19241010|M||1|123121234||||#Cartoon Ducks Inc|111^DUCK ST^^FOWL^CA^999990000|8885551212||PT| 
DG1|1|I9|71596^OSTEOARTHROS NOS-L/LEG ^I9|OSTEOARTHROS NOS-L/LEG ||A| 
IN1|1|MEDICARE|3|MEDICARE|||||||Cartoon Ducks Inc|19891001|||4|DUCK^DONALD^D|1|19241010|111^DUCK ST^^FOWL^CA^999990000|||||||||||||||||123121234A||||||PT|M|111 DUCK ST^^FOWL^CA^999990000|||||8291 
IN2|1||123121234|Cartoon Ducks Inc|||123121234A|||||||||||||||||||||||||||||||||||||||||||||||||||||||||8885551212 
IN1|2|NON-PRIMARY|9|MEDICAL MUTUAL CALIF.|PO BOX 94776^^HOLLYWOOD^CA^441414776||8003621279|PUBSUMB|||Cartoon Ducks Inc||||7|DUCK^DONALD^D|1|19241010|111 DUCK ST^^FOWL^CA^999990000|||||||||||||||||056269770||||||PT|M|111^DUCK ST^^FOWL^CA^999990000|||||8291 
IN2|2||123121234|Cartoon Ducks Inc||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||8885551212 
IN1|3|SELF PAY|1|SELF PAY|||||||||||5||1
";*/

function convert(msg){

	try {
 		console.log("=== convert msg.srcDataBase64  "  + msg.srcDataBase64  );
 		console.log("=== convert msg.templateBase64 " + msg.templateBase64 );
		const base64RegEx = /^[a-zA-Z0-9/\r\n+]*={0,2}$/;
		console.log("=== convert base64RegEx " + base64RegEx);
		if (!base64RegEx.test(msg.srcDataBase64)) {
			console.log("srcData is not a base 64 encoded string.")
		}
		if (!base64RegEx.test(msg.templateBase64)) {
			console.log("Template is not a base 64 encoded string.")
		}
	}catch (err) {
		console.log("error="+err);
    }
}//convert

console.log(hl7msg); 
convert(hl7msg); 


/*
console.log("worker /api/convert/:srcDataType");			//BY AN
//console.log("worker msg.type " + msg.type);					//BY AN	(/api/convert/:srcDataType)
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
console.log("\n============================ worker TEMPLATE ===========================================");                               
console.log("worker templateString=" + templateString);		//BY AN
*/