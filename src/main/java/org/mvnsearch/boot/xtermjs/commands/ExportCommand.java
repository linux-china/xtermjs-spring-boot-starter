package org.mvnsearch.boot.xtermjs.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

/**
 * export command
 *
 * @author linux_china
 */
public class ExportCommand implements CustomizedCommand {

	@Override
	public String[] getNames() {
		return new String[] { "export" };
	}

	@Override
	public @Nullable Object execute(@NotNull String command, @Nullable String arguments) throws Exception {
		if (arguments == null || !arguments.contains("=")) {
			return new Exception("Please use export name=value!");
		}
		return Mono.deferWithContext(context -> {
			String[] parts = arguments.split("=", 2);
			String name = parts[0].trim();
			String value = parts[1].trim();
			if (value.startsWith("\"")) {
				value = value.substring(1, value.length() - 1);
			}
			context.put(name, value);
			return Mono.just("$" + name + ":" + value);
		});
	}

}
