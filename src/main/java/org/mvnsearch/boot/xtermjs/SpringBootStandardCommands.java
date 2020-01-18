package org.mvnsearch.boot.xtermjs;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @ShellMethod("Display application info")
    public String system() {
        List<String> lines = new ArrayList<>();
        lines.add("Started Time: " + startedTime);
        lines.add("Java Version: " + System.getProperty("java.version"));
        return String.join("\n", lines);
    }
}
