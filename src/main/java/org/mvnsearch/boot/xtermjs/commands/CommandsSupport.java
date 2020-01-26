package org.mvnsearch.boot.xtermjs.commands;

import java.util.Collection;

/**
 * Commands support
 *
 * @author linux_china
 */
public interface CommandsSupport {

	default String linesToString(Collection<String> lines) {
		return String.join("\n", lines);
	}

	default String formatClassName(String classFullName) {
		if (classFullName.contains("java.lang.")) {
			return classFullName.replaceAll("java.lang.([A-Z]\\w*)", "$1");
		}
		else {
			return classFullName;
		}
	}

}
