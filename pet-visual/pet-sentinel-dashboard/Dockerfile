FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER zy


ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8718

ADD ./target/pet-sentinel-dashboard.jar ./app.jar

ENTRYPOINT java -jar -Xms524m -Xmx560m -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$active app.jar

