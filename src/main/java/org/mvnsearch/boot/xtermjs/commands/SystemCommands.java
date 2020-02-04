package org.mvnsearch.boot.xtermjs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.File;
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

	@ShellMethod("cd command")
	public String cd(@ShellOption(help = "path name", defaultValue = "") String path) throws Exception {
		File dest;
		if (path.isEmpty()) {
			dest = new File(".");
		}
		else if (path.startsWith("/")) {
			dest = new File(path);
		}
		else {
			File workDir = new File(".");
			dest = new File(workDir, path);
		}
		String absolutePath = dest.getAbsolutePath();
		if (absolutePath.endsWith("/.")) {
			absolutePath = absolutePath.substring(0, absolutePath.length() - 2);
		}
		if (!dest.exists()) {
			throw new Exception("Directory not existed: " + absolutePath);
		}
		else if (!dest.isDirectory()) {
			throw new Exception("Not diretory: " + absolutePath);
		}
		else {
			return "$path:" + absolutePath;
		}
	}

}
