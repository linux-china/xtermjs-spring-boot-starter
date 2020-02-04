package org.mvnsearch.boot.xtermjs.commands;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer;
import org.springframework.boot.actuate.health.*;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.ClassUtils;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Spring commands: include spring framework, spring boot, spring cloud etc
 *
 * @author linux_china
 */
@ShellComponent
public class SpringCommands implements CommandsSupport {

	private String startedTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'").format(new Date());

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private AbstractEnvironment env;

	@Autowired
	private ConfigurableListableBeanFactory beanFactory;

	@Autowired
	private HealthEndpoint healthEndpoint;

	@Autowired
	private MeterRegistry meterRegistry;

	@Autowired
	private WebEndpointDiscoverer endpointDiscoverer;

	@ShellMethod("Display application info")
	public String app() {
		List<String> lines = new ArrayList<>();
		if (env.getProperty("spring.application.name") != null) {
			lines.add("Application name: " + env.getProperty("spring.application.name"));
		}
		lines.add("User Home: " + env.getProperty("user.home"));
		lines.add("Work Dir: " + env.getProperty("user.dir"));
		lines.add("Shell: " + env.getProperty("SHELL", ""));
		if (env.getProperty("PID") != null) {
			lines.add("PID: " + env.getProperty("PID"));
		}
		lines.add("Started Time: " + startedTime);
		lines.add("Java Version: " + System.getProperty("java.version"));
		lines.add("OS Name: " + System.getProperty("os.name"));
		lines.add("OS Version: " + System.getProperty("os.version"));
		lines.add("OS Arch: " + System.getProperty("os.arch"));
		lines.add("Spring Boot Version: " + SpringBootVersion.getVersion());
		// cpu + memory + disk
		int mb = 1024 * 1024;
		int gb = mb * 1024;
		Runtime runtime = Runtime.getRuntime();
		lines.add("CPU Cores: " + runtime.availableProcessors());
		lines.add("Total Memory(M): " + runtime.totalMemory() / mb);
		lines.add("Free Memory(M): " + runtime.freeMemory() / mb);
		lines.add("Used Memory(M): " + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		lines.add("Max Memory(M): " + runtime.maxMemory() / mb);
		File path = new File(".");
		lines.add("Total Space(G): " + path.getTotalSpace() / gb);
		lines.add("Free Space(G): " + path.getUsableSpace() / gb);
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			lines.add("IP: " + inetAddress.getHostAddress());
			lines.add("Host: " + inetAddress.getHostName());
		}
		catch (Exception ignore) {

		}
		return linesToString(lines);
	}

	@ShellMethod("Execute SpEL")
	public String spel() {
		return "Not implemented";
	}

	@ShellMethod("Display beans info")
	public String beans() {
		List<String> lines = new ArrayList<>(Arrays.asList(beanFactory.getBeanDefinitionNames()));
		lines.add("");
		lines.add("Beans: " + beanFactory.getBeanDefinitionNames().length);
		return linesToString(lines);
	}

