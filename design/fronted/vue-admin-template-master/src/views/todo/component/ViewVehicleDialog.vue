<template>
    <el-dialog :title="title" :visible.sync="localDialogFormVisible" :width="width" custom-class="custom-dialog" :before-close="handleBeforeClose">
        <el-form :model="ruleForm" ref="ruleForm" :inline="false" :label-width="labelWidth" class="form-item">
            <el-row>
            <el-col :span="11">
                <el-form-item label="车辆编号" prop="deviceNo">
                <el-input v-model="ruleForm.deviceNo" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="车辆型号" prop="deviceType">
                <el-input v-model="ruleForm.deviceType" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
            <el-col :span="11">
                <el-form-item label="机油更换次数" prop="changeCount">
                <el-input v-model.number="ruleForm.changeCount" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="机油更换日期" prop="lastChangeDatetime">
                <el-date-picker 
                type="datetime" 
                placeholder="机油更换日期" 
                v-model="ruleForm.lastChangeDatetime" 
                style="width: 100%;" 
                :disabled="disabled"
                value-format="yyyy-MM-dd hh:mm:ss"></el-date-picker>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
            <el-col :span="11">
                <el-form-item label="发动机型号" prop="deviceEngineType">
                <el-input v-model="ruleForm.deviceEngineType" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="机油型号" prop="oilType">
                <el-input v-model="ruleForm.oilType" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
            <el-col :span="11">
                <el-form-item label="车辆负责人" prop="deviceManager">
                <el-input v-model="ruleForm.deviceManager" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="联系方式" prop="deviceManagerPhone">
                <el-input v-model="ruleForm.deviceManagerPhone" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
              <el-col :span="11">
                <el-form-item label="车辆所在省份" prop="deviceLocationProvince">
                    <el-select v-model="ruleForm.deviceLocationProvince" placeholder="所在省份" :disabled="disabled">
                    </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="车辆所在市" prop="deviceLocationCity">
                    <el-select v-model="ruleForm.deviceLocationCity" placeholder="所在市" :disabled="disabled">
                    </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
            <el-form-item label="车辆所在地址" prop="deviceLocation">
                <el-input type="textarea" v-model="ruleForm.deviceLocation" :disabled="disabled"></el-input>
            </el-form-item>
            </el-row>
        </el-form>
    </el-dialog>
</template>

<script>

export default {
  name: 'ViewVehicleDialog',
  props: {
    title: {
      type: String,
      required: true
    },
    dialogFormVisible: {
      type: Boolean,
      required: true
    },
    ruleForm: {
      type: Object,
      required: true
    },
    width: {
      type: String,
      default: '45%'
    },
    labelWidth: {
      type: String,
      default: '100px'
    },
    disabled: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      localDialogFormVisible: this.dialogFormVisible,
    };
  },
  watch: {
    dialogFormVisible(newVal) {
      this.localDialogFormVisible = newVal;
    },
    localDialogFormVisible(newVal) {
      this.$emit('update:dialogFormVisible', newVal);
    }
  },
  methods: {
    resetForm() {
      this.localDialogFormVisible = false;
      this.$refs.ruleForm.resetFields();
      this.cities = [];
      this.$emit('reset');
    },
    handleBeforeClose(done) {
      if(!this.disabled){
        this.resetForm(); // Reset the form
        done(); // Close the dialog
      }else{
        this.localDialogFormVisible = false;
      }
    },
  }
};
</script>

<style scoped>
.custom-dialog .el-form-item__label {
  font-size: 12px; /* 设置标签字体大小为12px */
}
.form-item .el-form-item__label {
  width: 100px; /* 设置标签宽度 */
}
.add-vehicle-warning {
  text-align: center;
  margin-bottom: 10px;
  color: red;
}
</style>