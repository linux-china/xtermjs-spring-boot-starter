package org.mvnsearch.boot.xtermjs.demo;

import org.junit.jupiter.api.Test;
import org.mvnsearch.boot.xtermjs.SpelCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * spel command test
 *
 * @author linux_china
 */
@SpringBootTest
public class SpelCommandTest {

	@Autowired
	private SpelCommand spelCommand;

	@Test
	public void testSpelCommand() {
		Object result = spelCommand.execute("#{ systemProperties['user.home'] }");
		System.out.println(result);
	}

}
