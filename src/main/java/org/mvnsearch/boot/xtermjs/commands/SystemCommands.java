package org.mvnsearch.boot.xtermjs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * System Commands
 *
 * @author linux_china
 */
@ShellComponent
public class SystemCommands implements CommandsSupport {

	@ShellMethod("Date command")
	public String date() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		return dateFormat.format(new Date());
	}

}
