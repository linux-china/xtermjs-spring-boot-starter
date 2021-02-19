package org.mvnsearch.boot.xtermjs.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * JVM commands
 *
 * @author linux_china
 */
@ShellComponent
public class JvmCommands implements CommandsSupport {

	@ShellMethod("Java Options")
	public String options() {
		return linesToString(ManagementFactory.getRuntimeMXBean().getInputArguments());
	}

	@ShellMethod("Show Threads")
	public String threads() {
		String threadPattern = "%4s %5s %10s %8s %16s  %s";
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		List<String> lines = new ArrayList<>();
		lines.add(String.format(threadPattern, "ID", "Alive", "State", "Priority", "Group", "Name", "Daemon"));
		threadSet.stream().map(thread -> {
			return String.format(threadPattern, thread.getId(), thread.isAlive() ? 1 : 0, thread.getState().name(),
					thread.getPriority(), (thread.getThreadGroup() == null ? "" : thread.getThreadGroup().getName()),
					thread.getName(), thread.isDaemon());
		}).forEach(lines::add);
		lines.add("");
		lines.add("Thread Count: " + (lines.size() - 1));
		return linesToString(lines);
	}

	@ShellMethod("Show Thread")
	public String thread(@ShellOption(help = "Thread ID", defaultValue = "") Integer threadId) {
		return "";
	}

	@ShellMethod("Show Memory")
	public String mem() {
		StringBuilder builder = new StringBuilder();
		builder.append("HeapSize:").append(Runtime.getRuntime().totalMemory());
		builder.append("HeapMaxSize:").append(Runtime.getRuntime().maxMemory());
		builder.append("HeapFreeSize:").append(Runtime.getRuntime().freeMemory());
		return builder.toString();
	}

	@ShellMethod("Show CPU")
	public String cpu() {
		StringBuilder builder = new StringBuilder();
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		builder.append("arch:").append(operatingSystemMXBean.getArch());
		builder.append("processors:").append(operatingSystemMXBean.getAvailableProcessors());
		builder.append("load:").append(operatingSystemMXBean.getSystemLoadAverage());
		return builder.toString();
	}

	@ShellMethod("Display Classpath info")
	public String classpath() {
		List<String> lines = new ArrayList<>();
		String classPath = System.getProperty("CLASSPATH");
		if (classPath == null || classPath.isEmpty()) {
			classPath = ManagementFactory.getRuntimeMXBean().getClassPath();
		}
		if (classPath != null) {
			lines.addAll(Arrays.asList(classPath.split(File.pathSeparator)));
		}
		else {
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			URL[] urls = ((URLClassLoader) cl).getURLs();
			Arrays.stream(urls).map(URL::toString).forEach(lines::add);
		}
		return linesToString(lines);
	}

	@ShellMethod("demo")
	public String demo() {
		return ansi().fgRed().a("good").reset().toString();
	}

}
