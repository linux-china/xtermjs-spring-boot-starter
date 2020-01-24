package org.mvnsearch.boot.xtermjs.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Shell;
import reactor.core.publisher.Mono;

/**
 * spring boot standard commands test
 *
 * @author linux_china
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringBootStandardCommandsTest {

	@Autowired
	private Shell shell;

	@Test
	public void testSystemCommand() {
		Object result = shell.evaluate(() -> "system");
		System.out.println(result);
	}

	@Test
	public void testClasspath() {
		Object result = shell.evaluate(() -> "classpath");
		System.out.println(result);
	}

	@Test
	public void testOptions() {
		Object result = shell.evaluate(() -> "options");
		System.out.println(result);
	}

	@Test
	public void testThreads() {
		Object result = shell.evaluate(() -> "threads");
		System.out.println(result);
	}

	@Test
	public void testBeansCommand() {
		Object result = shell.evaluate(() -> "beans");
		System.out.println(result);
	}

	@Test
	public void testSpelCommand() {
		Object result = shell.evaluate(() -> "spel @userService.findRealName(1)");
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
