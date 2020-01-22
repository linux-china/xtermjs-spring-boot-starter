package org.mvnsearch.boot.xtermjs.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Shell;

/**
 * spring boot standard commands test
 *
 * @author linux_china
 */
@SpringBootTest
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

}
