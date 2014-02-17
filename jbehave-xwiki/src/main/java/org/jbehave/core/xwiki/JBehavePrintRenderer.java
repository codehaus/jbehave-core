package org.jbehave.core.xwiki;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.rendering.listener.chaining.ListenerChain;
import org.xwiki.rendering.renderer.AbstractChainingPrintRenderer;

@Component
@Named("jbehave/3.0")
@Singleton
public class JBehavePrintRenderer extends AbstractChainingPrintRenderer
		implements Initializable {

	public void initialize() throws InitializationException {
		ListenerChain chain = new ListenerChain();
		setListenerChain(chain);

		// Construct the listener chain in the right order. Listeners early in
		// the chain are called before listeners
		// placed later in the chain.
		chain.addListener(this);
	}

}
