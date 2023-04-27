#!/bin/sh
APP_HOME=`pwd`
PWD=$APP_HOME

echo '开始编译'

mvn clean install

echo '编译完成'

echo '启动网关中'

cd PWD/pet-gateway/
sh start.sh

echo '网关启动结束'

echo '启动gen'

cd PWD/pet-modules/pet-gen

sh start.sh

echo '启动resource'
cd PWD/pet-modules/pet-resource/
sh start.sh
echo '启动system'

cd PWD/pet-modules/pet-system/
sh start.sh

echo '启动xxl-admin'
cd PWD/pet-xxl-nacos/pet-xxl-admin/
sh start.sh

echo '启动xxl-executor'
cd PWD/pet-xxl-nacos/pet-xxl-executor/
sh start.sh


