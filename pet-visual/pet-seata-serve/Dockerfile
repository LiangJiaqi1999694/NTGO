FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER zy


ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ARG active
EXPOSE 7091
EXPOSE 8091

ADD ./target/pet-seata-serve.jar ./app.jar
RUN echo "-Dspring.profiles.active=${active}"
ENV wd ${active}
# 指定容器启动时，需要执行的命令(运行该jar程序)
ENTRYPOINT java -jar -Xms524m -Xmx560m -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$wd app.jar
