<template>
    <div class="app-container">
      <div v-if="!showLogTurn">
        <el-row :gutter="20">
          <el-col :span="24" :xs="24">
            <el-tabs v-model="jobType" type="card" @tab-click="searchData">
              <el-tab-pane v-for="item in jobTypeArray" :key="item.id" :label="item.name.toString()" :name="item.id.toString()"></el-tab-pane>
            </el-tabs>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24" :xs="24">
            <el-row :gutter="10" class="mb8">
              <el-input v-model="jobDesc" placeholder="按任务名称搜索" clearable size="small" @input="searchData" style="width:220px"/>
              <el-button @click="handleStop" class="button-top" style="margin-left:10px;">批量暂行</el-button>
              <el-button @click="handleStart" class="button-top">批量运行</el-button>
              <el-button @click="handleAdd" class="button-top">新增任务</el-button>
            </el-row>
            <el-table v-loading="loading" :data="systemList" class="table-style" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column label="序号" type="index" align="center">
                <template slot-scope="scope">
                  <span>{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</span>
                </template>
              </el-table-column>
              <el-table-column label="任务名称" align="left" prop="jobDesc" :show-overflow-tooltip="true"/>
              <el-table-column label="路由策略" align="left">
                <template slot-scope="scope"><span>{{showRouterName(scope.row.executorRouteStrategy)}}</span></template>
              </el-table-column>
              <el-table-column label="Jobhandler" align="left" prop="jobhandler" :show-overflow-tooltip="true" />
              <el-table-column label="Cron" align="left" prop="jobCron" width="120" :show-overflow-tooltip="true"/>
              <el-table-column label="状态" align="left" width="120">
                <template slot-scope="scope">
                  <button class="status-running" v-if="scope.row.triggerStatus==1">RUNNING</button>
                  <button class="status-stop" v-if="scope.row.triggerStatus==0">STOP</button>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="left" width="160" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.actionType" placeholder="选择操作">
                    <el-option v-for="item in actionsArray" :key="item.value" :value="item.value" :label="item.label"
                               :style="{'display':(item.value=='4'&&scope.row.triggerStatus==0)||(item.value=='5'&&scope.row.triggerStatus==1)?'none':'block'}"
                               @click.native="handlerAction(scope.row)">
                    </el-option>
                  </el-select>
                </template>
              </el-table-column>
            </el-table>
            <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="searchData"/>
          </el-col>
        </el-row>
      </div>
      <showLog v-if="showLogTurn" ref="showLogRef" :DataArray="taskData" @backToHome="backToHome"></showLog>
      <el-dialog :visible.sync="timeOpen" width="400px" append-to-body class="dialog-style">
        <div slot="title"><i class="el-icon-warning-outline" style="color:#faad14;"></i><span>下次执行时间</span></div>
        <template v-for="(item,index) in content">
          <span :key="index">{{item}}</span><br/>
        </template>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="timeOpen=false">确定</el-button>
          <el-button @click="timeOpen=false">取消</el-button>
        </div>
      </el-dialog>
      <el-dialog :title="titleShow" :visible.sync="paramSetOpen" width="700px" style="height:700px;" append-to-body>
        <paramSetting :DataParam="dataParam" ref="paramSettingRef"></paramSetting>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="saveTask">保存</el-button>
          <el-button @click="paramSetOpen=false">取消</el-button>
        </div>
      </el-dialog>
      <el-dialog :visible.sync="deleteOpen" width="200px" append-to-body>
        <div slot="title"><i class="el-icon-warning-outline" style="color:#faad14;"></i><span>确认删除此任务？</span></div>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirmDelete">确认</el-button>
        </div>
      </el-dialog>
    </div>
</template>

<script>
    import { jobPageList,batchStart,batchStop,saveTask,triggerTask,nextTriggerTime,delTask,startTask,stopTask } from "@/api/supportSystem/log";
    import {selectallcategorys,} from "@/api/pugin.js";
    import showLog from './showLog.vue'
    import paramSetting from './paramSetting.vue'
    export default {
        name: "index",
        components:{showLog,paramSetting},
        data(){
            return{
                titleShow:'编辑任务',
                jobDesc:'',
                queryParams:{
                    pageNum:1,
                    pageSize:10
                },
                taskData:{},
                total:0,
                loading:false,
                jobType:'1',
                timeOpen:false,
                deleteOpen:false,
                paramSetOpen:false,
                jobTypeArray:[],
                actionsArray:[{value:'1',label:'执行一次'},{value:'2',label:'查看日志'},{value:'3',label:'下次执行时间'},{value:'4',label:'停止'},{value:'5',label:'启动'},
                    {value:'6',label:'编辑'},{value:'7',label:'删除'},{value:'8',label:'复制'}],
                systemList:[],
                showLogTurn:false,
                content:[],
                dataParam:{},
                routerArray:[{value:'FIRST',label:'第一个'},{value:'LAST',label:'最后一个'},{value:'ROUND',label:'轮询'},
                    {value:'RANDOM',label:'随机'},{value:'CONSISTENT_HASH',label:'一致性HASH'},{value:'LEAST_FREQUENTLY_USED',label:'最不经常使用'},
                    {value:'LEAST_RECENTLY_USED',label:'最近最久未使用'},{value:'FAILOVER',label:'故障转移'},{value:'SHARDING_BROADCAST',label:'分片广播'}],
                multiSelects:[],
                clickItem:{},
            }
        },
        created(){
            this.selectallcategorysFun();
        },
        methods:{
            selectallcategorysFun() {
                let params = {
                    type: 2
                };
                selectallcategorys(params).then(res => {
                    if (res.code == 200) {
                        this.jobTypeArray = res.content;
                        this.jobType=this.jobTypeArray[0].id.toString();
                        this.searchData();
                    } else {
                        this.$message({
                            message: res.msg,
                            type: "error"
                        });
                    }
                });
            },
            searchData(){
                let params={
                    start:this.queryParams.pageNum,
                    length:this.queryParams.pageSize,
                    // triggerStatus:'',
                    jobDesc:this.jobDesc||undefined,
                    jobType:this.jobType||undefined,
                    // categoryId:''
                }
                jobPageList(params).then(res=>{
                  this.total = res.recordsTotal;
                  this.systemList=res.data
                })
            },
            handlerAction(row){
                this.clickItem=row
                let id={id:row.id}
                if(row.actionType=='4'||row.actionType=='5'){//启动，停止
                    if(row.actionType=='4'){//停止
                        stopTask(id).then(res=>{
                            if (res.code == 200) {
                                this.searchData()
                                this.$message({message: '停止任务', type: "success"});
                            }else{
                                this.$message({message: res.msg, type: "error"});
                            }
                        })
                    }else if(row.actionType=='5'){//启动
                        startTask(id).then(res=>{
                            if (res.code == 200) {
                                this.searchData()
                                this.$message({message: '启动任务', type: "success"});
                            }else{
                                this.$message({message: res.msg, type: "error"});
                            }
                        })
                    }
                }else if(row.actionType=='2'){//查看日志
                    this.showLogTurn=true
                    this.taskData={
                        taskArray:this.systemList,
                        taskCode:row.id,
                        time:row.triggerLastTime
                    }
                    console.log(this.taskData)
                }else if(row.actionType=='3'){//下次执行时间
                    let param={
                        cron:row.jobCron
                    }
                    nextTriggerTime(param).then(res=>{
                        if (res.code == 200) {
                            this.content=res.content
                            this.timeOpen=true
                        }else{
                            this.$message({message: res.msg, type: "error"});
                        }
                    })
                }else if(row.actionType=='6'){//编辑
                    if(row.triggerStatus==1){//运行中
                        this.$message({message: '运行中，无法编辑'});
                    }else{
                        this.paramSetOpen=true
                        this.dataParam={
                            routerArray:this.routerArray,
                            jobTypeArray:this.jobTypeArray,
                            row:row,
                            type:'edit'
                        }
                    }
                }else if(row.actionType=='7'){//删除
                    if(row.triggerStatus==1){//运行中
                        this.$message({message: '运行中，无法删除'});
                    }else{
                        this.deleteOpen=true
                    }
                }else if(row.actionType=='1'){//执行一次
                    let param={
                        id:row.id,
                        // executorParam:'',
                        // addressList:''
                    }
                    triggerTask(param).then(res=>{
                        if (res.code == 200) {
                            this.searchData()
                            this.$message({message: '操作成功', type: "success"});
                        }else{
                            this.$message({message: res.msg, type: "error"});
                        }
                    })

                }else if(row.actionType=='8'){//复制
                    this.paramSetOpen=true
                    this.dataParam={
                        routerArray:this.routerArray,
                        jobTypeArray:this.jobTypeArray,
                        row:row,
                        type:'edit',
                        copy:true
                    }
                }
            },
            confirmDelete(){
                delTask(this.clickItem.id).then(res=>{
                    if (res.code == 200) {
                        this.searchData()
                        this.deleteOpen = false
                        this.$message({message: '删除成功', type: "success"});

                    }else{
                        this.$message({message: res.msg, type: "error"});
                    }
                })
            },
            backToHome(){
                this.showLogTurn=false
                this.systemList.forEach(item=>item.actionType='')
            },
            handleSelectionChange(val){
                this.multiSelects=val.map(item=>item.id)
            },
            handleAdd(){
                this.paramSetOpen=true
                this.dataParam={
                    routerArray:this.routerArray,
                    jobTypeArray:this.jobTypeArray,
                    type:'add'
                }
            },
            handleStart(){
                batchStart(this.multiSelects).then(res=>{
                    if (res.code == 200) {
                        this.searchData()
                        this.$message({message: '操作成功', type: "success"});
                    }else{
                        this.$message({message: res.msg, type: "error"});
                    }
                })
            },
            saveTask(){
                this.paramSetOpen=false
                let param=this.$refs.paramSettingRef.getParams()
                if(this.dataParam.copy){
                    delete param.id;
                }
                saveTask(param).then(res=>{
                    if (res.code == 200) {
                        this.searchData()
                        this.$message({message: res.msg, type: "success"});
                        this.$refs.paramSettingRef.closeAll()
                    }else{
                        this.$message({message: res.msg, type: "error"});
                    }
                })
            },
            handleStop(){
                batchStop(this.multiSelects).then(res=>{
                    if (res.code == 200) {
                        this.searchData()
                        this.$message({message: '操作成功', type: "success"});
                    }else{
                        this.$message({message: res.msg, type: "error"});
                    }
                })
            },
            showRouterName(val){
                let label=this.routerArray.filter(item=>item.value==val)[0].label
                return label
            }
        }
    }
</script>

<style scoped>
  .button-top{float:right;height: 32px;line-height: 32px;padding: 0 16px;border-radius: 8px;}
  .status-running{width: 90px;display: block;text-align: center;border-radius: 4px;background-color: #32ab42;color: #fff;border: 0;height: 25px;line-height: 25px;}
  .status-stop{width: 60px;display: block;text-align: center;border-radius: 4px;background-color: #dcdcdc;border: 0;height: 25px;line-height: 25px;}
  /deep/.el-dialog__header {
      background: rgba(0, 0, 0, 0.1);
    }
    /deep/.el-dialog {
      border-radius: 5px;
    }
</style>
