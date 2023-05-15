#!/bin/sh
APP_HOME=`pwd`

echo '开始编译'

mvn clean install -P "$1"

echo '编译完成'

echo '启动网关中'

cd $APP_HOME/pet-gateway/
sh start.sh "$1"

echo '网关启动结束'

echo '启动gen'

cd $APP_HOME/pet-modules/pet-gen

sh start.sh "$1"

echo '启动resource'
cd $APP_HOME/pet-modules/pet-resource/
sh start.sh "$1"
echo '启动system'

cd $APP_HOME/pet-modules/pet-system/
sh start.sh "$1"

echo '启动xxl-admin'
cd $APP_HOME/pet-xxl-nacos/pet-xxl-admin/
# shellcheck disable=SC2086
sh start.sh $1

echo '启动xxl-executor'
cd $APP_HOME/pet-xxl-nacos/pet-xxl-executor/
sh start.sh "$1"

echo '启动seata'
cd $APP_HOME/pet-visual/pet-seata-serve/
# shellcheck disable=SC2086
sh start.sh $1




