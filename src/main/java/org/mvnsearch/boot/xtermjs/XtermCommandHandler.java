package org.mvnsearch.boot.xtermjs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.impl.DefaultParser;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.mvnsearch.boot.xtermjs.commands.CustomizedCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;
import org.zeroturnaround.exec.ProcessExecutor;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Xterm command handler: execute the commands from xterm.js
 *
 * @author linux_china
 */
public class XtermCommandHandler {

	@Autowired
	private Shell shell;

	@Autowired
	public List<CustomizedCommand> customizedCommands;

	private ObjectMapper objectMapper;

	/**
	 * command line parser
	 */
	private DefaultParser lineParser = new DefaultParser();

	private List<String> stringOutputClasses = Arrays.asList("java.util.Date", "java.lang.Boolean", "java.lang.Void");

	private Map<String, CustomizedCommand> customizedCommandMap = new HashMap<>();

	@PostConstruct
	public void init() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new Jdk8Module());
		this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
		for (CustomizedCommand customizedCommand : customizedCommands) {
			String[] names = customizedCommand.getNames();
			for (String name : names) {
				customizedCommandMap.put(name, customizedCommand);
			}
		}
	}

	public Mono<String> executeCommand(String commandLine) {
		String command;
		String arguments = null;
		int spaceIndex = commandLine.indexOf(" ");
		if (spaceIndex > 0) {
			command = commandLine.substring(0, spaceIndex);
			arguments = commandLine.substring(spaceIndex + 1).trim();
		}
		else {
			command = commandLine;
		}
		Object result;
		try {
			if (customizedCommandMap.containsKey(command)) {
				result = customizedCommandMap.get(command).execute(command, arguments);
			}
			else if (this.shell.listCommands().containsKey(command)) {
				result = this.shell.evaluate(() -> commandLine);
			}
			else {
				result = executeOsCommand(commandLine);
				// result = new Exception("Command not found!");
			}
		}
		catch (Exception e) {
			result = e;
		}
		String textOutput;
		if (result == null) {
			textOutput = "";
		}
		else if (result instanceof Mono) {
			return ((Mono<Object>) result).map(this::formatObject).map(this::formatLineBreak).onErrorResume(error -> {
				return Mono.just(formatObject(error));
			}).defaultIfEmpty("");
		}
		else {
			textOutput = formatObject(result);
		}
		// text format for Xterm
		if (!textOutput.contains("\r\n") && textOutput.contains("\n")) {
			return Mono.just(textOutput.replaceAll("\n", "\r\n"));
		}
		else {
			return Mono.just(textOutput);
		}
	}

	public Mono<Object> executeOsCommand(String commandLine) {
		return Mono.deferWithContext(context -> {
			try {
				List<String> command = lineParser.parse(commandLine, 0).words();
				String output = new ProcessExecutor().directory(new File((String) context.get("path"))).command(command)
						.readOutput(true).execute().outputUTF8();
				return Mono.just(output.trim());
			}
			catch (Exception e) {
				return Mono.error(e);
			}
		});

	}

	public String formatLineBreak(String textOutput) {
		if (!textOutput.contains("\r\n") && textOutput.contains("\n")) {
			return textOutput.replaceAll("\n", "\r\n");
		}
		else {
			return textOutput;
		}
	}

	public String formatObject(@NotNull Object object) {
		if (object instanceof Exception) {
			return new AttributedString(((Exception) object).getMessage(),
					AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi();
		}
		else if (object instanceof AttributedString) {
			return ((AttributedString) object).toAnsi();
		}
		else if (object instanceof CharSequence || object instanceof Number || object instanceof Throwable) {
			return object.toString();
		}
		else if (object instanceof Collection) {
			return String.join("\r\n", (Collection) object);
		}
		else {
			// to string or json output
			String classFullName = object.getClass().getCanonicalName();
			// toString() declared
			try {
				Method toStringMethod = object.getClass().getDeclaredMethod("toString");
				if (toStringMethod != null) {
					return object.toString();
				}
			}
			catch (Exception ignore) {

			}
			if (classFullName == null) {
				classFullName = object.getClass().getSimpleName();
			}
			if (stringOutputClasses.contains(classFullName)) {
				return object.toString();
			}
			else if (classFullName.startsWith("java.lang.") && classFullName.matches("java.lang.([A-Z]\\w*)")) {
				return object.toString();
			}
			else if (classFullName.startsWith("java.time.")) {
				return object.toString();
			}
			else {
				try {
					return "Class: " + classFullName + "\n" + objectMapper.writeValueAsString(object);
				}
				catch (Exception ignore) {
					return object.toString();
				}
			}
		}
	}

}
