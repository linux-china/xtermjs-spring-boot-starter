package org.mvnsearch.boot.xtermjs.demo;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * my commands
 *
 * @author linux_china
 */
@ShellComponent
public class MyCommands {
    private static AttributedStyle RED_FONT_STYLE = AttributedStyle.DEFAULT.foreground(AttributedStyle.RED);
    private static AttributedStyle GREEN_FONT_STYLE = AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN);

    @ShellMethod("Add two integers together.")
    public int add(int a, int b) {
        return a + b;
    }

    @ShellMethod("Minus two integers together.")
    public int minus(int a, int b) {
        return a - b;
    }

    @ShellMethod("Display information.")
    public AttributedString info() {
        return new AttributedString("Xterm with RSocket! http://rsocket.io ", GREEN_FONT_STYLE);
    }
}