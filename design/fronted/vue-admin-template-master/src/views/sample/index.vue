<template>
    <div class="app-container">
        <el-form :inline="true" :model="form" class="demo-form-inline form-container">
            <el-form-item label="样本查询">
              <el-input v-model="form.info" placeholder="样本信息"></el-input>
            </el-form-item>
            <el-form-item label="活动区域">
                <el-select v-model="form.region" placeholder="活动区域">
                    <el-option 
                      v-for="region in regions"
                      :key="region"
                      :label="region"
                      :value="region"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="采样时间">
            <el-col :span="11">
                <el-date-picker type="datetime" placeholder="开始日期" v-model="form.date1" style="width: 100%;" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
            </el-col>
            <el-col class="line" :span="2">-</el-col>
            <el-col :span="11">
                <el-date-picker type="datetime" placeholder="结束日期" v-model="form.date2" style="width: 100%;" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
            </el-col>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSubmit" icon="el-icon-search">查询</el-button>
            </el-form-item>
            <el-form-item>
                <el-button type="success" @click="resetForm('form')" icon="el-icon-refresh">重置</el-button>
            </el-form-item>
        </el-form>
        <el-form :inline="true" :model="form" class="demo-form-inline form-container">
            <el-form-item>
                <el-button type="warning" @click="dialogVisible = true" icon="el-icon-circle-plus">新增</el-button>
                <el-button style="margin-left: 10px;" type="danger" @click="handleDeletes()" icon="el-icon-remove-outline">批量删除</el-button>
            </el-form-item>
        </el-form>
        <sample-dialog
                      :title="'新增样本'"
                      :dialogFormVisible.sync="dialogVisible"
                      :ruleForm="addForm"
                      :rules="rules"
                      @submits="handleSubmit"
                      @reset="handleReset"
                      ref="addDialog"
                />
        <el-table
            v-loading="listLoading"
            element-loading-text="Loading"
            :data="tableData"
            ref="multipleTable"
            border
            fit
            stripe
            highlight-current-row
            @selection-change="handleSelectionChange"
        >
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>
        <el-table-column align="center" label="车辆编号" width="160" prop="deviceNo">
        </el-table-column>
        <el-table-column align="center" label="车辆名称" width="160" prop="deviceName">
        </el-table-column>
        <el-table-column align="center" label="采样周期" width="100" prop="oilCycle">
        </el-table-column>
        <el-table-column align="center" label="机油运行时间" width="160" prop="oilRuntime">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.oilRuntime.toFixed(2) + '小时' }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="oilSampleDate" label="采样时间" width="200">
          <template slot-scope="scope">
            <i class="el-icon-time"></i>
            <span style="margin-left: 10px">{{ scope.row.oilSampleDate.toLocaleString().replace(/T/g, ' ').substr(0,16) }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="样本编号" prop="oilSampleId">
        </el-table-column>
        <el-table-column align="center" label="更新时间" width="200" prop="updateTime">
          <template slot-scope="scope">
            <i class="el-icon-time"></i>
            <span style="margin-left: 10px">{{ scope.row.updateTime.toLocaleString().replace(/T/g, ' ').substr(0,16) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="执行操作" width="160" align="center">
            <template slot-scope="scope">
                <el-button
                    size="mini"
                    type="success"
                    @click="viewSamples(scope.$index, scope.row)">查看<i class="el-icon-info" style="margin-left: 5px"></i> </el-button>
            </template>
        </el-table-column>
        </el-table>
        <sample-dialog
              :title="'样本信息'"
              :dialogFormVisible.sync="dialogFormVisible"
              :ruleForm="editForm"
              :rules="rules"
              :disabled="true"
            />
        <div class="pagination-container">
            <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="form.page"
                :page-sizes="[5, 10, 15, 20]"
                :page-size="form.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                style="padding: 20px 0;"
            >
            </el-pagination>
        </div>
    </div>
</template>

<script>
import SampleDialog from './components/SampleDialog.vue'
import {region} from '@/api/vehicle'
import {pageQuery, deleteByIds, queryOilSample, addOilSample} from '@/api/oilsample'
import {websocket} from '@/layout/components/Navbar.vue'
import store from '@/store'

export default {
  components: { SampleDialog },
  data() {
      return {
        listLoading: true,
        dialogVisible: false,
        regions: [],
        form: {
          info: '',
          region: '',
          date1: '',
          date2: '',
          page:1,
          pageSize:5,
        },
        total:50,
        dialogFormVisible: false,
        addForm: {
              deviceNo: '',
              oilCycle: '',
              oilSampleDate: '',
              oilChangeDate: '',
              engineType: '',
              isEnd:'1',
              oilType: '',
              oilTan: '',
              oilTbn: '',
              oilWater: '',
              oilViscosity: '',
              oilFe: '',
              oilCu: '',
              oilAl: '',
              oilSi: ''
        },
        editForm: {
              deviceNo: '',
              oilCycle: '',
              oilSampleDate: '',
              oilChangeDate: '',
              engineType: '',
              isEnd: '',
              oilType: '',
              oilTan: '',
              oilTbn: '',
              oilWater: '',
              oilViscosity: '',
              oilFe: '',
              oilCu: '',
              oilAl: '',
              oilSi: ''
        },
        rules: {
          deviceNo: [
            { required: true, message: '请输入车辆编号', trigger: 'blur' },
            { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
          ],
          isEnd: [
            {required: true}
          ],
          oilCycle: [
            {required: true, message: '请输入机油周期', trigger: 'blur'},
            { type: 'number', message: '机油周期必须为数字值'}
          ],
          oilTan: [
            { required: true, message: '请输入机油酸值', trigger: 'blur'},
            { validator: this.validateDouble, trigger: 'blur' }
          ],
          oilTbn: [
            { required: true, message: '请输入机油碱值', trigger: 'blur' },
            { validator: this.validateDouble, trigger: 'blur' }
          ],
          oilWater: [
            { required: true, message: '请输入机油水含量', trigger: 'blur' },
            { validator: this.validateDouble, trigger: 'blur' }
          ],
          oilViscosity: [
            { required: true, message: '请输入机油粘度', trigger: 'blur' },
            { validator: this.validateDouble, trigger: 'blur' }
          ],
          oilFe: [
            { required: true, message: '请输入机油铁含量', trigger: 'blur' },
            { validator: this.validateDouble, trigger: 'blur' }
          ],
          oilCu: [
            { required: true, message: '请输入机油铜含量', trigger: 'blur' },
            { validator: this.validateDouble, trigger: 'blur' }
          ],
          oilAl: [
            { required: true, message: '请输入机油铝含量', trigger: 'blur' },
            { validator: this.validateDouble, trigger: 'blur' }
          ],
          oilSi: [
            { required: true, message: '请输入机油硅含量', trigger: 'blur' },
            { validator: this.validateDouble, trigger: 'blur' }
          ],
          oilSampleDate: [
            { required: true, message: '请选择日期', trigger: 'change' }
          ],
          oilChangeDate: [
            { required: true, message: '请选择日期', trigger: 'change' }
          ]
        },
        tableData: [],
        multipleTable: [],
        ids: [],
        username: '',
        roles: []
      }
  },
  created() {
    this.username = store.getters.name;
    this.roles = store.getters.roles;
    this.regionLoad()
    this.pageLoad(this.form)
  },
  methods: {
      validateDouble(rule, value, callback) {
        const doubleRegex = /^(0|([1-9][0-9]*))(\.[\d]+)?$/;
        if (!value) {
          return callback(new Error('请输入样本数值'));
        }
        if (!doubleRegex.test(value)) {
          return callback(new Error('无效的样本数值'));
        }
        callback();
      },
      handleSelectionChange(val) {
          this.multipleSelection = val;
          this.ids = this.multipleSelection.map(v => v.oilSampleId);
          console.log(this.multipleSelection.map(v => v.oilSampleId));
      },
      handleDeletes(ids) {
        if (this.ids.length === 0){
          return
        }
        console.log(ids);
          this.$confirm('此操作将批量删除样本, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteByIds(this.ids).then(res=>{
            this.$message({
                type: 'success',
                message: '删除成功!'
            });
            this.pageLoad(this.form);
            this.regionLoad();
          })
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },
      onSubmit() {
          console.log('submit!');
          this.pageLoad(this.form);
      },
      viewSamples(index, row) {
        let id = row.oilSampleId;
        queryOilSample(id).then(res=>{
          this.editForm = res.data;
          let end = res.data.isEnd===0? '0':'1'
          this.editForm.isEnd = end
          this.dialogFormVisible = true;
        })
      },
      handleSizeChange(pageSize) {
        console.log(`每页 ${pageSize} 条`);
        this.form.pageSize = pageSize;
        this.pageLoad(this.form);
      },
      handleCurrentChange(page) {
        console.log(`当前页: ${page}`);
        this.form.page = page;
        this.pageLoad(this.form);
      },
      handleSubmit(formData) {
        console.log('Form submitted with:', formData);
        addOilSample(formData).then(res =>{
          console.log('submit:', res)
          this.$message({
              type: 'success',
              message: '新增成功!'
          });
          this.pageLoad(this.form)
          if (this.roles.includes('editor')){
            websocket.send('新增车辆编号为:'+formData.deviceNo+'的机油样本');
          }
          this.$refs.addDialog.resetForm(formData)
          this.dialogVisible = false; 
        }).catch(()=>{
          this.$message({
              type: 'error',
              message: '新增失败！'
          });
          this.$refs.addDialog.resetForm(formData)
          this.dialogVisible = false;
        });
      },
      handleReset() {
        console.log('Form reset');
        // Handle form reset if needed
      },
      resetForm(formName) {
        this.form.info = '';
        this.form.date1 = '';
        this.form.date2 = '';
        this.form.region = '';
      },
      pageLoad(tableFrom){
        this.listLoading = true;
        pageQuery(tableFrom).then(res=>{
          console.log('pageQuery:', res)
          this.total = res.data.total;
          this.tableData = res.data.records;
          this.listLoading = false;
        })
      },
      regionLoad(){
        region().then(res=>{
          console.log('region:', res)
          this.regions = res.data
        });
      }
  }
}
</script>

<style>
.line {
  display: flex;
  justify-content: center;  /* 水平方向居中 */
  align-items: center;      /* 垂直方向居中 */
  height: 100%;             /* 使flex容器占满父容器高度 */
}
.form-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  align-items: center;
}
.form-container .el-form-item {
  margin-right: 20px;
}
.form-container .right-align {
  margin-left: auto;
}
.pagination-container {
  display: flex;
  justify-content: center; /* 水平方向居中 */
  align-items: center;     /* 垂直方向居中（可选，根据需求） */
  padding: 20px 0;         /* 可选，根据需要调整内边距 */
}
.custom-dialog .el-form-item__label {
  font-size: 12px; /* 设置标签字体大小为12px */
}
.form-item .el-form-item__label {
  width: 100px; /* 设置标签宽度 */
}

</style>
