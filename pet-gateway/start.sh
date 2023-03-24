#!/usr/bin/env bash
imagesname=pet-gayeway
#查找docker镜像id
imagesid=`docker images|grep -i ${imagesname}|awk '{print $3}'`
constainerid=`docker ps -a|grep -w ${imagesname}| awk '{print $1}'`

## 删除容器
if [ -z "constainerid" ]; then
   echo "constainerid is null"
else
    docker rm -f ${constainerid}
fi

## 删除镜像
if [ -z "$imagesid" ]; then
   echo "imagesid is null"
else
    docker rmi ${imagesid} -f
fi

#cd /mnt/omms/${imagesname}
## 生成镜像
docker build -t ${imagesname} .

## 启动镜像
docker run -d -p 8099:8099 -v /mnt/pet:/mnt/logs/ --name ${imagesname} ${imagesname}


