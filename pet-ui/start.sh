#!/bin/sh
APP_HOME=`pwd`
cd $APP_HOME

npm install --registry=https://registry.npmmirror.com
npm run build:"$1"

rm -rf /mnt/nginx/html/pet-ui

cp -r ./dist /mnt/nginx/html/pet-ui

cp $APP_HOME/pet-ui.conf /mnt/nginx/conf.d
docker restart nginx

