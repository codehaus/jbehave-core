package org.jbehave.core.xwiki;

import org.jbehave.core.configuration.Configuration;
import org.xwiki.component.annotation.Role;

@Role
public interface ConfigurationProvider {

	Configuration getConfiguration();
	
}
