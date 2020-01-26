package org.mvnsearch.boot.xtermjs.commands;

import org.junit.jupiter.api.Test;

/**
 * System commands test
 *
 * @author linux_china
 */
public class SystemCommandsTest {

	private SystemCommands systemCommands = new SystemCommands();

	@Test
	public void testDate() {
		System.out.println(systemCommands.date());
	}

}
