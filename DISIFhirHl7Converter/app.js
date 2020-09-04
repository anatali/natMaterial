var createError 	= require('http-errors');
var express 		= require('express');
var path 		 	= require('path');
var cookieParser 	= require('cookie-parser');
var logger 			= require('morgan');
//var url 			= require('url');


var indexRouter 	= require('./routes/index');
//var usersRouter 	= require('./routes/users');
var disicvtRouter 	= require('./routes/disicvt');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/',          disicvtRouter);
app.use('/hl7tofhir', disicvtRouter);
//app.use('/users',   usersRouter);


/*
//curl -H "Content-Type: application/json" -X PUT -d "{\"template\": \"50\", \"data\" : \"100\" }" http://localhost:3000/hl7tofhir
app.put("/hl7tofhir", function(req,res,next){
	var path = url.parse(req.url).pathname;
	console.log( "PUT path=" + path + " template=" + req.body.template + " data=" + req.body.data);
	res.write("hl7tofhir\n");
	next();
});
*/

//no next => terminate;
app.use(function( request, response ) { response.end( ); });

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
