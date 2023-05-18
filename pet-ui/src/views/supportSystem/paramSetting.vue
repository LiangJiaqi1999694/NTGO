<template>
    <div>
      <el-row :gutter="20">
        <el-col :span="12" :offset="6">
          <el-radio-group v-model="active" class="radio-top">
            <el-radio label="1" border style="margin:0;">1.任务参数</el-radio>
            <div style="display:inline-block;"><span class="line-block"></span></div>
            <el-radio label="2" border>2.输入参数</el-radio>
          </el-radio-group>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top:30px;" v-if="active=='1'">
        <el-form :model="formParams" ref="queryForm" :rules="rules" label-width="120px" class="form-dialog">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="任务名称：" prop="jobDesc">
                <el-input v-model="formParams.jobDesc" placeholder="请输入任务名称"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="预警邮箱：" prop="alarmEmail">
                <el-input v-model="formParams.alarmEmail" placeholder="请输入预警邮箱"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="路由策略：" prop="executorRouteStrategy">
                  <el-select v-model="formParams.executorRouteStrategy" placeholder="请选择">
                    <el-option v-for="item in routerArray" :key="item.value" :value="item.value" :label="item.label">
                    </el-option>
                  </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="Cron：" prop="jobCron">
                <el-popover v-model="cronPopover">
                  <vueCron @change="changeCron" @close="cronPopover=false" i18n="cn"/>
                  <el-input slot="reference" v-model="formParams.jobCron" @click="cronPopover=true"/>
                </el-popover>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="插件分类：" prop="jobType">
                <el-select v-model="formParams.jobType" placeholder="请选择" @change="selectalgosFun">
                  <el-option v-for="item in jobTypeArray" :key="item.name" :value="item.id" :label="item.name">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="插件选择：" prop="algoId">
                <el-select v-model="formParams.algoId" placeholder="请选择" @change="selectbyidFun">
                  <el-option v-for="item in pluginArray" :key="item.id" :value="item.id" :label="item.name">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="任务超时时间：" prop="executorTimeout">
                <el-input v-model="formParams.executorTimeout" placeholder="请输入任务超时时间"/>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="失败重复次数" prop="executorFailRetryCount">
                <el-input v-model="formParams.executorFailRetryCount" placeholder="请输入失败重复次数"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="子任务ID：" prop="childJobId">
                <el-input v-model="formParams.childJobId" placeholder="请输入子任务任务ID,如存在多个则逗号隔开"/>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-row>
      <el-row :gutter="20" style="margin-top:30px;" v-if="active=='2'">
        <el-table :data="tableList" class="table-style" height="263">
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
      </el-row>
    </div>
</template>


<script>
    import {selectalgos,selectbyid} from "@/api/pugin.js";
    export default {
        name: "paramSetting",
        props: {
            DataParam: {
                type: Object,
                default: function () {
                    return {};
                }
            },
        },
        data(){
            return{
                active:'1',
                tableList:[],
                formParams:{jobDesc:'',alarmEmail:'',executorRouteStrategy:'',jobCron:'',jobType:'',algoId:'',executorTimeout:'',executorFailRetryCount:'',childJobId:''},
                jobTypeArray:[],
                routerArray:[],
                pluginArray:[],
                cronPopover:false,
                rules: {
                  jobDesc: [
                    { required: true, message: '请输入任务描述', trigger: 'change' }
                  ],
                  algoId: [
                    { required: true, message: '请输入插件', trigger: 'change' }
                  ],
                  jobType: [
                    { required: true, message: '请输入插件分类', trigger: 'change' }
                  ],
                  jobCron: [
                    { required: true, message: '请设置Cron', trigger: 'change' }
                  ],
                  executorRouteStrategy: [
                    { required: true, message: '请设置路由策略', trigger: 'change' }
                  ],
                }
            }
        },
        watch:{
            DataParam:{
                handler(newVal, oldVal) {
                  console.log(newVal)
                    this.jobTypeArray=newVal.jobTypeArray
                    this.routerArray=newVal.routerArray
                    if(newVal.type=='edit'){
                        this.formParams={
                            jobDesc:newVal.row.jobDesc,
                            alarmEmail:newVal.row.alarmEmail,
                            executorRouteStrategy:newVal.row.executorRouteStrategy,
                            jobCron:newVal.row.jobCron,
                            jobType:newVal.row.jobType,
                            algoId:'',
                            id: newVal.row.id,
                            executorTimeout:newVal.row.executorTimeout,
                            executorFailRetryCount:newVal.row.executorFailRetryCount
                        }
                        let params= {
                            categoryId: newVal.row.jobType,
                            pageNum: 1,
                            pageSize: 10000000,
                        }
                        selectalgos(params).then(res =>{
                            if (res.code == 200) {
                                this.pluginArray = res.content.list;
                                this.formParams.algoId=newVal.row.algoId;
                                this.formParams.id=newVal.row.id;
                                this.selectbyidFun(this.formParams.algoId,this.formParams.id)
                            }
                        })
                    }else{
                        this.formParams = {jobDesc:'',alarmEmail:'',executorRouteStrategy:'',jobCron:'',jobType:'',algoId:'',executorTimeout:'',executorFailRetryCount:''}
                    }
                },
                deep: true,
                immediate: true
            }
        },
        methods:{
            getParams(){
              let param={
                  alarmEmail: this.formParams.alarmEmail,
                  algoId: this.formParams.algoId,
                  executorFailRetryCount: this.formParams.executorFailRetryCount,
                  executorRouteStrategy: this.formParams.executorRouteStrategy,
                  executorTimeout: this.formParams.executorTimeout,
                  id: this.formParams.id,
                  jobCron: this.formParams.jobCron,
                  jobDesc: this.formParams.jobDesc,
                  jobParams: this.tableList,
                  jobType: this.formParams.jobType,
                  productId: "",
              }
              return param
            },
            selectalgosFun(id){
              let params= {
                categoryId: id,
                pageNum: 1,
                pageSize: 10000000,
              }
              selectalgos(params).then(res =>{
                  if (res.code == 200) {
                    if(res.content && res.content.list.length > 0){
                      this.pluginArray = res.content.list;
                    }else{
                      this.pluginArray = [];
                    }

                  } else {
                    this.$message({
                      message: res.msg,
                      type: "error"
                    });
                    this.pluginArray = [];
                  }
              })
            },
            changeCron(val){
                this.formParams.jobCron=val;
            },
            selectbyidFun(algoId,id){
              let formData = new FormData();
              formData.append("algoid", algoId);
              if(id){
               formData.append("id", id);

              }
              selectbyid(formData).then(res=>{
                  if (res.code == 200) {
                    this.tableList = res.content.jobParams;
                  } else {
                    this.$message({
                      message: res.msg,
                      type: "error"
                    });
                  }
              })
            },
            closeAll(){
                this.resetForm('queryForm');
            }
        }
    }
</script>

<style scoped>
  .form-dialog>>>.el-input__inner{width:200px;height: 32px;line-height: 32px;margin-bottom:10px;}
  .line-block{width:60px;height:2px;background-color:#1bc34f;display:block;}
  .radio-top>>>.el-radio__input.is-checked .el-radio__inner{border-color: #1bc34f;background: #1bc34f;}
  .radio-top>>>.el-radio.is-bordered.is-checked{border-color:#1bc34f}
  .radio-top>>>.el-radio__input.is-checked + .el-radio__label{color:#1bc34f}
  .radio-top>>>.el-radio--medium.is-bordered{border-radius:10px;}
</style>
