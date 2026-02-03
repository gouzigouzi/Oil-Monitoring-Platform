<template>
    <div class="app-container">
        <el-form :inline="true" :model="form" ref="form" class="demo-form-inline form-container">
            <el-form-item label="车辆查询">
              <el-input v-model="form.info" placeholder="车辆信息"></el-input>
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
            <el-form-item label="换油时间">
            <el-col :span="11">
                <el-date-picker type="datetime" placeholder="开始日期" v-model="form.startDateTime" style="width: 100%;" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
            </el-col>
            <el-col class="line" :span="2">-</el-col>
            <el-col :span="11">
                <el-date-picker type="datetime" placeholder="结束日期" v-model="form.endDateTime" style="width: 100%;" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
            </el-col>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSubmit" icon="el-icon-search">查询</el-button>
            </el-form-item>
            <el-form-item>
                <el-button type="success" @click="resetForm('form')" icon="el-icon-refresh">重置</el-button>
            </el-form-item>
        </el-form>
        <el-form :inline="true" class="demo-form-inline form-container">
            <el-form-item>
                <el-button type="warning" @click="dialogVisible = true" icon="el-icon-circle-plus">新增</el-button>
                <el-button style="margin-left: 10px;" type="danger" @click="handleDeletes()" icon="el-icon-remove-outline">批量删除</el-button>
            </el-form-item>
        </el-form>
        <vehicle-dialog
          :title="'新增车辆'"
          :dialogFormVisible.sync="dialogVisible"
          :ruleForm="addForm"
          :rules="rules"
          @submits="handleSubmit"
          @reset="handleReset"
          ref="addDialog"
        />
        <el-table
            element-loading-text="Loading"
            v-loading="listLoading"
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
        <el-table-column align="center" label="车辆所在地址" prop="deviceLocation">
        </el-table-column>
        <el-table-column class-name="status-col" label="机油状态" width="140" align="center" prop="oilStatus">
          <template slot-scope="{row}">
            <el-tag :type="statusType(row.oilStatus)">
              {{ statusText(row.oilStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="lastChangeDatetime" label="机油更换时间" width="220">
          <template slot-scope="scope">
            <i class="el-icon-time"></i>
            <span style="margin-left: 10px">{{ scope.row.lastChangeDatetime.toLocaleString().replace(/T/g, ' ') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="执行操作" width="200" align="center">
            <template slot-scope="scope">
                <el-button
                    size="mini"
                    type="success"
                    @click="viewVehicles(scope.$index, scope.row)">查看<i class="el-icon-info" style="margin-left: 5px"></i> </el-button>
                <el-button
                    size="mini"
                    type="info"
                    @click="editVehicles(scope.$index, scope.row)">编辑<i class="el-icon-edit" style="margin-left: 5px"></i> </el-button>
            </template>
        </el-table-column>
        </el-table>
        <vehicle-dialog
              :title="'车辆信息'"
              :dialogFormVisible.sync="dialogFormVisible"
              :ruleForm="editForm"
              :rules="rules"
              @submits="handleEdit"
              @reset="handleReset"
              ref="editDialog"
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
import VehicleDialog from './components/VehicleDialog.vue'
import {deleteByIds, queryVehicle, pageQuery, region, addVehicle, edit } from '@/api/vehicle'
import {websocket} from '@/layout/components/Navbar.vue'
import store from '@/store'

export default {
  inject: ['reload'],
  components: { VehicleDialog },
  filters: {
      statusFilter(status) {
      const statusMap = {
          健康: 'success',
          draft: 'gray',
          需换油: 'danger'
      }
      return statusMap[status]
      }
  },
  data() {
      return {
        listLoading: true,
        regions: [],
        form: { 
          info: '',
          region: '',
          startDateTime: '',
          endDateTime: '',
          page:1,
          pageSize:5,
        },
        total:50,
        dialogVisible: false,
        dialogFormVisible: false,
        addForm: {
              deviceNo: '',
              deviceLocation: '',
              lastChangeDatetime: '',
              deviceEngineType: '',
              oilType: '',
              deviceManager: '',
              deviceManagerPhone: '',
              changeCount: '',
              deviceLocationProvince: '',
              deviceLocationCity: '',
              deviceType: ''
        },
        editForm: {
              deviceNo: '',
              deviceLocation: '',
              date: '',
              engine: '',
              oil: '',
              manager: '',
              managerPhone: '',
              count: '',
              province: '',
              city: '',
              deviceType: ''
        },
        rules: {
          deviceNo: [
            { required: true, message: '请输入车辆编号', trigger: 'blur' },
            { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
          ],
          changeCount: [
            { required: true, message: '换油次数不能为空'},
            { type: 'number', message: '换油次数必须为数字值'}
          ],
          deviceLocation: [
            { required: true, message: '请输入车辆活动区域', trigger: 'change' }
          ],
          deviceLocationProvince: [
            { required: true, message: '请输入车辆所在省份', trigger: 'blur' }
          ],
          deviceLocationCity: [
            { required: true, message: '请输入车辆所在市', trigger: 'blur' }
          ],
          lastChangeDatetime: [
            { required: true, message: '请选择日期', trigger: 'change' }
          ],
          deviceManager: [
            { required: true, message: '请输入联系人', trigger: 'blur' }
          ],
          deviceManagerPhone: [
            { required: true, message: '请输入联系方式', trigger: 'blur' },
            { validator: this.validatePhone, trigger: 'blur' }
          ]
        },
        tableData: [
        ],
        multipleTable: [],
        ids: [],
        username: '',
        roles: [],
      }
  },
  created() {
    this.username = store.getters.name;
    this.roles = store.getters.roles;
    this.regionLoad();
    this.pageLoad(this.form);
  },
  methods: {
    statusType(status) {
        const statusMap = {
          0: 'success',
          1: 'warning',
          2: 'danger'
        };
        return statusMap[status] || 'info'; // 默认类型
    },
    statusText(status) {
      const statusMap = {
        0: '健康',
        1: '已老化',
        2: '需换油'
      };
      return statusMap[status] || status; // 默认文本
    },
    handleSelectionChange(val) {
        console.log('row:',val)
        this.multipleSelection = val;
        this.ids = this.multipleSelection.map(v => v.deviceNo);
        console.log(this.multipleSelection.map(v => v.deviceNo));
    },
    validatePhone(rule, value, callback) {
      const phoneRegex = /^[1][3-9][0-9]{9}$/;
      if (!value) {
        return callback(new Error('请输入联系方式'));
      }
      if (!phoneRegex.test(value)) {
        return callback(new Error('无效的联系方式'));
      }
      callback();
    },
    handleDeletes() {
      if (this.ids.length === 0){
        return
      }
      this.$confirm('此操作将批量删除车辆, 是否继续?', '提示', {
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
        if (this.roles.includes('editor')){
          websocket.send('删除车辆编号为：'+this.ids.toString());
        }
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
    resetForm(formName) {
      this.form.info = '';
      this.form.startDateTime = '';
      this.form.endDateTime = '';
      this.form.region = '';
    },
    viewVehicles(index, row) {
      let id = row.deviceNo;
      sessionStorage.setItem('fromVehicleList', true);
      this.$router.push({ name: 'Form', params: { id } })
    },
    handleSizeChange(pageSize) {
      console.log(`每页 ${pageSize} 条`);
      this.form.pageSize = pageSize;
      this.pageLoad(this.form);
    },
    handleCurrentChange(page) {
      console.log(`当前页: ${page}`)
      this.form.page = page
      this.pageLoad(this.form);
    },
    handleSubmit(formData) {
      // Handle form submission
      console.log('Form submitted with:', formData);
      addVehicle(formData).then(res =>{
        console.log('submit:', res)
        this.$message({
            type: 'success',
            message: '新增成功!'
        });
        this.pageLoad(this.form)
        this.regionLoad()
        if (this.roles.includes('editor')){
          websocket.send('新增车辆编号为：'+formData.deviceNo+'的车辆信息');
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
    editVehicles(index, row){
      let id = row.deviceNo;
      queryVehicle(id).then(res=>{
        this.editForm = res.data;
        // this.editForm.lastChangeDatetime = new Date(this.editForm.lastChangeDatetime).toISOString().slice(0, 19) + ".000";
        console.log('edit:', res);
        this.dialogFormVisible = true;
      })
    },
    handleEdit(formData){
      console.log('Form edit with:', formData);
      edit(formData).then(res=>{
        console.log('submit:', res)
        this.$message({
            type: 'success',
            message: '修改成功!'
        });
        this.pageLoad(this.form)
        this.regionLoad()
        // this.$refs.editDialog.resetForm(formData)
        this.dialogFormVisible = false; 
        if (this.roles.includes('editor')){
          websocket.send('修改车辆编号为:'+formData.deviceNo+'的车辆信息');
        }
      }).catch(()=>{
        this.$message({
            type: 'error',
            message: '修改失败！'
        });
        // this.$refs.editDialog.resetForm(formData)
        this.dialogFormVisible = false;
      });
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
