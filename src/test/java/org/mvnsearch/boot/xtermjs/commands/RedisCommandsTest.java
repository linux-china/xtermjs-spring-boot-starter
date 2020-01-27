package org.mvnsearch.boot.xtermjs.commands;

import org.junit.jupiter.api.Test;
import org.mvnsearch.boot.xtermjs.demo.DemoAppBaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;

/**
 * redis commands test
 *
 * @author linux_china
 */
public class RedisCommandsTest extends DemoAppBaseTestCase {

	@Autowired
	private Shell shell;

	@Test
	public void testGet() {
        Object result = shell.evaluate(() -> "redis-get name");
 		System.out.println(result);
	}

}
