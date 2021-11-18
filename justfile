install:
   rm -rf  ~/.m2/repository/org/mvnsearch/xterm-spring-boot-starter/
   mvn -DskipTests clean source:jar package install

deploy:
   mvn clean
   npm run build
   mvn -P release -DskipTests package deploy

format:
   mvn spring-javaformat:apply