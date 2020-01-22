package org.mvnsearch.boot.xtermjs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringBootVersion;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Spring Boot standard commands
 *
 * @author linux_china
 */
@ShellComponent
public class SpringBootStandardCommands {

	private String startedTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'").format(new Date());

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private Environment env;

	@Autowired
	private ConfigurableListableBeanFactory beanFactory;

	@ShellMethod("Display application info")
	public String system() {
		List<String> lines = new ArrayList<>();
		lines.add("Started Time: " + startedTime);
		lines.add("Java Version: " + System.getProperty("java.version"));
		lines.add("OS Name: " + System.getProperty("os.name"));
		lines.add("OS Version: " + System.getProperty("os.version"));
		lines.add("Spring Boot Version: " + SpringBootVersion.getVersion());
		if (env.getProperty("spring.application.name") != null) {
			lines.add("Application name: " + env.getProperty("spring.application.name"));
		}
		return String.join("\n", lines);
	}

	@ShellMethod("Display beans info")
	public String beans() {
		List<String> lines = new ArrayList<>(Arrays.asList(beanFactory.getBeanDefinitionNames()));
		lines.add("");
		lines.add("Beans: " + beanFactory.getBeanDefinitionNames().length);
		return String.join("\n", lines);
	}

	@ShellMethod("Display bean info")
	public String bean(@ShellOption(help = "Bean name or class") String beanNameOrClass) {
		List<String> lines = new ArrayList<>();
		String nameForSearch = beanNameOrClass.toLowerCase();
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			String beanClassName = beanDefinition.getBeanClassName();
			if (beanName.toLowerCase().contains(nameForSearch)
					|| (beanClassName != null && beanClassName.toLowerCase().contains(nameForSearch))) {
				if (!lines.isEmpty()) {
					lines.add("-------------------------------");
				}
				lines.add("Name: " + beanName);
				if (beanClassName != null) {
					lines.add("Class: " + beanClassName);
				}
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
			}
		}
		return String.join("\n", lines);
	}

}
