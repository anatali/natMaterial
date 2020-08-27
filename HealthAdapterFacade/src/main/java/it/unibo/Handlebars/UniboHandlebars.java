package it.unibo.Handlebars;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

public class UniboHandlebars {
	//From https://www.baeldung.com/handlebars	
	private static Handlebars handlebars = new Handlebars();

	
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

}
