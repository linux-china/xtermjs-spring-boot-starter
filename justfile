install:
   rm -rf  ~/.m2/repository/org/mvnsearch/xterm-spring-boot-starter/
   mvn -DskipTests clean source:jar package install

deploy:
   mvn -P release -DskipTests clean package deploy

format:
   mvn spring-javaformat:apply