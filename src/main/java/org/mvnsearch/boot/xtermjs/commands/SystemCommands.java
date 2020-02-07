package org.mvnsearch.boot.xtermjs.commands;

import org.apache.commons.io.FilenameUtils;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.Mono;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * System Commands
 *
 * @author linux_china
 */
@ShellComponent
public class SystemCommands implements CommandsSupport {

    @ShellMethod("Date command")
    public String date() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        return dateFormat.format(new Date());
    }

    @ShellMethod("cd command")
    public Mono<String> cd(@ShellOption(help = "path name", defaultValue = "") String path) throws Exception {
        return Mono.deferWithContext(context -> {
            String currentDir = context.get("path");
            File dest;
            if (path.isEmpty()) {
                dest = new File(System.getProperty("user.home", "."));
            } else if (path.startsWith("/")) {
                dest = new File(path);
            } else {
                dest = new File(new File(currentDir), path);
            }
            String absolutePath = FilenameUtils.normalize(dest.getAbsolutePath());
            if (!dest.exists()) {
                return Mono.error(new Exception("Directory not existed: " + absolutePath));
            } else if (!dest.isDirectory()) {
                return Mono.error(new Exception("Not directory: " + absolutePath));
            } else {
                context.put("path", absolutePath);
                return Mono.just("$path:" + absolutePath);
            }
        });
    }

}
