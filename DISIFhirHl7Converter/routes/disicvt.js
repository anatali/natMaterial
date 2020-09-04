var express 	= require('express');
var router 		= express.Router();
var url 		= require('url');
var convert  	= require('../DisiFhirConverter/workers/uniboworker');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'DISI FHIR-HL7 Converter' });
});

router.get('/cvthtf', function(request, response, next) {
  console.log("Request method=" + request.method + " request.url=" + request.url);
  response.write("cvthtf\n");
  next();
});

//curl -H "Content-Type: application/json" -X POST -d "{\"template\": \"50\", \"data\" : \"100\" }" http://localhost:3000/hl7tofhir
router.post("/hl7tofhir", function(req,res,next){
	var path = url.parse(req.url).pathname;
	console.log( "POST path=" + path + " args=" + Object.keys( req.body  )  );
	//console.log( "POST path=" + path + " template=" + req.body.a   );
	//console.log( "POST path=" + path + " hl7msg="   + req.body.b   );
	var buffTemplate = Buffer.from( req.body.a, 'base64' );
 	var template     = buffTemplate.toString();
	var buffMsgHl7 	 = Buffer.from( req.body.b, 'base64' );
 	var msgHl7       = buffMsgHl7.toString();
// 	console.log( "POST template=" + template  );
    console.log( "POST msgHl7="   + msgHl7  );
 	
  	
 	var result = convert(template,msgHl7,res);
 	
// 	res.write( result  ); 

});



module.exports = router;