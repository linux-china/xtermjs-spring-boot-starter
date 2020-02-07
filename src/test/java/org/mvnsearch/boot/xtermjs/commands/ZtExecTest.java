package org.mvnsearch.boot.xtermjs.commands;

import org.junit.jupiter.api.Test;
import org.zeroturnaround.exec.ProcessExecutor;

/**
 * ZT Processor test
 *
 * @author linux_china
 */
public class ZtExecTest {

	@Test
	public void testShellFunction() throws Exception {
		String output = new ProcessExecutor().command("ls", "/").readOutput(true).execute().outputUTF8();
		System.out.println(output);
	}

}
