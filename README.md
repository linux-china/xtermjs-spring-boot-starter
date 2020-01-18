xtermjs-spring-boot-starter
===========================
[![Build Status](https://api.travis-ci.com/linux-china/xtermjs-spring-boot-starter.svg?branch=master)](https://travis-ci.com/linux-china/xtermjs-spring-boot-starter)
Integrate xterm for Spring Boot with Xterm.js & Spring Shell

# Why terminal in browser?

With app terminal in browser, and you can understand all things in app.

![Xterm Console](console.png)

# How to use?

* Edit pom.xml and add following dependency:

```xml
<dependency>
    <groupId>org.mvnsearch.boot</groupId>
    <artifactId>xterm-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>    
```

* Open application.properties and add gossip.seeds configuration.

```properties
# rsocket websocket
spring.rsocket.server.mapping-path=/rsocket
spring.rsocket.server.transport=websocket
```

* Start Spring Boot app and visit http://localhost:8080/xterm


# Internal commands

* Help: Spring Shell help
* exit/quit: close window/tab of terminal
* system: display system information
