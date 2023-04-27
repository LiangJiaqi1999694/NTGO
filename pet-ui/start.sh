#!/bin/sh
APP_HOME=`pwd`
cd $APP_HOME

npm install --registry=https://registry.npmmirror.com
npm run build:prod

rm -rf /mnt/nginx/html/pet-ui

cp -r ./dist /mnt/nginx/html/pet-ui

docker restart nginx

