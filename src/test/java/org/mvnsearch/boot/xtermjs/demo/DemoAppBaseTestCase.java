package org.mvnsearch.boot.xtermjs.demo;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * demo app base testcase
 *
 * @author linux_china
 */
@SpringBootTest(classes = XtermDemoApp.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class DemoAppBaseTestCase {

}
