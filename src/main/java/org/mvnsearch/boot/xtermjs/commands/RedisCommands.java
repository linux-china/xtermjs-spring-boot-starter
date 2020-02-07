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
@ShellComponent("redis: execute redis commands")
public class RedisCommands {

	@Autowired(required = false)
	private RedisTemplate redisTemplate;

	@ShellMethod(value = "Redis Get")
	public Object redisGet(@ShellOption(help = "Key", defaultValue = "") String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@ShellMethod("Redis Set")
	public String redisSet(@ShellOption(help = "Redis Key", defaultValue = "") String key,
			@ShellOption(help = "Value", defaultValue = "") String value) {
		redisTemplate.opsForValue().set(key, value);
		return "OK";
	}

	@ShellMethodAvailability
	public Availability availabilityOnWeekdays() {
		return redisTemplate != null ? Availability.available()
				: Availability.unavailable("RedisTemplate bean not found!");
	}

}
