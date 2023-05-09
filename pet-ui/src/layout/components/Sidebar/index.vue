<template>
    <div :class="{'has-logo':showLogo}" :style="{ backgroundColor: settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
        <logo v-if="showLogo" :collapse="isCollapse" />
        <el-scrollbar :class="settings.sideTheme" wrap-class="scrollbar-wrapper">
            <el-input placeholder="请输入菜单名称" @input="change" v-model="route" style="margin: 0 5px;width: 190px"></el-input>
            <el-menu
                :default-active="activeMenu"
                :collapse="isCollapse"
                :background-color="settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground"
                :text-color="settings.sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
                :unique-opened="opened"
                :active-text-color="settings.theme"
                :collapse-transition="false"
                mode="vertical"
                :default-openeds="defaultOpeneds"
            >
                <sidebar-item
                    v-for="(route, index) in sidebarRouters"
                    :key="route.path  + index"
                    :item="route"
                    :base-path="route.path"
                />
            </el-menu>
        </el-scrollbar>
    </div>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import Logo from "./Logo";
import SidebarItem from "./SidebarItem";
import variables from "@/assets/styles/variables.scss";
import getters from "../../../store/getters";
import path from "path";
import { isExternal } from '@/utils/validate'
export default {
    components: { SidebarItem, Logo },
    computed: {
        ...mapState(["settings"]),
        ...mapGetters([ "sidebar"]),
        activeMenu() {
            const route = this.$route;
            const { meta, path } = route;
            // if set path, the sidebar will highlight the path you set
            if (meta.activeMenu) {
                return meta.activeMenu;
            }
            return path;
        },
        showLogo() {
            return this.$store.state.settings.sidebarLogo;
        },
        variables() {
            return variables;
        },
        isCollapse() {
            return !this.sidebar.opened;
        }
    },
    data() {
      return {
        route: "",
        sidebarRouters: this.$store.getters.sidebarRouters,
        opened: false,
        defaultOpeneds: []
      }
    },
    methods:{
      deepClone(obj) {
        let _obj = JSON.stringify(obj) //  对象转成字符串
        let objClone = JSON.parse(_obj) //  字符串转成对象
        return objClone
      },
      change(value){
          this.route = value;
          console.log(value)
        if(!value){
          this.opened = true
          this.sidebarRouters = this.deepClone(this.$store.getters.sidebarRouters)
          this.defaultOpeneds = []
          console.log(this.sidebarRouters)
          return
        }
        let allroute = this.deepClone(this.$store.getters.sidebarRouters)
        console.log(allroute)
        for (let i=0;i<allroute.length;i++){
          if(allroute[i].hidden == false){
            // 不包含查询的菜单
            if(allroute[i].meta.title&&allroute[i].meta.title.indexOf(value)==-1){
              if(allroute[i].children.length>0){
                let k = 0;
                console.log(allroute[i])
                for (let j=0;j<allroute[i].children.length;j++){
                  if(allroute[i].children[j].meta.title.indexOf(value)==-1){
                    k = k + 1
                    allroute[i].children[j].hidden = true
                  }
                }
                if(k == allroute[i].children.length){
                  allroute[i].hidden = true
                }
              }else{

                allroute[i].hidden = true
              }
            }
          }
          console.log(allroute[i].path)
          if(allroute[i].path){

            this.defaultOpeneds.push(this.resolvePath(allroute[i].path))
          }

        }
        this.opened = false
        this.sidebarRouters = allroute

        this.$forceUpdate()
        console.log(this.sidebarRouters)
      },
      resolvePath(routePath, routeQuery) {
        if (isExternal(routePath)) {
          return routePath
        }
        if (isExternal(this.basePath)) {
          return this.basePath
        }
        if (routeQuery) {
          let query = JSON.parse(routeQuery);
          return { path: path.resolve(this.basePath, routePath), query: query }
        }
        return path.resolve(this.basePath, routePath)
      }
    },
    watch:{
      route(){

      }
    }
};
</script>
