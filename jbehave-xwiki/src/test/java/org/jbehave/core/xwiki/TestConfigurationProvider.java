package org.jbehave.core.xwiki;

import javax.inject.Singleton;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.xwiki.component.annotation.Component;

@Component
@Singleton
public class TestConfigurationProvider implements ConfigurationProvider {

	public Configuration getConfiguration() {
		return new MostUsefulConfiguration();
	}

}
