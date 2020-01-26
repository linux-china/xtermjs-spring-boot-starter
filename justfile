deploy:
   mvn -P release -DskipTests clean package deploy

install:
   rm -rf  ~/.m2/repository/org/mvnsearch/xterm-spring-boot-starter/
   mvn -DskipTests clean source:jar package install