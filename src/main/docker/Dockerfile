FROM ubuntu

# Install Java 8
RUN apt-get update
RUN apt-get install -y software-properties-common
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java8-installer

#Configure env
RUN mkdir app
COPY server-0.0.1-SNAPSHOT.jar /app/server.jar

ENTRYPOINT [ "sh", "-c", "java -jar /app/server.jar" ]
