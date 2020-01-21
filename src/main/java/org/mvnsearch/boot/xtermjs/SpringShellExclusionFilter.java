package org.mvnsearch.boot.xtermjs;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Spring Shell exclusion filter
 *
 * @author linux_china
 */
public class SpringShellExclusionFilter implements AutoConfigurationImportFilter {

	private static final Set<String> SHOULD_SKIP = new HashSet<>(
			Arrays.asList("org.springframework.shell.jline.JLineShellAutoConfiguration",
					"org.springframework.shell.standard.commands.StandardCommandsAutoConfiguration"));

	@Override
	public boolean[] match(String[] classNames, AutoConfigurationMetadata autoConfigurationMetadata) {
		boolean[] matches = new boolean[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			matches[i] = !SHOULD_SKIP.contains(classNames[i]);
		}
		return matches;
	}

}
