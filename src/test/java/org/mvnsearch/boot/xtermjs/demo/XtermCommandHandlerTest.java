package org.mvnsearch.boot.xtermjs.demo;

import org.junit.jupiter.api.Test;
import org.mvnsearch.boot.xtermjs.XtermCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * XtermCommandHandler test
 *
 * @author linux_china
 */
@SpringBootTest
public class XtermCommandHandlerTest {

	@Autowired
	private XtermCommandHandler commandHandler;

	@Test
	public void testFormatObject() {
		System.out.println(commandHandler.formatObject("good morning"));
		System.out.println(commandHandler.formatObject(new User(1, "linux_china", new Date())));
	}

}
