xtermjs-spring-boot-starter
===========================
[![Build Status](https://api.travis-ci.com/linux-china/xtermjs-spring-boot-starter.svg?branch=master)](https://travis-ci.com/linux-china/xtermjs-spring-boot-starter)


Supply a web terminal to manage Spring Boot app. Why?

* Access easy: not install, no env setup, just open the browser
* Use commands to manage app
* No need to us curl to call lots of REST API
* Easy to write commands by Spring Shell

![Xterm Console](console.png)

**Attention**: You should care about security by yourself.

# How to use?

* Edit pom.xml and add following dependency:

```xml
<dependency>
    <groupId>org.mvnsearch</groupId>
    <artifactId>xtermjs-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>    
```

* Open application.properties and add rsocket configuration.

```properties
# rsocket websocket
spring.rsocket.server.mapping-path=/rsocket
spring.rsocket.server.transport=websocket
```

* Start Spring Boot app and visit http://localhost:8080/xterm


# Terminal Features

* History: arrow up/down
* Ctrl+U: clear input
* clear: clear the screen
* exit: close the window/tab

# Application commands

* Help: Spring Shell help
* system: display system information
* beans: display all spring beans
* bean: display bean definition
* profiles: display profile
* metrics: display metrics
* health: display health information
* classpath: display classpath
* spel: execute SpEL expression
* threads: display threads
* actuator: display actuator information
* curl: curl command for http operation

# References

* Xterm.js Home: https://xtermjs.org/
* Spring Shell Docs: https://docs.spring.io/spring-shell/docs/current/reference/htmlsingle/
* xterm.js + local echo https://www.linkedin.com/pulse/xtermjs-local-echo-ioannis-charalampidis
