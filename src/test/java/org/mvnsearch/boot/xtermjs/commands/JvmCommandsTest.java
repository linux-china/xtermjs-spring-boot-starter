package org.mvnsearch.boot.xtermjs.commands;

import org.junit.jupiter.api.Test;

/**
 * jvm commands test
 *
 * @author linux_china
 */
public class JvmCommandsTest {

	private JvmCommands jvmCommands = new JvmCommands();

	@Test
	public void testThreads() {
		System.out.println(jvmCommands.classpath());
	}

}
