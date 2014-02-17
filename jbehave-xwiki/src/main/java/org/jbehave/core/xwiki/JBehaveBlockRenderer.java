package org.jbehave.core.xwiki;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.rendering.internal.renderer.AbstractBlockRenderer;
import org.xwiki.rendering.renderer.PrintRendererFactory;

@Component
@Named("jbehave/3.0")
@Singleton
public class JBehaveBlockRenderer extends AbstractBlockRenderer {

	@Inject
	@Named("jbehave/3.0")
	private PrintRendererFactory rendererFactory;

	@Override
	protected PrintRendererFactory getPrintRendererFactory() {
		return this.rendererFactory;
	}
}
