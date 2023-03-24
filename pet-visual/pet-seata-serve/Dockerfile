FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER zy

RUN mkdir -p /mnt/seata-server/logs \
    /mnt/skywalking/agent

WORKDIR /mnt/seata-server

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 7091
EXPOSE 8091

ADD ./target/pet-seata-serve.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom",
            "-jar", "app.jar"]
