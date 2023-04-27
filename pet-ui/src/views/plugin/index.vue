<template>
  <div class="plugin">
    <div class="plugin_main">
      <div class="plugin_left">
        <div class="plugin_nav">
          <span class="name">插件分组</span>
          <el-button type="text" icon="el-icon-plus" @click="dialogAddVisible = true "></el-button>
          <el-button type="text" icon="el-icon-delete" @click="deletecategoryFun"></el-button>
          <el-button type="text" icon="el-icon-edit-outline" @click="leftEditBtn"></el-button>
        </div>
        <el-tree :data="pluginData" ref="leftTree"  node-key="id" :current-node-key="currentId"   :highlight-current="true" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
        <!--弹出 编辑-->
        <el-dialog title="编辑分组" :width="width" :visible.sync="dialogFormVisible">
          <div class="from_item">
            <span class="name">分组名称：</span>
            <el-input type="text" placeholder v-model="leftEdit.name"></el-input>
          </div>
          <div class="from_item">
            <span class="name">所属分组：</span>
            <el-select v-model="leftEdit.parentId"  :disabled="disabled" placeholder="请选择">
              <el-option
                v-for="item in pluginData"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </div>

          <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="dialogFormVisible = false">取 消</el-button>
            <el-button size="small" type="primary" @click="leftEditFine">确 定</el-button>
          </div>
        </el-dialog>
        <!--弹出 编辑-->
        <el-dialog title="新增分组" :width="width" :visible.sync="dialogAddVisible">
          <div class="from_item">
            <span class="name">分组名称：</span>
            <el-input type="text" placeholder v-model="leftAdd.name"></el-input>
          </div>
          <div class="from_item">
            <span class="name">所属分组：</span>
            <el-select v-model="leftAdd.parentId" placeholder="请选择">
              <el-option
                v-for="item in pluginData"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </div>
          <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="dialogFormVisible = false">取 消</el-button>
            <el-button type="primary" size="small" @click="createcategoryFun">确 定</el-button>
          </div>
        </el-dialog>
      </div>
      <div class="plugin_right">
        <div class="plugin_nav">
          <el-input v-model="searchVal" placeholder="请输入插件名称搜索" @input="selectalgosFun(true)"></el-input>
          <el-button @click="addPluginBtn" round>添加插件</el-button>
        </div>
        <div class="table_box">
          <el-table :data="tableData" stripe>
            <el-table-column type="index" width="50"></el-table-column>
            <el-table-column prop="name" label="插件名称"></el-table-column>
            <el-table-column prop="jobhandler" label="插件JobHandler"></el-table-column>
            <el-table-column prop="state" label="状态" align="center">
              <template slot-scope="scope">
                {{ scope.row.state === 1 ? '正常':"停止"}}
              </template>
            </el-table-column>
            <el-table-column prop="createtime" label="创建时间"></el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button icon="el-icon-edit-outline" type="text" @click="editBtn(scope.row)"></el-button>
                <el-dropdown :hide-on-click="true">
                  <span class="el-dropdown-link">
                    <i class="el-icon-set-up"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <template v-for="dropItem in pluginData">
                      <el-dropdown-item>
                        <el-button
                          type="text"
                          @click="handleCommand(dropItem.id,scope.row)"
                        >{{dropItem.name}}</el-button>
                      </el-dropdown-item>
                    </template>
                  </el-dropdown-menu>
                </el-dropdown>
                <el-button icon="el-icon-delete" type="text" @click="deletealgoFun(scope.row.id)"></el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination background layout="prev, pager, next" :total="pageNums"></el-pagination>
        </div>
        <el-dialog :title="title" :visible.sync="addPluginVisible" width="800px">
          <el-tabs v-model="activeName" @tab-click="tabClick">
            <el-tab-pane label="模型参数" name="first">
              <el-form ref="form" :model="addPlug" label-width="120px">
                <el-form-item label="插件名称">
                  <el-input v-model="addPlug.name"></el-input>
                </el-form-item>
                <el-form-item label="插件JobHandler">
                  <el-input v-model="addPlug.jobhandler"></el-input>
                </el-form-item>
                <el-form-item label="阻塞处理策略">
                  <el-select v-model="addPlug.blockStrategy" placeholder="请选择">
                     <el-option
                        v-for="item in blockStrategys"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                      </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="状态">
                  <el-radio-group v-model="addPlug.state">
                    <el-radio :label="item.value" :key="item.value" v-for="item in isStatus">
                      {{item.label}}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="插件备注">
                  <el-input type="textarea" v-model="addPlug.description"></el-input>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="输入参数" name="second">
              <el-table :data="addPlug.params" current-row-key>
                <el-table-column type="index" width="50"></el-table-column>
                <el-table-column label="名称">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.paramDescription" type="text"></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="标识">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.paramName" type="text"></el-input>
                  </template>
                </el-table-column>
                <el-table-column label="参数类型">
                  <template slot-scope="scope">
                    <el-select
                      v-model="scope.row.paramType"
                      @change="paramTypeFun"
                      placeholder="请选择"
                    >
                      <el-option
                        v-for="typeItem in typeData"
                        :key="typeItem.val"
                        :label="typeItem.label"
                        :value="typeItem.val"
                      ></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="参数默认值">
                  <template slot-scope="scope">
                    <el-input
                      v-if="scope.row.paramType == 1"
                      v-model="scope.row.defaultValue"
                      type="text"
                    ></el-input>
                    <el-date-picker
                      v-else-if="scope.row.paramType == 2"
                      v-model="scope.row.defaultValue"
                      type="date"
                      placeholder="选择日期"
                    ></el-date-picker>
                    <el-select v-else v-model="scope.row.defaultValue" placeholder="请选择">
                      <el-option
                        v-for="selectItem in selectDictionary"
                        :key="selectItem.dicValue"
                        :label="selectItem.dictName"
                        :value="selectItem.dicValue"
                      ></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="重做" width="50">
                  <template slot-scope="scope" >
                    <el-checkbox v-model="scope.row.isRedo"></el-checkbox>
                  </template>
                </el-table-column>
                <el-table-column label="显示"  width="50">
                  <template slot-scope="scope">
                    <el-checkbox v-model="scope.row.isShow"></el-checkbox>
                  </template>
                </el-table-column>
                <el-table-column label="操作">
                  <template slot-scope="scope">
                    <el-button
                      icon="el-icon-delete"
                      type="text"
                      @click="tableDelete(scope.row.key)"
                    ></el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-button icon="el-icon-plus" type="text" @click="plusTable"></el-button>
            </el-tab-pane>
            <el-tab-pane label="部署节点" name="third">
              <el-table
                ref="tables"
                :data="pageList"
                highlight-current-row
                @current-change="handleCurrentChange"
              >
                <el-table-column type="index" width="50"></el-table-column>
                <el-table-column prop="appname" label="集群名称"></el-table-column>
                <el-table-column prop="addressType" label="注册方式">
                  <template slot-scope="scope">{{scope.row.addressType == 0 ? '自动注册':'手动注册'}}</template>
                </el-table-column>
                <el-table-column prop="addressList" label="OnLine机器地址"></el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
          <div slot="footer" class="dialog-footer">
            <el-button size="small" @click="addPluginVisible = false">取 消</el-button>
            <el-button size="small" type="primary" @click="savealgoFun">确 定</el-button>
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import {
  selectallcategorys,
  updatecategory,
  selectalgos,
  deletecategory,
  createcategory,
  deletealgo,
  pageList,
  selectDataDictionary,
  savealgo,selectalgosbyid
} from "@/api/pugin.js";
import pako from  'pako'

