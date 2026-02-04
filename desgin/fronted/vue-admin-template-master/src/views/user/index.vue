<template>
  <div class="app-container">
    <el-form :inline="true" :model="form" ref="form" class="demo-form-inline form-container">
      <el-form-item label="人员查询">
        <el-input v-model="form.info" placeholder="人员信息" clearable></el-input>
      </el-form-item>
      <el-form-item label="所属公司">
          <el-select v-model="form.company" placeholder="公司名称">
              <el-option 
                v-for="company in companies"
                :key="company"
                :label="company"
                :value="company"></el-option>
          </el-select>
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
    <user-dialog
      :title="'新增用户'"
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
        <el-table-column align="center" label="用户名" width="160" prop="username">
        </el-table-column>
        <el-table-column align="center" label="所属公司" prop="company">
        </el-table-column>
        <el-table-column class-name="status-col" label="用户状态" width="140" align="center" prop="isEnabled">
          <template slot-scope="{row}">
            <el-tag :type="statusType(row.isEnabled)">
              {{ statusText(row.isEnabled) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="用户权限" width="160" prop="permission">
          <template slot-scope="{row}">
              {{ permissionText(row.permission) }}
          </template>
        </el-table-column>
        <el-table-column align="center" prop="createTime" label="用户创建时间" width="220">
          <template slot-scope="scope">
            <i class="el-icon-time"></i>
            <span style="margin-left: 10px">{{ scope.row.createTime.toLocaleString().replace(/T/g, ' ').substr(0,16) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="执行操作" width="240" align="center">
            <template slot-scope="scope">
                <el-button
                    size="mini"
                    :type="statusType(scope.row.isEnabled===0?1:0)"
                    @click="handleUserStatus(scope.row)"
                    :disabled="statusEdit(scope.row.permission)">{{statusText(scope.row.isEnabled===0?1:0)}}<i class="el-icon-edit" style="margin-left: 5px"></i> </el-button>
                <el-button
                    size="mini"
                    type="primary"
                    @click="handleResetPassword(scope.$index, scope.row)"
                    :disabled="statusEdit(scope.row.permission)">重置密码<i class="el-icon-edit" style="margin-left: 5px"></i> </el-button>
            </template>
        </el-table-column>
    </el-table>
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
import UserDialog from './components/UserDialog.vue'
import {pageQuery, company, addUser, deleteByIds, enable, resetPassword} from '@/api/user'

export default {
  components: { UserDialog },
  data() {
    return {
      listLoading: false,
      dialogVisible: false,
      form: {
        info: '',
        company: '',
        page: 1,
        pageSize: 5
      },
      total: 50,
      companies: [],
      tableData: [],
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        company: [
          { required: true, message: '所属公司不能为空'},
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ]
      },
      addForm: {
        username: '',
        company: '',
      },
      multipleTable: [],
      ids: []
    }
  },
  created() {
    this.companyLoad();
    this.pageLoad(this.form);
  },
  methods: {
    statusEdit(permission){
      return permission===1? true:false;
    },
    statusType(status) {
        const statusMap = {
          1: 'success',
          0: 'info',
        };
        return statusMap[status] || 'info'; // 默认类型
    },
    statusText(status) {
      const statusMap = {
        0: '禁用',
        1: '启用',
      };
      return statusMap[status] || status; // 默认文本
    },
    permissionText(status) {
      const statusMap = {
        0: '用户',
        1: '管理员',
      };
      return statusMap[status] || status; // 默认文本
    },
    handleSelectionChange(val) {
        console.log('row:',val)
        this.multipleSelection = val;
        this.ids = this.multipleSelection.map(v => v.username);
        console.log(this.multipleSelection.map(v => v.username));
    },
    handleDeletes() {
      if (this.ids.length === 0){
        return
      }
      this.$confirm('此操作将批量删除用户, 是否继续?', '提示', {
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
    resetForm(formName) {
      this.form.info = '';
      this.form.company = '';
    },
    handleUserStatus(row){
      let username = row.username;
      let status = row.isEnabled===0?1:0;
      let statusText = this.statusText(status);
      this.$confirm('确认'+statusText+'?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log('statusUser:', username);
        enable(status, username).then(res=>{
          this.$message({
              type: 'success',
              message: statusText+'成功!'
          });
          this.pageLoad(this.form);
        }).catch(()=>{
          this.$message({
              type: 'error',
              message: statusText+'失败！'
          });
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        });
      });
    },
    handleResetPassword(index, row){
      this.$confirm('确认重置密码?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log('resetPassword:', row.username);
        resetPassword(row.username).then(res=>{
          this.$message({
              type: 'success',
              message: '重置成功!'
          });
        }).catch(()=>{
          this.$message({
              type: 'error',
              message: '重置失败！'
          });
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        });
      });
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
      addUser(formData).then(res =>{
        console.log('submit:', res)
        this.$message({
            type: 'success',
            message: '新增成功!'
        });
        this.pageLoad(this.form)
        this.companyLoad()
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
    companyLoad(){
      company().then(res=>{
        console.log('company:', res)
        this.companies = res.data
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