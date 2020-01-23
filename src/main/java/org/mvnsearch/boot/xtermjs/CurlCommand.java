package org.mvnsearch.boot.xtermjs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.shell.Shell;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * curl command
 *
 * @author linux_china
 */
@ShellComponent
public class CurlCommand {

	@Autowired
	private Shell shell;

	public static WebClient webClient = WebClient.builder().build();

	public static WebClient webClientFollowRedirect = WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true))).build();

	@ShellMethod("Curl command")
	public Mono<String> curl(
			@ShellOption(value = { "" }, defaultValue = ShellOption.NULL, help = "Request URL") URI uri,
			@ShellOption(value = { "-X", "--request" }, defaultValue = "GET",
					help = "Specify request command to use") HttpMethod requestMethod,
			@ShellOption(value = { "-d", "--data" }, defaultValue = ShellOption.NULL,
					help = "HTTP POST data") String data,
			@ShellOption(value = { "--data-urlencode" }, defaultValue = ShellOption.NULL,
					help = "HTTP POST data url encoded") String urlEncodedData,
			@ShellOption(value = { "-H", "--header" }, defaultValue = ShellOption.NULL,
					help = "Custom header to pass to server") String header,
			@ShellOption(value = { "-b", "--cookie" }, defaultValue = ShellOption.NULL,
					help = "Send cookies") String cookies,
			@ShellOption(value = { "-u", "--user" }, defaultValue = ShellOption.NULL,
					help = "Send cookies") String user,
			@ShellOption(value = { "-A", "--user-agent" }, defaultValue = ShellOption.NULL,
					help = "User-Agent to send to server") String userAgent,
			@ShellOption(value = { "-e", "--referer" }, defaultValue = ShellOption.NULL,
					help = "Referer URL") String refer,
			@ShellOption(value = { "-m", "--max-time" }, defaultValue = "300",
					help = "Maximum time allowed for the transfer") int maxTime,
			@ShellOption(arity = 0, value = { "-I", "--head" }, defaultValue = "false",
					help = "Show document info only") boolean headOnly,
			@ShellOption(arity = 0, value = { "-v", "--verbose" }, defaultValue = "false",
					help = "Verbose") boolean verbose,
			@ShellOption(arity = 0, value = { "-L", "--location" }, defaultValue = "false",
					help = "Follow redirects") boolean followRedirects,
			@ShellOption(arity = 0, value = { "-K", "--insecure" }, defaultValue = "false") boolean allowInsecure,
			@ShellOption(arity = 0, value = { "-v", "--version" }, defaultValue = "false",
					help = "Show version number and quit") boolean showVersion,
			@ShellOption(arity = 0, value = { "-h", "--help" }, defaultValue = "false",
					help = "Show help information") boolean showHelp) {
		if (showVersion) { // show version
			return Mono.just("7.0.0 - WebClient");
		}
		if (showHelp) { // show help
			return Mono.just(this.shell.evaluate(() -> "help curl").toString());
		}
		WebClient.RequestBodySpec requestBodySpec;
		// follow redirects
		if (followRedirects) {
			requestBodySpec = webClientFollowRedirect.method(requestMethod).uri(uri);
		}
		else {
			requestBodySpec = webClient.method(requestMethod).uri(uri);
		}
		// Host header
		requestBodySpec.header("Host", uri.getHost());
		// optional header
		if (!StringUtils.isEmpty(header)) {
			String[] parts = header.split(":", 2);
			if (parts.length > 1) {
				requestBodySpec = requestBodySpec.header(parts[0], parts[1]);
			}
			else {
				requestBodySpec = requestBodySpec.header(parts[0], "");
			}
		}
		if (!StringUtils.isEmpty(userAgent)) {
			requestBodySpec = requestBodySpec.header("User-Agent", userAgent);
		}
		else {
			requestBodySpec = requestBodySpec.header("User-Agent", "curl/7.64.1");
		}
		if (!StringUtils.isEmpty(refer)) {
			requestBodySpec = requestBodySpec.header("Referer", userAgent);
		}
		if (!StringUtils.isEmpty(data)) {
			requestBodySpec = (WebClient.RequestBodySpec) requestBodySpec.body(data, String.class);
		}
		if (!StringUtils.isEmpty(urlEncodedData)) {
			MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
			String[] pairs = urlEncodedData.split("&");
			for (String pair : pairs) {
				String[] parts = pair.split("=");
				if (parts.length > 1) {
					multiValueMap.add(parts[0], parts[1]);
				}
				else {
					multiValueMap.add(parts[0], "");
				}
			}
			requestBodySpec = (WebClient.RequestBodySpec) requestBodySpec
					.body(BodyInserters.fromFormData(multiValueMap));
		}
		if (!StringUtils.isEmpty(user)) {
			requestBodySpec.header("Authorization", "Basic " + Base64Utils.encodeToString(user.getBytes()));
		}
		// request headers information
		final String requestHeaderLines;
		if (verbose) {
			List<String> requestLines = new ArrayList<>();
			int port = uri.getPort();
			if (port == -1) {
				if (uri.getScheme().equals("http")) {
					port = 80;
				}
				else if (uri.getScheme().equalsIgnoreCase("https")) {
					port = 443;
				}
			}
			requestLines.add("Connected to " + uri.getHost() + " port " + port);
			requestLines.add(requestMethod.name() + " " + uri.getPath() + " HTTP/1.1");
			requestBodySpec.headers(httpHeaders -> {
				httpHeaders.forEach((name, values) -> {
					values.forEach(value -> {
						requestLines.add(name + ": " + value);
					});
				});
			});
			requestHeaderLines = String.join("\r\n", requestLines);
		}
		else {
			requestHeaderLines = "";
		}
		return requestBodySpec.exchange().flatMap(response -> {
			Mono<String> body = response.bodyToMono(String.class);
			if (verbose || headOnly) {
				body = body.map(textBody -> {
					List<String> lines = new ArrayList<>();
					// response code
					HttpStatus httpStatus = response.statusCode();
					lines.add("HTTP/1.1 " + httpStatus.value() + " " + httpStatus.getReasonPhrase());
					// response headers
					response.headers().asHttpHeaders().forEach((name, values) -> {
						values.forEach(value -> {
							lines.add(name + ": " + value);
						});
					});
					String responseHeaderLines = String.join("\r\n", lines);
					// add body
					if (headOnly) {
						return responseHeaderLines;
					}
					else {
						return requestHeaderLines + "\r\n\r\n" + responseHeaderLines + "\r\n\r\n" + textBody;
					}
				});
			}
			return body;
		}).timeout(Duration.ofSeconds(maxTime));
	}

}
