FROM java:openjdk-8-jre

RUN mkdir -p /opt/app
ADD target/configserver-0.0.1-SNAPSHOT.jar /opt/app/app.jar
RUN bash -c 'touch /opt/app/app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/app/app.jar"]