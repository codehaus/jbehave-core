package org.jbehave.core.xwiki;

import org.jbehave.core.steps.InjectableStepsFactory;
import org.xwiki.component.annotation.Role;

@Role
public interface InjectableStepsFactoryProvider {

	InjectableStepsFactory getStepsFactory();
	
}
