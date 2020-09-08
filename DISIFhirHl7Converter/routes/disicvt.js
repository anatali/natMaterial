var express 	= require('express');
var router 		= express.Router();
var url 		= require('url');
var worker  	= require('../DisiFhirConverter/workers/uniboworker');

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
	console.log( "disicvt POST path=" + path + " args=" + Object.keys( req.body  )  );
	//console.log( "disicvt POST path=" + path + " template=" + req.body.templateb64   );
	//console.log( "disicvt POST path=" + path + " hl7msg="   + req.body.hl7b64   );
	var buffTemplate = Buffer.from( req.body.templateb64, 'base64' );
 	var template     = buffTemplate.toString();
	var buffMsgHl7 	 = Buffer.from( req.body.hl7b64, 'base64' );
 	var msgHl7       = buffMsgHl7.toString();
// 	console.log( "disicvt POST template=" + template  );
    console.log( "disicvt POST /hl7tofhir msgHl7="   + msgHl7  );
 	var result = worker.convert(template,msgHl7,res);
});



module.exports = router;