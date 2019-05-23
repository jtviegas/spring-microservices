FROM java:openjdk-8-jre

RUN mkdir -p /opt/app
ADD target/api-0.0.1-SNAPSHOT.jar /opt/app/app.jar
ADD start.sh /opt/app/
WORKDIR /opt/app
CMD [ "./start.sh" ]