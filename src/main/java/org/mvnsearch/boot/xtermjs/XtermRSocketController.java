package org.mvnsearch.boot.xtermjs;

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Subst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

/**
 * xterm rsocket controller
 *
 * @author linux_china
 */
@Controller
public class XtermRSocketController extends XtermCommandHandler {

	@Autowired
	private Environment environment;

	@MessageMapping("xterm.shell")
	public Flux<String> shell(Flux<String> commands) {
		return commands.filter(data -> !data.trim().isEmpty()).flatMap(this::executeCommand);
	}

	@GetMapping(value = "/xterm", produces = "text/html; charset=utf-8")
	@ResponseBody()
	public String xterm() {
		@Subst("/rsocket")
		String path = environment.getProperty("spring.rsocket.server.mapping-path");
		@Subst("app-name")
		String appName = environment.getProperty("spring.application.name");
		@Language("HTML")
		String html = "<!doctype html>\n" + "<html lang=\"en\">\n" + "<head>\n" + "    <meta charset=\"UTF-8\">\n"
				+ "    <title>Xterm</title>\n" + "    <style>\n" + "        html, body {\n"
				+ "            height: 100%;\n" + "        }\n" + "    </style>\n" + "    <script>\n"
				+ "        var title =\"Xterm for " + appName + "\";\n" + "    " + "</script>\n" + "</head>\n"
				+ "<body>\n" + "<xterm-console rsocket=\"" + path + "\"></xterm-console>\n"
				+ "<script type=\"text/javascript\" src=\"/xterm.bundle.js\"></script></body>\n" + "</html>";
		return html;
	}

}
