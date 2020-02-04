package org.mvnsearch.boot.xtermjs.commands;

import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Process build test
 *
 * @author linux_china
 */
public class ProcessBuilderTest {

	@Test
	public void testParse() {
		String line = "ls -al ./ ./ 'demo' '$home' ";
		DefaultParser parser = new DefaultParser();
		ParsedLine parsedLine = parser.parse(line, 0);
		for (String word : parsedLine.words()) {
			System.out.println(word);
		}
	}

	@Test
	public void testExecute() throws Exception {
		Process p = new ProcessBuilder("ls", "-al", "/").start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringBuilder builder = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
			builder.append(System.getProperty("line.separator"));
		}
		String result = builder.toString();
		System.out.println(result);
	}

}
