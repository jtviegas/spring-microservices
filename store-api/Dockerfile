FROM java:openjdk-8-jre

RUN mkdir -p /opt/app
ADD target/store-api-0.0.1-SNAPSHOT.jar /opt/app/app.jar
ADD start.sh /opt/app/start.sh
WORKDIR /opt/app
CMD [ "./start.sh" ]