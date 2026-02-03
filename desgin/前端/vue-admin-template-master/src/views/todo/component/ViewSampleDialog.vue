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
                <el-form-item label="发动机型号" prop="engineType">
                  <el-input v-model="ruleForm.engineType" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
            <el-col :span="11">
                <el-form-item label="机油周期" prop="oilCycle">
                <el-input v-model.number="ruleForm.oilCycle" :disabled="disabled"></el-input>
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
                <el-form-item label="机油更换时间" prop="oilChangeDate">
                <el-date-picker type="datetime" placeholder="机油更换日期" v-model="ruleForm.oilChangeDate" style="width: 100%;" :disabled="disabled" value-format="yyyy-MM-dd hh:mm:ss"></el-date-picker>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="样本采样日期" prop="oilSampleDate">
                <el-date-picker type="datetime" placeholder="样本采样日期" v-model="ruleForm.oilSampleDate" style="width: 100%;" :disabled="disabled" value-format="yyyy-MM-dd hh:mm:ss"></el-date-picker>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
            <el-col :span="11">
                <el-form-item label="机油酸值" prop="oilTan">
                <el-input v-model="ruleForm.oilTan" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="机油碱值" prop="oilTbn">
                <el-input v-model="ruleForm.oilTbn" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
            <el-col :span="11">
                <el-form-item label="机油水分" prop="oilWater">
                <el-input v-model="ruleForm.oilWater" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="机油粘度" prop="oilViscosity">
                <el-input v-model="ruleForm.oilViscosity" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
            <el-col :span="11">
                <el-form-item label="机油铁含量" prop="oilFe">
                <el-input v-model="ruleForm.oilFe" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="机油铜含量" prop="oilCu">
                <el-input v-model="ruleForm.oilCu" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
            <el-col :span="11">
                <el-form-item label="机油铝含量" prop="oilAl">
                <el-input v-model="ruleForm.oilAl" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="机油硅含量" prop="oilSi">
                <el-input v-model="ruleForm.oilSi" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
              <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="机油周期终点" prop="isEnd">
                  <el-switch 
                    v-model="ruleForm.isEnd" 
                    :disabled="disabled"
                    active-value="1"
                    inactive-value="0"
                    ></el-switch>
                </el-form-item>
              </el-col>
            </el-row>
        </el-form>
    </el-dialog>
</template>

<script>

export default {
  name: 'ViewSampleDialog',
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
    resetForm(formName) {
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