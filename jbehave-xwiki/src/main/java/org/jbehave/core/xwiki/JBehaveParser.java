package org.jbehave.core.xwiki;

import static java.util.Arrays.asList;
import static org.jbehave.core.xwiki.JBehaveSyntax.JBEHAVE_3_0;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.GivenStories;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Narrative;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.Step;
import org.jbehave.core.steps.StepCollector;
import org.jbehave.core.steps.StepCreator.PendingStep;
import org.xwiki.component.annotation.Component;
import org.xwiki.rendering.block.AbstractBlock;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.rendering.listener.Listener;
import org.xwiki.rendering.parser.ParseException;
import org.xwiki.rendering.parser.Parser;
import org.xwiki.rendering.renderer.PrintRenderer;
import org.xwiki.rendering.syntax.Syntax;

@Component
@Named("jbehave/3.0")
@Singleton
public class JBehaveParser implements Parser {

	@Inject
	private ConfigurationProvider configurationProvider;

	@Inject
	private InjectableStepsFactoryProvider stepsFactoryProvider;

	public Syntax getSyntax() {
		return JBEHAVE_3_0;
	}

	public XDOM parse(Reader source) throws ParseException {
		try {
			Story story = configurationProvider.getConfiguration()
					.storyParser().parseStory(IOUtils.toString(source));
			List<CandidateSteps> candidateSteps = stepsFactoryProvider
					.getStepsFactory().createCandidateSteps();
			Keywords keywords = configurationProvider.getConfiguration()
					.keywords();

			Block storyBlock = new StoryBlock(story);
			addMeta(storyBlock, keywords, story.getMeta());
			addNarrative(storyBlock, keywords, story.getNarrative());
			addGivenStories(storyBlock, keywords, story.getGivenStories());
			for (Scenario scenario : story.getScenarios()) {
				ScenarioBlock scenarioBlock = new ScenarioBlock(
						keywords, scenario);
				addMeta(scenarioBlock, keywords, scenario.getMeta());
				addGivenStories(scenarioBlock, keywords,
						scenario.getGivenStories());
				addScenarioSteps(scenarioBlock, keywords, candidateSteps,
						scenario);
				addExamplesTable(scenarioBlock, keywords,
						scenario.getExamplesTable());
				storyBlock.addChild(scenarioBlock);
			}
			return new XDOM(asList(storyBlock));
		} catch (IOException e) {
			throw new ParseException("Failed to parse source " + source, e);
		}
	}

	private void addMeta(Block block, Keywords keywords, Meta meta) {
		if (!meta.isEmpty()) {
			block.addChild(new MetaBlock(meta, keywords));
		}
	}

	private void addNarrative(Block block, Keywords keywords,
			Narrative narrative) {
		if (!narrative.isEmpty()) {
			block.addChild(new NarrativeBlock(narrative, keywords));
		}
	}

	private void addGivenStories(Block block, Keywords keywords,
			GivenStories givenStories) {
		if (!givenStories.asString().isEmpty()) {
			block.addChild(new GivenStoriesBlock(givenStories,
					keywords));
		}
	}

	private void addExamplesTable(Block block, Keywords keywords,
			ExamplesTable examplesTable) {
		if (!examplesTable.asString().isEmpty()) {
			block.addChild(new ExampleTableBlock(examplesTable, keywords));
		}
	}

	private void addScenarioSteps(Block block, Keywords keywords,
			List<CandidateSteps> candidateSteps, Scenario scenario) {
		for (Step step : scenarioSteps(scenario, candidateSteps)) {
			block.addChild(new StepBlock(step, keywords));
		}
	}

	private List<Step> scenarioSteps(Scenario scenario,
			List<CandidateSteps> candidateSteps) {
		StepCollector stepCollector = configurationProvider.getConfiguration()
				.stepCollector();
		List<Step> scenarioSteps = stepCollector.collectScenarioSteps(
				candidateSteps, scenario, new HashMap<String, String>());
		return scenarioSteps;
	}

	public abstract class JBehaveBlock extends AbstractBlock {

		@Override
		public void traverse(Listener listener) {
			printTo(listener);
			super.traverse(listener);
		}

		protected void printTo(Listener listener) {
			if (listener instanceof PrintRenderer) {
				((PrintRenderer) listener).getPrinter().println(toString());
			}
		}
	}

	public class StoryBlock extends JBehaveBlock {

		private Story story;

		public StoryBlock(Story story) {
			this.story = story;
		}

		public String asString() {
			return story.getDescription().asString();
		}

		@Override
		public String toString() {
			return asString();
		}
	}

	public class NarrativeBlock extends JBehaveBlock {

		private Narrative narrative;
		private Keywords keywords;

		public NarrativeBlock(Narrative narrative, Keywords keywords) {
			this.narrative = narrative;
			this.keywords = keywords;
		}

		public String asString() {
			return narrative.asString(keywords);
		}

		@Override
		public String toString() {
			return keywords.narrative() + asString();
		}
	}

	public class MetaBlock extends JBehaveBlock {

		private Meta meta;
		private Keywords keywords;

		public MetaBlock(Meta meta, Keywords keywords) {
			this.keywords = keywords;
			this.meta = meta;
		}

		public String asString() {
			return meta.asString(keywords);
		}

		@Override
		public String toString() {
			return keywords.meta() + asString();
		}
	}

	public class GivenStoriesBlock extends JBehaveBlock {

		private GivenStories givenStories;
		private Keywords keywords;

		public GivenStoriesBlock(GivenStories givenStories, Keywords keywords) {
			this.givenStories = givenStories;
			this.keywords = keywords;
		}

		public String asString() {
			return givenStories.asString();
		}

		@Override
		public String toString() {
			return keywords.givenStories() + asString();
		}
	}

	public class ScenarioBlock extends JBehaveBlock {

		private Keywords keywords;
		private Scenario scenario;

		public ScenarioBlock(Keywords keywords, Scenario scenario) {
			this.keywords = keywords;
			this.scenario = scenario;
		}

		public String asString() {
			return scenario.getTitle();
		}

		@Override
		public String toString() {
			return keywords.scenario()+asString();
		}

	}

	public class StepBlock extends JBehaveBlock {

		private Step step;
		private Keywords keywords;

		public StepBlock(Step step, Keywords keywords) {
			this.step = step;
			this.keywords = keywords;
		}

		public Type getType() {
			return step.getClass();
		}

		public String asString() {
			String asString = step.asString(keywords);
			if (step instanceof PendingStep) {
				asString = asString + " (PENDING)";
			}
			return asString;
		}

		@Override
		public String toString() {
			return asString();
		}
	}

	public class ExampleTableBlock extends JBehaveBlock {

		private ExamplesTable table;
		private Keywords keywords;

		public ExampleTableBlock(ExamplesTable table, Keywords keywords) {
			this.table = table;
			this.keywords = keywords;
		}

		public String asString() {
			return table.asString();
		}

		@Override
		public String toString() {
			return keywords.examplesTable() + "\n" + asString();
		}
	}

}
