FROM ubuntu:16.10

# Install Java 8
RUN apt-get update
RUN apt-get install software-properties-common -y
RUN add-apt-repository ppa:webupd8team/java -y
RUN apt-get update
RUN echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections
RUN apt-get install oracle-java8-installer -y --force-yes
RUN apt-get install oracle-java8-set-default

#Install aws cli
RUN apt-get install awscli -y

#Configure env
RUN mkdir app
COPY server-0.0.1-SNAPSHOT.jar /app/server.jar
COPY temp /app/temp

ENTRYPOINT [ "sh", "-c", "java -jar /app/server.jar" ]
