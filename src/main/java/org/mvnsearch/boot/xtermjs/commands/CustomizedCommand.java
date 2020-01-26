package org.mvnsearch.boot.xtermjs.commands;

import org.jetbrains.annotations.Nullable;

/**
 * customized command
 *
 * @author linux_china
 */
public interface CustomizedCommand {

	public String getName();

	@Nullable
	public Object execute(@Nullable String arguments);

}
