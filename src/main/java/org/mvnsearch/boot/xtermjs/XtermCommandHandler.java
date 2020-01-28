package org.mvnsearch.boot.xtermjs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.mvnsearch.boot.xtermjs.commands.CustomizedCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Xterm command handler: execute the commands from xterm.js
 *
 * @author linux_china
 */
public class XtermCommandHandler {

	@Autowired
	private Shell shell;

	private ObjectMapper objectMapper;

	@Autowired
	public List<CustomizedCommand> customizedCommands;

	private List<String> stringOutputClasses = Arrays.asList("java.util.Date", "java.lang.Boolean", "java.lang.Void");

	private Map<String, CustomizedCommand> customizedCommandMap = new HashMap<>();

	@PostConstruct
	public void init() {
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new Jdk8Module());
		this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
		for (CustomizedCommand customizedCommand : customizedCommands) {
			customizedCommandMap.put(customizedCommand.getName(), customizedCommand);
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
		if (customizedCommandMap.containsKey(command)) {
			result = customizedCommandMap.get(command).execute(arguments);
		}
		else {
			result = this.shell.evaluate(() -> commandLine);
		}
		String textOutput;
		if (result == null) {
			textOutput = "";
		}
		else if (result instanceof Exception) {
			textOutput = new AttributedString(result.toString(),
					AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAnsi();
		}
		else if (result instanceof AttributedString) {
			textOutput = ((AttributedString) result).toAnsi();
		}
		else if (result instanceof Collection) {
			textOutput = String.join("\r\n", (Collection) result);
		}
		else if (result instanceof Mono) {
			return ((Mono<String>) result).map(this::formatObject).map(this::formatLineBreak).onErrorReturn("")
					.defaultIfEmpty("");
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

	public String formatLineBreak(String textOutput) {
		if (!textOutput.contains("\r\n") && textOutput.contains("\n")) {
			return textOutput.replaceAll("\n", "\r\n");
		}
		else {
			return textOutput;
		}
	}

	public String formatObject(Object object) {
		// to string or json output
		String classFullName = object.getClass().getCanonicalName();
		if (classFullName == null) {
			classFullName = object.getClass().getSimpleName();
		}
		if (object instanceof CharSequence || object instanceof Number || object instanceof Throwable) {
			return object.toString();
		}
		else if (stringOutputClasses.contains(classFullName)) {
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
