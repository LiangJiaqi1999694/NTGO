FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER zy

WORKDIR /mnt/seata-server

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 7091
EXPOSE 8091

ADD ./target/pet-seata-serve.jar ./app.jar

ENTRYPOINT java -jar -Xms524m -Xmx560m -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=prod app.jar

