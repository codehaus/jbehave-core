package org.jbehave.scenario.steps;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsArray.array;
import static org.jbehave.Ensure.ensureThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.jbehave.scenario.definition.ScenarioDefinition;
import org.jbehave.scenario.definition.StoryDefinition;
import org.jbehave.scenario.steps.StepCreator.Stage;
import org.junit.Test;

public class UnmatchedToPendingStepCreatorBehaviour {

    private Map<String, String> tableRow = new HashMap<String, String>();

    @Test
    public void shouldMatchUpStepsAndScenarioDefinitionToCreateExecutableSteps() {
        // Given
        UnmatchedToPendingStepCreator creator = new UnmatchedToPendingStepCreator();

        CandidateStep candidate = mock(CandidateStep.class);
        CandidateSteps steps = mock(Steps.class);
        Step executableStep = mock(Step.class);

        when(candidate.matches("my step")).thenReturn(true);
        when(candidate.createFrom(tableRow, "my step")).thenReturn(executableStep);
        when(steps.getSteps()).thenReturn(new CandidateStep[] { candidate });

        // When
        Step[] executableSteps = creator
                .createStepsFrom(new ScenarioDefinition("", asList("my step")), tableRow, steps);

        // Then
        ensureThat(executableSteps.length, equalTo(1));
        ensureThat(executableSteps[0], equalTo(executableStep));
    }

    @Test
    public void shouldProvidePendingStepsForAnyStepsWhichAreNotAvailable() {
        // Given
        UnmatchedToPendingStepCreator creator = new UnmatchedToPendingStepCreator();

        CandidateStep candidate = mock(CandidateStep.class);
        CandidateSteps steps = mock(Steps.class);

        when(candidate.matches("my step")).thenReturn(false);
        when(steps.getSteps()).thenReturn(new CandidateStep[] { candidate });

        // When
        Step[] executableSteps = creator
                .createStepsFrom(new ScenarioDefinition("", asList("my step")), tableRow, steps);
        // Then
        ensureThat(executableSteps.length, equalTo(1));
        StepResult result = executableSteps[0].perform();
        ensureThat(result.getThrowable().getMessage(), equalTo("Pending: my step"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPrependBeforeScenarioAndAppendAfterScenarioAnnotatedSteps() {
        // Given some steps classes which run different steps before and after
        // scenarios
        CandidateSteps steps1 = mock(Steps.class);
        CandidateSteps steps2 = mock(Steps.class);
        Step stepBefore1 = mock(Step.class);
        Step stepBefore2 = mock(Step.class);
        Step stepAfter1 = mock(Step.class);
        Step stepAfter2 = mock(Step.class);

        when(steps1.runBeforeScenario()).thenReturn(asList(stepBefore1));
        when(steps2.runBeforeScenario()).thenReturn(asList(stepBefore2));
        when(steps1.runAfterScenario()).thenReturn(asList(stepAfter1));
        when(steps2.runAfterScenario()).thenReturn(asList(stepAfter2));

        // And which have a 'normal' step that matches our scenario
        CandidateStep candidate = mock(CandidateStep.class);
        Step normalStep = mock(Step.class);

        when(candidate.matches("my step")).thenReturn(true);
        when(candidate.createFrom(tableRow, "my step")).thenReturn(normalStep);
        when(steps1.getSteps()).thenReturn(new CandidateStep[] { candidate });
        when(steps2.getSteps()).thenReturn(new CandidateStep[] {});

        // When we create the series of steps for the scenario
        UnmatchedToPendingStepCreator creator = new UnmatchedToPendingStepCreator();
        Step[] executableSteps = creator.createStepsFrom(new ScenarioDefinition("", asList("my step")), tableRow,
                steps1, steps2);

        // Then all before and after steps should be added
        ensureThat(executableSteps, array(equalTo(stepBefore2), equalTo(stepBefore1), equalTo(normalStep),
                equalTo(stepAfter1), equalTo(stepAfter2)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnBeforeAndAfterStoryAnnotatedSteps() {
        // Given some steps classes which run different steps before and after
        // story
        CandidateSteps steps1 = mock(Steps.class);
        CandidateSteps steps2 = mock(Steps.class);
        Step stepBefore1 = mock(Step.class);
        Step stepBefore2 = mock(Step.class);
        Step stepAfter1 = mock(Step.class);
        Step stepAfter2 = mock(Step.class);

        when(steps1.runBeforeStory()).thenReturn(asList(stepBefore1));
        when(steps2.runBeforeStory()).thenReturn(asList(stepBefore2));
        when(steps1.runAfterStory()).thenReturn(asList(stepAfter1));
        when(steps2.runAfterStory()).thenReturn(asList(stepAfter2));

        // When we create the series of steps for the scenario
        UnmatchedToPendingStepCreator creator = new UnmatchedToPendingStepCreator();
        Step[] beforeSteps = creator.createStepsFrom(new StoryDefinition(new ScenarioDefinition("")), Stage.BEFORE,
                steps1, steps2);
        Step[] afterSteps = creator.createStepsFrom(new StoryDefinition(new ScenarioDefinition("")), Stage.AFTER,
                steps1, steps2);

        // Then all before and after steps should be added
        ensureThat(beforeSteps, array(equalTo(stepBefore1), equalTo(stepBefore2)));
        ensureThat(afterSteps, array(equalTo(stepAfter1), equalTo(stepAfter2)));
    }

}
