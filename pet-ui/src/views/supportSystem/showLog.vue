<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24" :xs="24">
        <el-row :gutter="10" class="mb8">
          <span>任务：</span>
          <el-select v-model="queryParams.taskCode" class="selection-query" style="margin-right:10px;">
            <el-option v-for="item in taskArray" :key="item.id" :value="item.id" :label="item.jobDesc">
            </el-option>
          </el-select>
          <span>状态：</span>
          <el-select v-model="queryParams.status" class="selection-status" style="margin-right:10px;">
            <el-option v-for="item in statusArray" :key="item.value" :value="item.value" :label="item.label">
            </el-option>
          </el-select>
          <span>调度时间：</span>
          <el-date-picker v-model="queryParams.time" class="selection-time" type="datetimerange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期">
          </el-date-picker>
          <el-button @click="toBack" class="button-top" style="margin-left:10px;" round>返回</el-button>
          <el-button @click="handleClear" class="button-top" round>清理</el-button>
          <el-button @click="searchData" class="button-top" round>搜索</el-button>
        </el-row>
        <el-table v-loading="loading" :data="systemList" class="table-style">
          <el-table-column label="序号" type="index" align="center">
            <template slot-scope="scope">
              <span>{{(queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1}}</span>
            </template>
          </el-table-column>
          <el-table-column label="任务名称" align="left" prop="jobDesc" :show-overflow-tooltip="true"/>
          <el-table-column label="调度时间" align="left" prop="triggerTime" :show-overflow-tooltip="true"/>
          <el-table-column label="调度结果" align="left">
            <template slot-scope="scope">
              <span class="resultCode">{{scope.row.triggerCode==200?'成功': scope.row.triggerCode==500?'失败':''}}</span>
            </template>
          </el-table-column>
          <el-table-column label="调度备注" align="left" width="120">
            <template slot-scope="scope">
              <span class="view-tip" @click="triggerTip(scope.row.triggerMsg)">查看</span>
            </template>
          </el-table-column>
          <el-table-column label="执行时间" align="left" prop="handleTime" :show-overflow-tooltip="true" />
          <el-table-column label="执行结果" align="left">
            <template slot-scope="scope">
              <span class="resultCode">{{scope.row.triggerCode==500?"":scope.row.handleCode==200?'成功':
                scope.row.handleCode==500?'失败':
                scope.row.handleTime!=null?"执行中":"等待执行"}}</span>
            </template>
          </el-table-column>
          <el-table-column label="执行备注" align="left" width="120">
            <template slot-scope="scope">
              <span class="view-tip" @click="triggerTip(scope.row.handleMsg)">查看</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="left" width="160" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-select v-model="scope.row.actionType" placeholder="选择操作">
                <el-option v-for="item in actionsArray" :key="item.value" :value="item.value"
                :label="item.label" @click.native="handlerAction(scope.row)">
                </el-option>
                <el-option value="4"
                 v-if="scope.row.handleCode === 0"
                label="终止日志" @click.native="handlerAction(scope.row)">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="searchData"/>
      </el-col>
    </el-row>
    <el-dialog title="备注信息" :visible.sync="msgOpen" width="500px" append-to-body>
      <div v-html="msgShow"></div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="msgOpen=false">确认</el-button>
      </div>
    </el-dialog>
    <el-dialog title="子任务日志" :visible.sync="sonTaskOpen" width="800px" append-to-body>
      <el-table :data="sonSystemList" class="table-style">
        <el-table-column label="序号" type="index" align="center">
          <template slot-scope="scope">
            <span>{{(pageNum - 1) * pageSize + scope.$index + 1}}</span>
          </template>
        </el-table-column>
        <el-table-column label="执行参数" align="left" width="120">
          <template slot-scope="scope">
            <span class="view-tip" @click="triggerTip(scope.row.executeParam)">查看</span>
          </template>
        </el-table-column>
        <el-table-column label="执行时间" align="left" prop="handleStartTime" :show-overflow-tooltip="true"/>
        <el-table-column label="执行耗时(ms)" align="left" prop="handleCostTime" :show-overflow-tooltip="true"/>
        <el-table-column label="执行结果" align="left">
          <template slot-scope="scope">
            <span class="resultCode">{{scope.row.handleCode==200?'成功':'失败'}}</span>
          </template>
        </el-table-column>
        <el-table-column label="执行备注" align="left" width="120">
          <template slot-scope="scope">
            <span class="view-tip" @click="triggerTip(scope.row.handleMsg)">查看</span>
          </template>
        </el-table-column>
        <el-table-column label="执行时间" align="left" prop="handleTime" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="left" width="120">
          <template slot-scope="scope">
            <span class="view-tip" @click="doTask(scope.row)">重做</span>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="pageNum" :limit.sync="pageSize" @pagination="searchTable"/>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="sonTaskOpen=false">确定</el-button>
        <el-button @click="sonTaskOpen=false">取消</el-button>
      </div>
    </el-dialog>
    <el-dialog title="重做参数" :visible.sync="paramOpen" width="800px" append-to-body>
      <el-table :data="tableList" class="table-style" height="350">
        <el-table-column label="序号" type="index" align="center"></el-table-column>
        <el-table-column label="参数名称" align="left" prop="paramDescription" :show-overflow-tooltip="true"/>
        <el-table-column label="参数标识" align="left" prop="paramName" :show-overflow-tooltip="true" />
        <el-table-column label="参数类型" align="left" prop="paramType" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="left" width="160" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-input v-model="scope.row.paramValue"/>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="paramDo">确认</el-button>
        <el-button @click="paramOpen=false">取消</el-button>
      </div>
    </el-dialog>
    <el-dialog title="清除日志" :visible.sync="clearOpen" width="500px" append-to-body>
      <el-row :gutter="20">
        <el-col :span="24" :xs="24">
          <span>清理方式：</span>
          <el-select v-model="clearCode" class="clear-query">
            <el-option v-for="item in clearType" :key="item.id" :value="item.id" :label="item.label">
            </el-option>
          </el-select>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="clearConfirm">确认</el-button>
        <el-button @click="clearOpen=false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
    import { jobLogList,deleteLog,pageSonTasksList,redo,clearLog,logKill } from "@/api/supportSystem/log";
    import {selectbyid} from "@/api/pugin.js";
    import moment from 'moment'

    export default {
        props: {
            DataArray: {
                type: Object,
                default: function () {
                    return {};
                }
            },
        },
        data(){
            return{
                msgShow:'',
                msgOpen:false,
                jobDesc:'',
                queryParams:{
                    pageNum:1,
                    pageSize:10,
                    taskCode:'',
                    status:'-1',
                    time:''
                },
                pageNum:1,
                pageSize:6,
                total:2,
                loading:false,
                sonTaskOpen:false,
                paramOpen:false,
                taskArray:[],
                statusArray:[{value:'-1',label:'全部'},{value:'1',label:'成功'},{value:'2',label:'失败'},{value:'3',label:'进行中'}],
                actionsArray:[{value:'1',label:'执行日志'},{value:'2',label:'删除日志'},{value:'3',label:'子任务日志'}],
                systemList:[],
                showLogTurn:false,
                sonSystemList:[],
                clickRow:{},
                tableList:[],
                clearOpen:false,
                clearCode:'',
                clearType:[{id: 1,label: '清理一个月之前日志数据'},
                  {id: 2,label: '清理三个月之前日志数据'},
                  {id: 3,label: '清理六个月之前日志数据'},
                  {id: 4,label: '清理一年之前日志数据'},
                  {id: 5,label: '清理一千条以前日志数据'},
                  {id: 6,label: '清理三万条以前日志数据'},
                  {id: 7,label: '清理三万条以前日志数据'},
                  {id: 8,label: '清理十万条以前日志数据'},
                  {id: 9,label: '清理所有日志数据'}]
            }
        },
        watch:{
            DataArray:{
                handler(newVal, oldVal) {
                    this.queryParams.taskCode=newVal.taskCode
                    this.taskArray=newVal.taskArray
                  console.log(new Date(newVal.time))
                    this.queryParams.time=[moment(new Date(newVal.time)).format('YYYY-MM-DD 00:00:00'),moment(new Date(newVal.time)).format('YYYY-MM-DD 23:59:59')]
                   this.searchData()
                },
                deep: true,
                immediate: true
            }
        },
        methods:{
            searchData(){
                let param={
                    start:this.queryParams.pageNum,
                    length:this.queryParams.pageSize,
                    // jobGroup:'',
                    jobId:this.queryParams.taskCode,
                    logStatus:this.queryParams.status,
                    startTime:this.queryParams.time[0],
                    endTime:this.queryParams.time[1]
                }
                jobLogList(param).then(res=>{
                    this.systemList=res.data
                    this.total=res.recordsTotal
                })
            },
            triggerTip(triggerMsg){
               this.msgOpen=true;
               if(triggerMsg){
                   this.msgShow=triggerMsg
               }else{
                   this.msgShow='无'
               }
            },
            handlerAction(row){
                this.clickRow=row
                if(row.actionType=='1'){//执行日志
                    let routeUrl = this.$router.resolve({
                        path: "/logList",
                        query: {
                            id:row.id
                        }
                    });
                    window.open(routeUrl.href, '_blank');
                }else if(row.actionType=='2'){//删除日志
                    let param={
                        jobLogId: Number(row.id)
                    }
                    deleteLog(param).then(res=>{
                        if (res.code == 200) {
                            this.searchData()
                            this.$message({message: '删除成功', type: "success"});
                        }else{
                            this.$message({message: res.msg, type: "error"});
                        }
                    })
                }else if(row.actionType=='3'){//子任务日志
                    this.sonTaskOpen=true;
                    this.searchTable();
                }
                else if(row.actionType=='4'){//终止日志
                    let param={
                        id: Number(row.id)
                    }
                    logKill(param).then(res=>{
                        if (res.code == 200) {
                            this.searchData()
                            this.$message({message: '终止成功', type: "success"});
                        }else{
                            this.$message({message: res.msg, type: "error"});
                        }
                    })
                }
            },
            searchTable(){
                let param={
                    logId: this.clickRow.id,
                    logStatus: this.queryParams.status,
                    start: this.pageNum,
                    length: this.pageSize,
                }
                pageSonTasksList(param).then(res=>{
                    this.sonSystemList=res.data
                })
            },
            doTask(row){
                this.paramOpen=true
                let formData = new FormData();
                formData.append("algoid", row.algoId);
                if(this.clickRow.jobId){
                  formData.append("id", this.clickRow.jobId);
                }
                this.handleLogId = row.id
                selectbyid(formData).then(res=>{
                    if (res.code == 200) {
                        this.tableList = res.content.jobParams;
                    } else {
                        this.$message({message: res.msg, type: "error"});
                    }
                })
            },
            paramDo(){
                let param={
                    handleLogId:this.handleLogId,
                    jobParams:this.tableList,
                }
                redo(param).then(res=>{
                    if (res.code == 200) {
                        this.$message({message: '提交成功', type: "success"});
                    } else {
                        this.$message({message: res.msg, type: "error"});
                    }
                })
            },
            handleClear(){
                this.clearOpen=true;
            },
            clearConfirm(){
                clearLog({jobId:this.queryParams.taskCode,type: this.clearCode,jobGroup:0}).then(res=>{
                    if (res.code == 200) {
                        this.clearOpen=false;
                        this.$message({message: '清除成功', type: "success"});
                        this.searchData()
                    } else {
                        this.$message({message: res.msg, type: "error"});
                    }
                })
            },
            toBack(){
              this.$emit('backToHome')
            }
        }
    }
</script>

<style scoped>
  .view-tip{color: #1890ff;text-decoration:underline;cursor:pointer;}
  .resultCode{color: #ff4d4d;}
  .button-top{float:right;height: 32px;line-height: 32px;padding: 0 16px;}
  .status-running{width: 90px;display: block;text-align: center;border-radius: 4px;background-color: #32ab42;color: #fff;border: 0;height: 25px;line-height: 25px;}
  .status-stop{width: 60px;display: block;text-align: center;border-radius: 4px;background-color: #dcdcdc;border: 0;height: 25px;line-height: 25px;}
  .selection-query>>>.el-input__inner{width:150px;height: 32px;line-height: 32px;}
  .selection-status>>>.el-input__inner{width:100px;height: 32px;line-height: 32px;}
  .selection-time.el-input__inner{height: 32px;line-height: 32px;width:300px;}
  .clear-query>>>.el-input__inner{width:260px;height: 32px;line-height: 32px;}
  /deep/.el-dialog__header {
      background: rgba(0, 0, 0, 0.1);
    }
    /deep/.el-dialog {
      border-radius: 5px;
    }
</style>
