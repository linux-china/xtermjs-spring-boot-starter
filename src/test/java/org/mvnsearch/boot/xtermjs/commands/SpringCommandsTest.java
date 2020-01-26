package org.mvnsearch.boot.xtermjs.commands;

import org.junit.jupiter.api.Test;
import org.mvnsearch.boot.xtermjs.demo.DemoAppBaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;
import reactor.core.publisher.Mono;

/**
 * Spring commands test
 *
 * @author linux_china
 */
public class SpringCommandsTest extends DemoAppBaseTestCase {

	@Autowired
	private Shell shell;

	@Test
	public void testAppCommand() {
		Object result = shell.evaluate(() -> "app");
		System.out.println(result);
	}

	@Test
	public void testOptions() {
		Object result = shell.evaluate(() -> "options");
		System.out.println(result);
	}

	@Test
	public void testBeansCommand() {
		Object result = shell.evaluate(() -> "beans");
		System.out.println(result);
	}

	@Test
	public void testBeanCommand() {
		Object result = shell.evaluate(() -> "bean userService");
		System.out.println(result);
	}

	@Test
	public void testEnv() {
		Object result = shell.evaluate(() -> "env");
		System.out.println(result);
	}

	@Test
	public void testHealth() {
		Object result = shell.evaluate(() -> "health");
		System.out.println(result);
	}

	@Test
	public void testMetrics() {
		Object result = shell.evaluate(() -> "metrics jvm.memory.used");
		System.out.println(result);
	}

	@Test
	public void testActuator() {
		Object result = shell.evaluate(() -> "actuator");
		System.out.println(((Mono) result).block());
	}

}
