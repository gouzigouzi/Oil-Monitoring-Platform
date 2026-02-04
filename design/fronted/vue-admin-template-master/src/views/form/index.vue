<template>
  <div class="dashboard-container">
    <el-button type="primary" @click="centerDialogVisible = true">车辆详细信息</el-button>
    <el-button type="danger" icon="el-icon-warning" @click="changeDialogVisible = true">车辆换油</el-button>
    <vehicle-info-dialog 
      :visible.sync="centerDialogVisible"
      :vehicle="vehicle"
    />
    <change-oil-dialog
      :visible.sync="changeDialogVisible"
      :ruleForm="changeForm"
      :rules="rules"
      @submits="handleChange"
      ref="changeDialog"
    />
    <panel-group-up 
    :chartData="vehicle"
    :locationName="locationName"/>
    <panel-group-down :chartData="vehicle"/>
    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-wrapper">
          <oil-life-chart :deviceNo="this.deviceNo"/>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-wrapper">
          <change-oil-info :chartData="changeOilData"/>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper"> 
          <engine-line-chart :deviceNo="this.deviceNo"/>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <engine-pie-chart :deviceNo="this.deviceNo"></engine-pie-chart>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <engine-bar-chart :deviceNo="this.deviceNo"/>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import PanelGroupUp from './components/PanelGroupUp.vue';
import PanelGroupDown from './components/PanelGroupDown.vue';
import VehicleInfoDialog from './components/VehicleInfoDialog.vue';
import ChangeOilInfo from './components/ChangeOilInfo.vue';
import OilLifeChart from './components/OilLifeChart.vue';
import EngineLineChart from './components/EngineLineChart.vue';
import EnginePieChart from './components/EnginePieChart.vue';
import EngineBarChart from './components/EngineBarChart.vue';
import ChangeOilDialog from './components/ChangeOilDialog.vue';
import {detail} from '@/api/vehicle';
import {changeHistory, changeOil} from '@/api/oillife';
export default {

  name: 'Form',
  components: {
    PanelGroupUp,
    PanelGroupDown,
    VehicleInfoDialog,
    ChangeOilInfo,
    OilLifeChart,
    EngineLineChart,
    EnginePieChart,
    EngineBarChart,
    ChangeOilDialog
  },
  data() {
    return {
      deviceNo :'',
      vehicle: {
        deviceNo: '',
        deviceName: '',
        deviceLocation: '',
        deviceType: '',
        deviceEngineType: '',
        oilRuntime: 0,
        deviceAccumulatedRuntime: 0,
        oilType: '',
        changeCount: 0,
        oilLifeTimePercent: 0,
        lastChangeDatetime: '',
        deviceManager: '',
        deviceManagerPhone: ''
      },
      changeForm: {
        deviceNo: '',
        lastChangeDatetime: ''
      },
      rules: {
          lastChangeDatetime: [
            { required: true, message: '请选择日期', trigger: 'change' }
          ]
        },
      centerDialogVisible: false,
      changeDialogVisible: false,
      changeOilData: [],
      locationList: [],
      locationName: '',
      regex: /.+?(省|市|自治区|自治州|县|区)/g,
    }
  },
  beforeRouteEnter(to, from, next) {
    if (sessionStorage.getItem('fromVehicleList')) {
      sessionStorage.removeItem('fromVehicleList'); // 删除标识
      next();
    } else {
      next({ name: 'List' }); // 重定向到车辆列表页面
    }
  },
  created() {
    this.deviceNo = this.$route.params.id;
    this.changeForm.deviceNo = this.deviceNo;
    console.log('deviceNo:', this.deviceNo);
    this.pageLoad(this.deviceNo);
  },
  methods: {
    handelLocationName(){
      this.locationList = this.vehicle.deviceLocation.match(this.regex)
      if (this.locationList ===null || this.locationList.length===0){
        this.locationName = this.vehicle.deviceLocation.substr(0,9);
        console.log('locationName:', this.locationName)
        return;
      }
      var s = this.locationList.join('')
      this.locationName = s.substr(0,9);
      console.log('locationName:', this.locationName)
    },
    pageLoad(deviceNo){
      detail(deviceNo).then(res=>{
        console.log('vehicleDetail:', res);
        this.vehicle = res.data;
        this.vehicle.oilLifeTimePercent = res.data.oilLifeTimePercent*100;
        this.handelLocationName()
      })
      changeHistory(deviceNo).then(res=>{
        console.log('changeHistory:',res);
        this.changeOilData = res.data;
      })
    },
    handleChange(formData){
      console.log('Form submitted with:', formData);
      changeOil(formData).then(res =>{
        console.log('submit:', res)
        this.$message({
            type: 'success',
            message: '更新成功!'
        });
        this.$refs.changeDialog.closeDialog()
        this.changeDialogVisible = false; 
      }).catch(()=>{
        this.$message({
            type: 'error',
            message: '更新失败！'
        });
        this.$refs.changeDialog.closeDialog()
        this.changeDialogVisible = false;
      });
    }
  },
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;
  height: 100%;
  .chart-wrapper {
    background: #ffffff;
    padding: 16px;
    margin-bottom: 32px;
    border-radius: 20px;
  }
}

@media (max-width:1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>