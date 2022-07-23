package org.mvnsearch.boot.xtermjs;

import org.mvnsearch.boot.xtermjs.commands.*;
import org.mvnsearch.boot.xtermjs.commands.sql.DatabaseCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Xterm.js auto configuration
 *
 * @author linux_china
 */
@Configuration
@Order()
public class XtermjsAutoConfiguration {

	private final Logger log = LoggerFactory.getLogger(XtermjsAutoConfiguration.class);

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
	public ExportCommand exportCommand() {
		return new ExportCommand();
	}

	@Bean
	@ConditionalOnClass(RedisTemplate.class)
	public RedisCommands redisCommands() {
		return new RedisCommands();
	}

	@Bean
	@ConditionalOnClass(JdbcTemplate.class)
	public DatabaseCommands databaseCommands() {
		return new DatabaseCommands();
	}

	@Bean
	public XtermRSocketController xtermRSocketController() {
		return new XtermRSocketController();
	}

}
