package org.mvnsearch.boot.xtermjs;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;

import java.util.Collection;

/**
 * Xterm command handler: execute the commands from xterm.js
 *
 * @author linux_china
 */
public class XtermCommandHandler {

	@Autowired
	private Shell shell;

	public String executeCommand(String commandLine) {
		Object result = this.shell.evaluate(() -> commandLine);
		String textOutput;
		if (result instanceof Exception) {
			textOutput = new AttributedString(result.toString(),
					AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi();
		}
		else if (result instanceof AttributedString) {
			textOutput = ((AttributedString) result).toAnsi();
		}
		else if (result instanceof Collection) {
			textOutput = String.join("\r\n", (Collection) result);
		}
		else {
			textOutput = result.toString();
		}
		// text format for Xterm
		if (!textOutput.contains("\r\n") && textOutput.contains("\n")) {
			return textOutput.replaceAll("\n", "\r\n");
		}
		else {
			return textOutput;
		}
	}

}
