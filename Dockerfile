#FROM gradle:9.1.0-jdk17 as build
#LABEL authors="ren1kron"
#WORKDIR /app
#
#
#COPY build.gradle.kts settings.gradle.kts gradle.properties ./
#COPY gradle ./gradle/
#
#RUN gradle clean war --no-daemon
#
#COPY src ./src
#
## FROM tomcat:11.0.11-jdk17-temurin
#FROM tomcat:11.0-jdk17
#RUN rm -rf /usr/local/tomcat/webapps/*
#COPY --from=build /app/build/libs/*.war /usr/local/tomcat/webapps/ROOT.war
##COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war
#
## optional: make JVM/system props configurable
#ENV CATALINA_OPTS=""
#EXPOSE 8080
#CMD ["catalina.sh", "run"]

FROM tomcat:11.0-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

ENV CATALINA_OPTS=""
EXPOSE 8080
CMD ["catalina.sh", "run"]
