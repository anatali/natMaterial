var handlebars = require('handlebars'),
  fs = require('fs');

var data = {
  title: 'practical node.js',
  author: '@azat_co',
  tags: ['express', 'node', 'javascript']
}
data.body 			= process.argv[2];		//from run command
data.name 			= "alice";
data.twitter		= "notwitter"

fs.readFile('template.hbs', 'utf-8', function(error, source){
  handlebars.registerHelper('custom_title', function(title){
    var words = title.split(' ');
    for (var i = 0; i < words.length; i++) {
      if (words[i].length > 4) {
        words[i] = words[i][0].toUpperCase() + words[i].substr(1);
      }
    }
    title = words.join(' ');
    return title;
  })//helper

  var template = handlebars.compile(source);
  var html     = template(data);
  console.log(html)
});

//USAGE: node ex0.js myemail
