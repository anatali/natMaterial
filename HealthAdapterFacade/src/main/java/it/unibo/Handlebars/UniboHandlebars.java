package it.unibo.Handlebars;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

public class UniboHandlebars {
	//From https://www.baeldung.com/handlebars	
	private static Handlebars handlebars = new Handlebars();

	public static void setTemplateLoader(String path, String suffix) {	//"/handlebars", ".html"
		TemplateLoader loader = new ClassPathTemplateLoader(path, suffix);
		handlebars            = new Handlebars(loader);
	}
	
	
	public static String noTemplateFile_compilesInline() {
		try {
			Template template     = handlebars.compileInline("Inline compiled: {{this}}!");
			String templateString = template.apply("Disi Test on Handlebars");
			System.out.println(templateString);
			return templateString;
		} catch (IOException e) {
			String s = "noTemplateFile_compilesInline error " + e.getMessage();
			System.out.println(s ); 
			return s; 
		}
	}
	
	public static String compilesInline_parameterObject() {
		try {
			Template template = handlebars.compileInline("Hello Inline compiled from a Person: {{name}}!");
			Person person = new Person();
			person.setName( "aDisiPerson" );
			String templateString = template.apply(person);
			System.out.println(templateString);
			return templateString;
		} catch (IOException e) {
			String s = "compilesInline_parameterObject error " + e.getMessage();
			System.out.println(s ); 
			return s; 
		}
		
	}
	
	
	public static String templateFile_parameterInMap() {
		try {
			Template template     = handlebars.compile( "templatesHbs/cvt" );	//.hbs 
			Map<String, String> parameterMap = new HashMap<>();
			parameterMap.put("foo", "Disi Test on Handlebars with Map and cvt.bls");
			String templateString = template.apply(parameterMap);
			System.out.println(templateString);
			return templateString;
		} catch (IOException e) {
			String s = "templateFile_parameterInMap error " + e.getMessage();
			System.out.println(s ); 
			return s; 
		}		
	}
	
	public static String usingWith() {
		try {
			Template template     = handlebars.compile( "templatesHbs/useWith" );	//.hbs 
			Person person = new Person();
			person.setName( "aDisiPerson" );
			person.getAddress().setStreet("SenzaNome");
			String templateString = template.apply(person);
			System.out.println(templateString);
			return templateString;
		} catch (IOException e) {
			String s = "usingWith error " + e.getMessage();
			System.out.println(s ); 
			return s; 
		}				
	}
	
	
	public static String usingEach() {	
		try {
			Template template     = handlebars.compile( "templatesHbs/useEach" );	//.hbs 
			Person person = new Person();
			person.setName( "aPersonWithFriends" );
			Person friend1 = new Person();friend1.setName("Bob");
			Person friend2 = new Person();friend2.setName("Alice");
			person.getFriends().add(friend1);
			person.getFriends().add(friend2);
 			String templateString = template.apply(person);
			return templateString;
		} catch (IOException e) {
			String s = "usingWith error " + e.getMessage();
			System.out.println(s ); 
			return s; 
		}				
		
	}
	
	public static String templateReference() {
		try {
			Template template     = handlebars.compile( "templatesHbs/page" );	//include  header
			Person person         = new Person();
			person.setName( "somePerson" );
 			String templateString = template.apply(person);
			return templateString;
		} catch (IOException e) {
			String s = "templateReference error " + e.getMessage();
			System.out.println(s ); 
			return s; 
		}				
		
	}
	

}
