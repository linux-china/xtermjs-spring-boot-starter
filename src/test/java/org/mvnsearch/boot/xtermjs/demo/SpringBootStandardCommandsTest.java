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
	public void testBeansCommand() {
		Object result = shell.evaluate(() -> "beans");
		System.out.println(result);
	}

	@Test
	public void testBeanCommand() {
		Object result = shell.evaluate(() -> "bean help");
		System.out.println(result);
	}

	@Test
	public void testEnv() {
		Object result = shell.evaluate(() -> "env home");
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
