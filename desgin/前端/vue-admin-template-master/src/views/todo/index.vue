<template>
<div class="app-container">
    <el-form :inline="true" :model="form" ref="form" class="demo-form-inline form-container">
    <el-form-item label="申请查询">
        <el-input v-model="form.info" placeholder="申请信息" clearable></el-input>
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
    <el-form-item label="申请类型">
        <el-select v-model="form.submitType" placeholder="申请类型">
            <el-option 
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"/>
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
            <el-button type="success" icon="el-icon-circle-plus" @click="handleBatchAgree()">批量同意</el-button>
            <el-button type="warning" icon="el-icon-remove-outline" @click="handleBatchReject()">批量拒绝</el-button>
            <el-button v-if="true" style="margin-left: 10px;" type="danger" @click="handleDeletes()" icon="el-icon-delete">批量删除</el-button>
        </el-form-item>
    </el-form>
    <el-tabs v-model="activeName" type="border-card" @tab-click="handleClick">
        <el-tab-pane label="待办申请" name="todo">
            <span slot="label">
                待办申请<el-badge v-if="submitCount>0" :value="submitCount"/>
            </span>
        </el-tab-pane>
        <el-tab-pane label="已办申请" name="finish"/>
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
        <el-table-column align="center" label="用户名" width="140" prop="username">
        </el-table-column>
        <el-table-column align="center" label="所属公司" width="200" prop="company">
        </el-table-column>
        <el-table-column align="center" label="申请操作" prop="operation">
            <template slot-scope="{row}">
                <el-button type="text" @click="viewApply(row.id, row.submitType)">
                    {{ typeText(row.submitType) }}
                </el-button>
                {{ row.operation }}
            </template>
        </el-table-column>
        <el-table-column class-name="status-col" label="申请状态" width="140" align="center" prop="todoStatus">
            <template slot-scope="{row}">
                <el-tag :type="statusType(row.todoStatus)">
                {{ statusText(row.todoStatus) }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column align="center" prop="createTime" label="用户申请时间" width="200">
            <template slot-scope="scope">
                <i class="el-icon-time"></i>
                <span style="margin-left: 10px">{{ scope.row.createTime.toLocaleString().replace(/T/g, ' ').substr(0,16) }}</span>
            </template>
        </el-table-column>
        <el-table-column label="执行操作" width="220" align="center">
            <template slot-scope="scope">
                <el-button
                    size="mini"
                    type="success"
                    @click="handleAgree(scope.row.username, scope.row.id, scope.row.submitType, scope.row.operation)"
                    :disabled="isShow">同意<i class="el-icon-edit" style="margin-left: 5px"></i> </el-button>
                <el-button
                    size="mini"
                    type="warning"
                    @click="handleReject(scope.row.username, scope.row.id)"
                    :disabled="isShow">拒绝<i class="el-icon-edit" style="margin-left: 5px"></i> </el-button>
            </template>
        </el-table-column>
    </el-table>
    <reject-dialog
        :title="'拒绝申请'"
        :dialogFormVisible.sync="dialogVisible"
        :ruleForm="rejectedForm"
        :rules="rules"
        @submits="submitReject"
        @reset="handleReset"
        ref="rejectedDialog"
    />
    <view-vehicle-dialog
        :title="dialogTitle"
        :dialogFormVisible.sync="dialogVehicleVisible"
        :ruleForm="vehicleForm"
        ref="vehicleDialog"
    />
    <view-sample-dialog
        :title="dialogTitle"
        :dialogFormVisible.sync="dialogAddOilVisible"
        :ruleForm="oilForm"
        ref="oilDialog"
    />
    <view-delete-vehicle-dialog
        :title="dialogTitle"
        :dialogFormVisible.sync="dialogDeleteVehicleVisible"
        :ruleForm="vehicleDeleteForm"
        ref="vehicleDeleteDialog"
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
    </el-tabs>
</div>
</template>

<script>
import {company} from '@/api/user'
import {pageQuery, todoCount, deleteByIds, agreeByIds, reject, viewVehicle, viewSample, viewDeleteVehicle} from '@/api/todo'
import RejectDialog from './component/RejectDialog.vue'
import ViewVehicleDialog from "./component/ViewVehicleDialog.vue";
import ViewSampleDialog from './component/ViewSampleDialog.vue';
import ViewDeleteVehicleDialog from './component/ViewDeleteVehicleDialog.vue';
import {websocket} from '@/layout/components/Navbar.vue'

export default {
  components: { RejectDialog, ViewVehicleDialog, ViewSampleDialog, ViewDeleteVehicleDialog },
data() {
    return {
        listLoading: false,
        activeName: 'todo',
        dialogVisible: false,
        dialogVehicleVisible: false,
        dialogDeleteVehicleVisible: false,
        dialogAddOilVisible: false,
        isShow: false,
        options: [
            {
            value: 0,
            label: '新增车辆'
            }, 
            {
            value: 1,
            label: '修改车辆'
            }, 
            {
            value: 2,
            label: '删除车辆'
            }, 
            {
            value: 3,
            label: '新增机油样本'
            }
        ],
        form: {
            info: '',
            company: '',
            submitType: '',
            todoStatus: 0,
            page: 1,
            pageSize: 5
        },
        total: 50,
        submitCount: 0,
        companies: [],
        tableData: [],
        rejectedForm: {
            rejectUsernameList: [],
            rejectIdList: [],
            rejectReason: '',
            detailReason: ''
        },
        rules: {
            rejectReason: { required: true, message: '请选择拒绝理由', trigger: 'change' }
        },
        dialogTitle: '',
        vehicleForm: {
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
        oilForm: {
            deviceNo: '1111',
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
        vehicleDeleteForm: [],
        multipleTable: [],
        ids: [],
        usernameList: [],
        applyList: []
    }
},
created() {
    this.companyLoad();
    this.submitCountLoad();
    this.pageLoad(this.form);
},
methods: {
    typeText(type){
        const statusMap = {
            0: '新增车辆',
            1: '修改车辆',
            2: '删除车辆',
            3: '新增机油样本'
        };
        return statusMap[type] || '';
    },
    statusType(status) {
        const statusMap = {
            0: 'warning',
            1: 'success',
            2: 'danger'
        };
        return statusMap[status] || 'info'; // 默认类型
    },
    statusText(status) {
        const statusMap = {
            0: '待办理',
            1: '已通过',
            2: '已拒绝'
        };
        return statusMap[status] || status; // 默认文本
    },
    handleClick(tab, event) {
        if (tab.name === 'todo'){
            this.form.todoStatus = 0;
            this.isShow = false;
        }else{
            this.form.todoStatus = 1;
            this.isShow = true;
        }
        this.pageLoad(this.form);
        this.submitCountLoad();
        console.log(tab.name);
    },
    handleSelectionChange(val) {
        this.multipleSelection = val;
        this.ids = this.multipleSelection.map(v => v.id);
        this.usernameList = this.multipleSelection.map(v => v.username);
        this.applyList = this.multipleSelection.map(v => this.typeText(v.submitType)+':'+v.operation);
        console.log(this.multipleSelection.map(v => this.typeText(v.submitType)+v.operation));
    },
    viewApply(id, submitType){
        this.dialogTitle = this.typeText(submitType);
        console.log('id', id, 'type', this.dialogTitle);
        switch (submitType) {
            case 0:
                console.log('type', this.dialogTitle);
                viewVehicle(id).then(res=>{
                    this.vehicleForm = res.data;
                    this.dialogVehicleVisible = true;
                })
                break;
            case 1:
                viewVehicle(id).then(res=>{
                    this.vehicleForm = res.data;
                    this.dialogVehicleVisible = true;
                })
                break;
            case 2:
                viewDeleteVehicle(id).then(res=>{
                    this.vehicleDeleteForm = res.data;
                    this.dialogDeleteVehicleVisible = true;
                })
                break;
            case 3:
                viewSample(id).then(res=>{
                    this.oilForm = res.data;
                    this.dialogAddOilVisible = true;
                })
                break;
            default:
                break;
        }
    },
    handleAgree(username, id, submitType, operation) {
        let idList = [];
        idList.push(id);
        this.$confirm('此操作将批量通过申请, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            agreeByIds(idList).then(res=>{
                this.$message({
                    type: 'success',
                    message: '审批成功!'
                });
                this.submitCountLoad();
                this.pageLoad(this.form);
                websocket.send(username+'-'+this.typeText(submitType)+':'+operation);
            });
        }).catch(() => {
            this.$message({
            type: 'info',
            message: '已取消审批'
            });
        });
    },
    handleReject(username, id) {
        this.rejectedForm.rejectUsernameList.push(username);
        this.rejectedForm.rejectIdList.push(id);
        console.log('idList', this.rejectedForm.rejectIdList);
        this.dialogVisible = true;
    },
    handleBatchAgree() {
        if (this.ids.length === 0){
            return
        }
        this.$confirm('此操作将批量通过申请, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            console.log('batch',this.usernameList.toString()+','+this.ids.toString());
            // for (var i=0;i<this.usernameList.length;i++){
            //     console.log(this.usernameList[i]+'-'+this.ids[i]);
            // }
            agreeByIds(this.ids).then(res=>{
                this.$message({
                    type: 'success',
                    message: '审批成功!'
                });
                this.submitCountLoad();
                this.pageLoad(this.form);
            });
            for (var i=0;i<this.usernameList.length;i++){
                websocket.send(this.usernameList[i]+','+this.applyList[i]);
            }
        }).catch(() => {
            this.$message({
            type: 'info',
            message: '已取消审批'
            });
        });
    },
    handleBatchReject() {
        if (this.ids.length === 0){
            return
        }
        this.rejectedForm.rejectUsernameList = this.usernameList;
        this.rejectedForm.rejectIdList = this.ids;
        this.dialogVisible = true;
    },
    submitReject(formData) {
        // Handle form submission
        console.log('Form submitted with:', formData);
        reject(formData).then(res =>{
            console.log('submit:', res)
            this.$message({
                type: 'success',
                message: '拒绝成功!'
            });
            this.submitCountLoad();
            this.pageLoad(this.form);
            this.dialogVisible = false;
            for(var i=0;i<this.usernameList.length;i++){
                // console.log(this.usernameList[i]+','+this.applyList[i]+','+formData.rejectReason+':'+formData.detailReason); 
                websocket.send(this.usernameList[i]+','+this.applyList[i]+','+formData.rejectReason+':'+formData.detailReason); 
            }
            this.$refs.rejectedDialog.resetForm(formData)
        }).catch(()=>{
            this.$message({
                type: 'error',
                message: '拒绝失败！'
            });
            this.$refs.rejectedDialog.resetForm(formData)
            this.dialogVisible = false;
        });
    },
    handleDeletes() {
        if (this.ids.length === 0){
            return
        }
        this.$confirm('此操作将批量删除申请, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            deleteByIds(this.ids).then(res=>{
                this.$message({
                    type: 'success',
                    message: '删除成功!'
                });
                this.submitCountLoad();
                this.companyLoad();
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
        this.form.submitType = ''
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
    handleReset() {
        console.log('Form reset');
        this.rejectedForm.rejectIdList = [];
        this.rejectedForm.rejectUsernameList = [];
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
    },
    submitCountLoad(){
        todoCount().then(res=>{
            console.log('count', res.data);
            this.submitCount = res.data;
        })
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
.item {
  margin-top: 10px;
  margin-right: 40px;
}

</style>