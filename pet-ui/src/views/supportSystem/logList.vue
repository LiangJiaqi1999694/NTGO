<template>
  <div class="log" style="overflow:auto;height:100%">
    <el-container>
      <el-header>
        <div style="width: 100%; height: 100%; background-color:#0ca090;line-height:60px;">
          <span style="color:#fff;font-size:20px;font-weight:bold;padding-left:10px;">执行日志Console</span>
        </div>
      </el-header>
      <el-main>
        <div v-html="center" class="main-div"></div>
      </el-main>
    </el-container>
  </div>
</template>
<script>
    import { logDetailCat } from "@/api/supportSystem/log";
    export default {
        data(){
            return {
                timer:null,
                center:"",
                fromLineNum:1,
                end:true,
            }
        },
        watch:{
            end(val){
                if(val==false){
                    setInterval(this.timer);
                }
            }
        },
        mounted(){
            //添加定时器 2秒 只想接口
            this.timer = setInterval(() => {
                setTimeout(() => {
                    this.logDetailCatFun()
                }, 0)
            }, 2000)
        },
        methods:{
            logDetailCatFun(){
                let params = {
                    logId: this.$route.query.id,
                    fromLineNum:this.fromLineNum,
                };
                logDetailCat(params).then(res =>{
                    if(res.code == 200){
                        this.center += res.content.logContent;
                        this.fromLineNum = res.content.toLineNum +1
                        if(!res.content.end){
                            console.log(res.content.end)
                            this.end= false;
                        }
                    }
                })
            }
        },
        beforeDestroy() {
            setInterval(this.timer);
        }
    }
</script>
