package org.mvnsearch.boot.xtermjs.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * customized command
 *
 * @author linux_china
 */
public interface CustomizedCommand {

	String[] getNames();

	@Nullable
	Object execute(@NotNull String command, @Nullable String arguments) throws Exception;

}