export default {
  name: "Config",
  data() {
    return {
      currentId:0,
      searchVal: "",
      width: "400px",
      activeName: "first",
      title: "新增分组",
      paramsList: [],
      selectDictionary: [],
      blockStrategys:[
        { label: "单机并行", value: 1 },
        { label: "单机串行", value: 0 }
      ],
      typeData: [
        { label: "字符串", val: 1 },
        { label: "日期", val: 2 },
        // { label: "枚举", val: 3 }
      ],
      isStatus: [
        { label: "正常", value: 1 },
        { label: "停止", value: 0 }
      ],
      pageList: [],
      addPluginVisible: false,
      addPlug: {},
      pageNum: 1,
      pageNums: 0,
      dialogFormVisible: false,
      dialogAddVisible: false,
      disabled: false,
      leftEdit: {
        name: "",
        parentId: ""
      },
      leftAdd: {
        name: "",
        parentId: ""
      },
      pluginData: [],
      defaultProps: {
        children: "sonMenuCategorys",
        label: "name"
      },
      tableData: []
    };
  },
  created() {
    this.selectallcategorysFun(); //默认数据
    this.pageListFun();
  },
  methods: {
    /** 左侧 */
    selectallcategorysFun() {
      let params = {
        type: 2
      };
      selectallcategorys(params).then(res => {
        if (res.code == 200) {
          this.pluginData = res.content;
          let _this = this;

          this.$nextTick(()=>{
            this.currentId = _this.pluginData[0].id;
          })

          this.leftEdit = {
            id: this.pluginData[0].id,
            name: this.pluginData[0].name,
            type: this.pluginData[0].type,
            parentId: this.pluginData[0].parentId
          };
          this.selectalgosFun();
          this.$nextTick(() => {
              document.querySelector('.el-tree-node__content').click()
          })
        } else {
          this.$message({
            message: res.msg,
            type: "error"
          });
        }
      });
    },
    /** 左侧编辑 */
    handleNodeClick(data) {
      // this.eftEditData = data;
      this.leftEdit.name = data.name;
      this.leftEdit.type = data.type;
      this.leftEdit.parentId = data.parentId;
      this.leftEdit.id = data.id;
      if (data.parentId == "0") {
        this.leftEdit.parentId = 0;
        this.disabled = true;
      } else {
        this.leftEdit.parentId = data.parentId;
        this.disabled = false;
      }
      this.selectalgosFun();
    },
    leftEditBtn() {
      this.dialogFormVisible = true;
    },
    /***左侧编辑 确定 */
    leftEditFine() {
      let formData = new FormData();
      formData.append("id", this.leftEdit.id);
      formData.append("name", this.leftEdit.name);
      formData.append("type", this.leftEdit.type);
      formData.append("parentId", this.leftEdit.parentId);
      updatecategory(formData).then(res => {
        if (res.code == 200) {
          this.$message({
            message: res.msg,
            type: "success"
          });
          this.selectallcategorysFun();
          this.dialogFormVisible = false;
        } else {
          this.$message({
            message: res.msg,
            type: "error"
          });
        }
      });
    },
    deletecategoryFun() {
      this.$confirm('确认关闭？')
          .then(_ => {
            deletecategory(this.leftEdit.id).then(res => {
          if (res.code == 200) {
            this.$message({
              message: res.msg,
              type: "success"
            });
            this.selectallcategorysFun();
          } else {
            this.$message({
              message: res.msg,
              type: "error"
            });
          }
        });
          })
          .catch(_ => {});
    },
    createcategoryFun() {
      let formData = new FormData();
      formData.append("name", this.leftAdd.name);
      formData.append("type", 2);
      formData.append("parentId", this.leftAdd.parentId);
      createcategory(formData).then(res => {
        if (res.code == 200) {
          this.$message({
            message: res.msg,
            type: "success"
          });
          this.selectallcategorysFun();
          this.dialogAddVisible();
        } else {
          this.$message({
            message: res.msg,
            type: "error"
          });
        }
      });
    },
    /*** table */
    selectalgosFun(search) {
      let params = {};
      if (search) {
        params = {
          fuzzySearchName: this.searchVal,
          pageNum: this.pageNum,
          pageSize: 10
        };
      } else {
        params = {
          categoryId: this.leftEdit.id,
          pageNum: this.pageNum,
          pageSize: 10
        };
      }
      selectalgos(params).then(res => {
        if (res.code == 200) {
          this.tableData = res.content.list;
          this.pageNums = res.content.navigateLastPage;
        } else {
          this.$message({
            message: res.msg,
            type: "error"
          });
        }
      });
    },
    //table
    deletealgoFun(id) {
      this.$confirm("确认删除？")
        .then(_ => {
          deletealgo(id).then(res => {
            if (res.code == 200) {
              this.$message({
                message: res.msg,
                type: "success"
              });
              this.selectalgosFun();
            } else {
              this.$message({
                message: res.msg,
                type: "error"
              });
            }
          });
        })
        .catch(_ => {});
    },
    pageListFun() {
      pageList().then(res => {
        this.pageList = res.data;
      });
    },
    handleCurrentChange(data) {
      this.addPlug.registryId = data.id;
    },
    paramTypeFun(val) {
      if (val == 3) {
        let params = {
          pageNum: 1,
          pageSize: 10000000
        };
        selectDataDictionary(params).then(res => {
          if (res.code == 200) {
            this.selectDictionary = res.content.list;
          } else {
            this.$message({
              message: res.msg,
              type: "error"
            });
          }
        });
      }
    },
    editBtn(row) {
      this.title = "编辑分组";
      this.addPluginVisible = true;
      let params= {
        id:row.id
      }
      selectalgosbyid(params).then(res =>{
          if(res.code == 200){
            res.content.params.forEach( item =>{
              item.isRedo == 1 ? item.isRedo = true : item.isRedo = false;
              item.isShow == 1 ? item.isShow = true : item.isShow = false;
            })
            this.addPlug = res.content;
          }else{
            this.$message({
              message: res.msg,
              type: "error"
            });
          }
      })

    },
    plusTable() {
      let keyVal = 0;
      if(this.addPlug.params !==null){
        keyVal = this.addPlug.params.length;
      }else{
        this.addPlug.params = [];
      }
      this.addPlug.params.push({
        algoId: "",
        defaultValue: "",
        dictionaryCategoryId: null,
        id: "",
        isRedo: true,
        isShow: true,
        paramDescription: "",
        paramName: "",
        paramType: 1,
        key: keyVal
      });
    },
    savealgoFun() {
      if (this.addPlug.params !== null && this.addPlug.params.length > 0) {
        this.addPlug.params.forEach(item => {
          item.isRedo ? (item.isRedo = 1) : (item.isRedo = 0);
          item.isShow ? (item.isShow = 1) : (item.isShow = 0);
        });
      }
      savealgo(this.addPlug).then(res => {
        if (res.code == 200) {
          this.$message({
            message: res.msg,
            type: "success"
          });
          this.selectalgosFun(); //回调数据
          this.addPluginVisible = false;
        } else {
          this.$message({
            message: res.msg,
            type: "error"
          });
        }
      });
    },
    addPluginBtn() {
      this.title = "新增分组";
      this.addPluginVisible = true;
      this.addPlug = {
        state: 1,
        blockStrategy: 1,
        params: [],
        categoryId:this.leftEdit.id,
        jobhandler:'',
      }
    },
    tableDelete(keyVal) {
      this.$confirm("确认删除？")
        .then(_ => {
          this.addPlug.params.splice(keyVal, 1);
        })
        .catch(_ => {});
    },
    handleCommand(id, data) {
      data.categoryId = id;
      this.addPlug = data;
      this.savealgoFun();
    },
    tabClick(val){
        if(this.activeName == 'third'){
          if(this.pageList.length > 0){
          let _this = this;
          this.$nextTick(()=>{
            //_this.addPlug.registryId =this.addPlug.registryId;
            for(let i=0; i<_this.pageList.length; i++){
                let item = _this.pageList[i];
                if(item.id == _this.addPlug.registryId){
                  _this.$refs.tables.setCurrentRow(item)
                }
            }

          })

        }
        }
    }
  }
};
</script>
<style scoped>
.plugin {
  display: block;
  padding: 20px;
  background: #f5f5f5;
  height: 100%;
  overflow: hidden;
}
.plugin_main {
  border-radius: 8px;
  background: #fff;
  border: 1px solid #e1e8f0;
  height: 100%;
  overflow: hidden;
}
.plugin_left {
  float: left;
  height: 100%;
  width: 220px;
  border-right: 1px solid #ddd;
}
.plugin_nav {
  border-bottom: 1px solid #e1e8f0;
  height: 45px;
  padding: 5px;
  width: 100%;
  box-sizing: border-box;
}
.plugin_nav .name {
  display: block;
  float: left;
  font-size: 14px;
  line-height: 35px;
  padding-left: 10px;
}
::v-deep.el-button {
  line-height: 35px;
  padding: 5px 10px;
}
.plugin_nav /deep/.el-button {
  float: right;
  display: block;
  height: 35px;
  line-height: 35px;
  padding: 0;
  margin: 0 5px;
}
.plugin_nav /deep/.el-input {
  width: 240px;
  float: left;
}
/deep/.el-button i {
  font-size: 16px;
  display: inline-block;
  vertical-align: middle;
  line-height: 25px;
}

/deep/.el-button span {
  vertical-align: top;
  line-height: 35px;
}
.plugin_right {
  float: left;
  height: 100%;
  width: calc(100% - 221px);
}
.plugin_right /deep/.el-button span {
  padding: 0 10px;
}
.table_box {
  display: block;
  padding: 15px;
}
.from_item {
  display: block;
  width: 80%;
  margin: 0 auto 10px;
}
.from_item span {
  display: inline-block;
  line-height: 34px;
  float: left;
  width: 100px;
  text-align: right;
}
.from_item /deep/.el-input,
.from_item /deep/.el-select {
  width: calc(100% - 100px);
}
.from_item /deep/.el-select .el-input {
  width: 100%;
}
/deep/.el-button {
  padding: 0 10px;
}
/deep/.el-dialog__header {
  background: rgba(0, 0, 0, 0.1);
}
/deep/.el-dialog {
  border-radius: 5px;
}
/deep/.el-tree-node__content {
  height: 35px;
}
/deep/.el-pagination {
  text-align: right;
  padding-top: 10px;
}
/deep/.el-dropdown{ color:#1890ff; font-size: 16px;}

</style>
