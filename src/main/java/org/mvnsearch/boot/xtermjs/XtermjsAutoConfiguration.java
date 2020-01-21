package org.mvnsearch.boot.xtermjs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.standard.commands.Help;

import java.util.List;

/**
 * Xterm.js auto configuration
 *
 * @author linux_china
 */
@Configuration
public class XtermjsAutoConfiguration {

	private Logger log = LoggerFactory.getLogger(XtermjsAutoConfiguration.class);

	@Bean
	public Help help(List<ParameterResolver> parameterResolvers) {
		return new Help(parameterResolvers);
	}

	@Bean
	public SpringBootStandardCommands springBootStandardCommands() {
		return new SpringBootStandardCommands();
	}

	@Bean
	public XtermRSocketController xtermRSocketController() {
		return new XtermRSocketController();
	}

}