	@ShellMethod("Display bean info")
	public String bean(@ShellOption(help = "Bean name or class") String beanNameOrClass) {
		List<String> lines = new ArrayList<>();
		String nameForSearch = beanNameOrClass;
		if (beanNameOrClass.contains("*")) {
			nameForSearch = beanNameOrClass.replaceAll("\\*", "").toLowerCase();
		}
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			String beanClassName = beanDefinition.getBeanClassName();
			Object bean = applicationContext.getBean(beanName);
			if (beanClassName == null) {
				beanClassName = bean.getClass().getCanonicalName();
				if (beanClassName == null) {
					beanClassName = bean.getClass().getSimpleName();
				}
			}
			boolean matched;
			if (beanNameOrClass.contains("*")) {
				matched = beanName.toLowerCase().contains(nameForSearch)
						|| beanClassName.toLowerCase().contains(nameForSearch);
			}
			else {
				matched = beanNameOrClass.equalsIgnoreCase(beanName) || beanNameOrClass.equalsIgnoreCase(beanClassName);
			}
			if (matched) {
				if (!lines.isEmpty()) {
					lines.add("-------------------------------");
				}
				lines.add("Name: " + beanName);
				lines.add("Class: " + beanClassName);
				if (beanDefinition.getFactoryBeanName() != null) {
					lines.add("Factory Bean: " + beanDefinition.getFactoryBeanName());
				}
				if (beanDefinition.getFactoryMethodName() != null) {
					lines.add("Factory Method: " + beanDefinition.getFactoryMethodName());
				}
				if (beanDefinition.getScope() != null && !beanDefinition.getScope().isEmpty()) {
					lines.add("Scope: " + beanDefinition.getScope());
				}
				if (beanDefinition.getDependsOn() != null && beanDefinition.getDependsOn().length > 0) {
					lines.add("DependsOn: " + String.join("\n", beanDefinition.getDependsOn()));
				}
				if (beanDefinition.getParentName() != null) {
					lines.add("Parent: " + beanDefinition.getParentName());
				}
				Class<?>[] allInterfaces = ClassUtils.getAllInterfaces(bean);
				for (Class<?> beanInterface : allInterfaces) {
					lines.add("=========" + beanInterface.getCanonicalName() + " ========");
					Arrays.stream(beanInterface.getDeclaredMethods()).map(method -> {
						if (method.getParameterCount() == 0) {
							return formatClassName(method.getGenericReturnType().getTypeName()) + " " + method.getName()
									+ "()";
						}
						else {
							String parameterTypes = Arrays.stream(method.getParameters()).map(parameter -> {
								String parameterName = parameter.getName();
								return formatClassName(parameter.getParameterizedType().getTypeName()) + " "
										+ parameterName;
							}).collect(Collectors.joining(", "));
							return formatClassName(method.getGenericReturnType().getTypeName()) + " " + method.getName()
									+ "(" + parameterTypes + ")";
						}
					}).forEach(lines::add);
				}
			}

		}
		return linesToString(lines);
	}

	@ShellMethod("Display env info")
	public String env(@ShellOption(help = "env name", defaultValue = "") String envName) {
		List<String> lines = new ArrayList<>();
		if (!envName.isEmpty()) {
			String key = envName;
			String value = env.getProperty(key);
			if (value == null) {
				key = envName.toUpperCase();
				value = env.getProperty(key);
			}
			if (value == null) {
				lines.add("Env name not found!");
			}
			else {
				lines.add(key + ": " + value);
			}
		}
		else {
			for (PropertySource<?> propertySource : env.getPropertySources()) {
				if (propertySource instanceof EnumerablePropertySource) {
					EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) propertySource;
					for (String propertyName : enumerablePropertySource.getPropertyNames()) {
						lines.add(propertyName + ": " + env.getProperty(propertyName));
					}
				}
			}
			lines.add("");
			lines.add("Env names: " + (lines.size() - 1));
		}
		return linesToString(lines);
	}

	@ShellMethod("Display metrics")
	public String metrics(@ShellOption(help = "metrics name", defaultValue = "") String metricsName) {
		List<String> lines = new ArrayList<>();
		if (!metricsName.isEmpty()) {
			for (Meter meter : meterRegistry.getMeters()) {
				String meterName = meter.getId().getName();
				if (meterName.contains(metricsName)) {
					lines.add(meterName(metricsName, meter.getId().getTags()) + ": "
							+ meter.measure().iterator().next().getValue());
				}
			}
		}
		else {
			for (Meter meter : meterRegistry.getMeters()) {
				lines.add(meterName(meter.getId().getName(), meter.getId().getTags()) + ": "
						+ meter.measure().iterator().next().getValue());
			}
			lines.add("");
			lines.add("Metrics Count: " + (lines.size() - 1));
		}
		return linesToString(lines);
	}

	@ShellMethod("Display health")
	public String health(@ShellOption(help = "health component name", defaultValue = "") String componentName) {
		List<String> lines = new ArrayList<>();
		HealthComponent healthComponent = healthEndpoint.health();
		Map<String, HealthComponent> healthComponents;
		if (healthComponent instanceof SystemHealth) {
			healthComponents = ((SystemHealth) healthComponent).getComponents();
		}
		else if (healthComponent instanceof CompositeHealth) {
			healthComponents = ((CompositeHealth) healthComponent).getComponents();
		}
		else {
			healthComponents = new HashMap<>();
			healthComponents.put("Default", healthComponent);
		}
		for (Map.Entry<String, HealthComponent> entry : healthComponents.entrySet()) {
			Status status = entry.getValue().getStatus();
			AttributedStyle foreground = AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN);
			if (!status.getCode().equalsIgnoreCase(Status.UP.getCode())) {
				foreground = AttributedStyle.DEFAULT.foreground(AttributedStyle.RED);
			}
			String line;
			if (status.getDescription() != null && !status.getDescription().isEmpty()) {
				line = healthName(entry.getKey(), entry.getValue()) + ": " + status.getCode() + " -- "
						+ status.getDescription();
			}
			else {
				line = healthName(entry.getKey(), entry.getValue()) + ": " + status.getCode();
			}
			lines.add(new AttributedString(line, foreground).toAnsi());
		}
		return linesToString(lines);
	}

	@ShellMethod("Display profiles")
	public String profiles() {
		String[] profiles = env.getActiveProfiles();
		if (profiles.length > 0) {
			return "Active Profiles: [" + String.join(",", profiles) + "]";
		}
		return "No active profiles";
	}

	@ShellMethod("Display logfile")
	public List<String> log() throws Exception {
		String loggingFilePath = env.getProperty("logging.file.path");
		String loggingFileName = env.getProperty("logging.file.name");
		if (loggingFilePath == null || loggingFileName == null) {
			throw new Exception("Missing 'logging.file.name' or 'logging.file.path' properties");
		}
		File loggingFile = new File(loggingFilePath, loggingFileName);
		if (!loggingFile.exists()) {
			throw new Exception("Logging file not found: " + loggingFile.getAbsolutePath());
		}
		ReversedLinesFileReader object = new ReversedLinesFileReader(loggingFile, StandardCharsets.UTF_8);
		List<String> lines = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			String line = object.readLine();
			if (line == null)
				break;
			lines.add(line);
		}
		Collections.reverse(lines);
		return lines;
	}

	@ShellMethod("Display actuator")
	public Mono<String> actuator(@ShellOption(help = "health component name", defaultValue = "") String endpoint) {
		if (endpoint.isEmpty()) {
			return Mono.just(endpointDiscoverer.getEndpoints().stream()
					.map(exposableWebEndpoint -> exposableWebEndpoint.getEndpointId().toLowerCaseString())
					.collect(Collectors.joining(",")));
		}
		String serverPort = env.getProperty("server.port");
		if (serverPort == null || serverPort.equals("-1")) {
			serverPort = "8080";
		}
		String managementPort = env.getProperty("management.server.port", serverPort);
		String managementPath = env.getProperty("management.endpoints.web.base-path", "/actuator");
		return CurlCommand.webClient.get().uri("http://127.0.0.1:" + managementPort + managementPath + "/" + endpoint)
				.retrieve().bodyToMono(String.class);
	}

	private static String meterName(String name, List<Tag> tags) {
		if (tags != null && tags.size() > 0) {
			return tags.stream().map(tag -> tag.getKey() + ":" + tag.getValue())
					.collect(Collectors.joining(",", name + "(", ")"));
		}
		return name;
	}

	private static String healthName(String name, HealthComponent healthComponent) {
		if (healthComponent instanceof Health) {
			Map<String, Object> details = ((Health) healthComponent).getDetails();
			if (details != null && !details.isEmpty()) {
				return details.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue().toString())
						.collect(Collectors.joining(",", name + "(", ")"));
			}
		}
		return name;
	}

}
