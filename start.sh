echo '开始编译'

mvn clean install

echo '编译完成'

echo '启动网关中'
./pet-gateway/start.sh

echo '网关启动结束'

echo '启动gen'

./pet-modules/pet-gen/start.sh

echo '启动resource'
./pet-modules/pet-resource/start.sh

echo '启动system'
./pet-modules/pet-system/start.sh

echo '启动xxl-admin'
./pet-modules/pet-xxl-admin/start.sh

echo '启动xxl-executor'
./pet-xxl-nacos/pet-xxl-executor/start.sh


