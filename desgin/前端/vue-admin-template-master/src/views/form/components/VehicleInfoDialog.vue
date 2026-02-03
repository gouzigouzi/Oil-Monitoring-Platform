<template>
  <el-dialog
    title="车辆详细信息"
    :visible.sync="localVisible"
    width="30%"
    center>
    <div class="dialog-content"><strong>车辆编号：</strong>{{vehicle.deviceNo}}</div>
    <div class="dialog-content"><strong>车辆名称：</strong>{{vehicle.deviceName}}</div>
    <div class="dialog-content"><strong>车辆活动区域：</strong>{{vehicle.deviceLocation}}</div>
    <div class="dialog-content"><strong>车辆型号：</strong>{{vehicle.deviceType}}</div>
    <div class="dialog-content"><strong>车辆发动机型号：</strong>{{vehicle.deviceEngineType}}</div>
    <div class="dialog-content"><strong>车辆发动机运行时间：</strong>{{vehicle.oilRuntime}}小时</div>
    <div class="dialog-content"><strong>车辆发动机累计运行时间：</strong>{{vehicle.deviceAccumulatedRuntime}}小时</div>
    <div class="dialog-content"><strong>机油型号：</strong>{{vehicle.oilType}}</div>
    <div class="dialog-content"><strong>车辆累计换油次数：</strong>{{vehicle.changeCount}}</div>
    <div class="dialog-content"><strong>机油寿命百分比：</strong>{{vehicle.oilLifeTimePercent}}%</div>
    <div class="dialog-content"><strong>车辆换油日期：</strong>{{vehicle.lastChangeDatetime.toLocaleString().replace(/T/g, ' ')}}</div>
    <div class="dialog-content"><strong>车辆联系人：</strong>{{vehicle.deviceManager}}</div>
    <div class="dialog-content"><strong>联系方式：</strong>{{vehicle.deviceManagerPhone}}</div>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="closeDialog">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'VehicleInfoDialog',
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    vehicle: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      localVisible: this.visible
    }
  },
  watch: {
    visible(newVal) {
      this.localVisible = newVal;
    },
    localVisible(newVal) {
      this.$emit('update:visible', newVal);
    },
  },
  methods: {
    closeDialog() {
      this.$emit('update:visible', false);
    }
  }
}
</script>

<style lang="scss" scoped>
.dialog-content {
  font-size: 16px;
  margin-top: 8px;
}
</style>