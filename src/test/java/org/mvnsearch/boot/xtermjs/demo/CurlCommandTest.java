package org.mvnsearch.boot.xtermjs.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.Shell;
import reactor.core.publisher.Mono;

/**
 * curl command test
 *
 * @author linux_china
 */
@SpringBootTest
public class CurlCommandTest {

	@Autowired
	private Shell shell;

	@Test
	public void testCurl() {
		Object result = shell.evaluate(() -> "curl -X GET --verbose http://httpbin.org/ip");
		if (result instanceof Mono) {
			System.out.println(((Mono<String>) result).block());
		}
	}

}
