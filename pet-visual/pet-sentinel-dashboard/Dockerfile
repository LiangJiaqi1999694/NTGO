FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER zy


ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8718

ADD ./target/pet-sentinel-dashboard.jar ./app.jar
ENV wd ${active}
# 指定容器启动时，需要执行的命令(运行该jar程序)
ENTRYPOINT java -jar -Xms524m -Xmx560m -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$wd app.jar
