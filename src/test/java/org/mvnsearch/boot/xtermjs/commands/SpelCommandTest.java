package org.mvnsearch.boot.xtermjs.commands;

import org.junit.jupiter.api.Test;
import org.mvnsearch.boot.xtermjs.demo.DemoAppBaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * spel command test
 *
 * @author linux_china
 */
public class SpelCommandTest extends DemoAppBaseTestCase {

	@Autowired
	private SpelCommand spelCommand;

	@Test
	public void testSpelCommand() {
		Object result = spelCommand.execute("Hello #{ systemProperties['user.home'] }");
		System.out.println(result);
	}

}
