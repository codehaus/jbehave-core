package org.jbehave.core.xwiki;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.rendering.renderer.PrintRenderer;
import org.xwiki.rendering.renderer.PrintRendererFactory;
import org.xwiki.rendering.renderer.printer.WikiPrinter;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.syntax.SyntaxType;

@Component
@Named("jbehave/3.0")
@Singleton
public class JBehavePrintRendererFactory implements PrintRendererFactory {

	@Inject
	@Named("jbehave/3.0")	
	private PrintRenderer renderer;
	
	public Syntax getSyntax() {
		return new Syntax(new SyntaxType("jbehave", "JBehave"), "3.0");
	}

	public PrintRenderer createRenderer(WikiPrinter printer) {
		renderer.setPrinter(printer);
		return renderer;
	}
	
}
