package org.jbehave.core.xwiki;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.steps.Steps;
import org.junit.Test;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentRepositoryException;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.rendering.parser.ParseException;
import org.xwiki.rendering.parser.Parser;
import org.xwiki.rendering.renderer.BlockRenderer;
import org.xwiki.rendering.renderer.printer.DefaultWikiPrinter;

public class JBehaveRenderingBehaviour {

	@Test
	public void canRenderStory() throws ParseException,
			ComponentLookupException, ComponentRepositoryException, IOException {
		EmbeddableComponentManager manager = new EmbeddableComponentManager();
		manager.initialize(this.getClass().getClassLoader());

		String syntax = JBehaveSyntax.JBEHAVE_3_0.toIdString();
		Parser parser = manager.getInstance(Parser.class, syntax);
		String name = "input.story";
		XDOM xdom = parser.parse(new StringReader(readStory(name)));		
		BlockRenderer renderer = manager.getInstance(BlockRenderer.class, syntax);
		DefaultWikiPrinter printer = new DefaultWikiPrinter();
		renderer.render(xdom.getRoot(), printer);
		assertThatRenderedStoryIs("rendered.story", printer.getBuffer());
	}

	private void assertThatRenderedStoryIs(String name, StringBuffer buffer) throws IOException {
		assertEquals(readStory(name).replace("\r\n", "\n"), buffer.toString().trim());
	}

	private String readStory(String name) throws IOException {
		return IOUtils.toString(this.getClass().getResourceAsStream("/stories/"+name)).trim();
	}

	public static class MySteps extends Steps {

		@Given("a param $s")
		public void given(String s) {
		}

		@When("another param $d")
		public void when(double d) {
		}

		@Then("the result is $i")
		public void then(int i) {
		}

	}
}
