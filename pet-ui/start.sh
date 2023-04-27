#!/bin/sh
APP_HOME=`pwd`
cd $APP_HOME/pet-ui

npm install --registry=https://registry.npmmirror.com
npm run build:prod



