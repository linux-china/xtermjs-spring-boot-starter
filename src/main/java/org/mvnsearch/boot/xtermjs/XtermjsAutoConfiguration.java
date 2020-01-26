package org.mvnsearch.boot.xtermjs;

import org.mvnsearch.boot.xtermjs.commands.*;
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
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	public Help help(List<ParameterResolver> parameterResolvers) {
		return new Help(parameterResolvers);
	}

	@Bean
	public SystemCommands systemCommands() {
		return new SystemCommands();
	}

	@Bean
	public JvmCommands jvmCommands() {
		return new JvmCommands();
	}

	@Bean
	public SpringCommands springBootStandardCommands() {
		return new SpringCommands();
	}

	@Bean
	public CurlCommand curlCommand() {
		return new CurlCommand();
	}

	@Bean
	public SpelCommand spelCommand() {
		return new SpelCommand();
	}

	@Bean
	public XtermRSocketController xtermRSocketController() {
		return new XtermRSocketController();
	}

}
