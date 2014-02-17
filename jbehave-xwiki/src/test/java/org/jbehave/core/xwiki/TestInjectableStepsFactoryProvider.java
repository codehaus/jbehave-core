package org.jbehave.core.xwiki;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.xwiki.JBehaveRenderingBehaviour.MySteps;
import org.xwiki.component.annotation.Component;

@Component
@Singleton
public class TestInjectableStepsFactoryProvider implements InjectableStepsFactoryProvider {

	@Inject
	private ConfigurationProvider configurationProvider;
	
	public InjectableStepsFactory getStepsFactory() {
		return new InstanceStepsFactory(configurationProvider.getConfiguration(), new MySteps());
	}

}
