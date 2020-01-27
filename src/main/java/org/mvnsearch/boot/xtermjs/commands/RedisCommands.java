package org.mvnsearch.boot.xtermjs.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

/**
 * Redis commands
 *
 * @author linux_china
 */
@ShellComponent
public class RedisCommands {

	@Autowired(required = false)
	private RedisTemplate redisTemplate;

	@ShellMethod(value = "Redis Get")
	public String redisGet(@ShellOption(help = "Key", defaultValue = "") String key) {
		return "redis value";
	}

	@ShellMethod("Redis Set")
	public String redisSet(@ShellOption(help = "Redis Key", defaultValue = "") String key,
			@ShellOption(help = "Value", defaultValue = "") String value) {
		return "";
	}

	@ShellMethodAvailability
	public Availability availabilityOnWeekdays() {
		return redisTemplate != null ? Availability.available()
				: Availability.unavailable("RedisTemplate bean not found!");
	}

}
