# 基于java:8镜像进行扩展
FROM openjdk:8
# 指定制作该镜像的作者和联系方式
MAINTAINER zhuangyong 503897166@qq.com
ENV TZ "Asia/Shanghai"
ENV LANG C.UTF-8
# 指定容器内进程对外开放的端口
EXPOSE 9208
# 定义数据卷位置---存放容器共享文件的位置
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
# 将maven打成的jar包复制到镜像目录中（相对路径---默认"."就是Dockerfile所在目录）
ADD target/pet-xxl-admin.jar app.jar
# 指定容器启动时，需要执行的命令(运行该jar程序)
ENTRYPOINT java -jar -Xms256m -Xmx256m -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=prod app.jar
