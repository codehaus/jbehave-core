package org.jbehave.core.xwiki;

import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.syntax.SyntaxType;

public class JBehaveSyntax extends Syntax {

	public static final Syntax JBEHAVE_3_0 = new JBehaveSyntax("3.0");

	public JBehaveSyntax(String version) {
		super(new SyntaxType("jbehave", "JBehave"), version);
	}

}
