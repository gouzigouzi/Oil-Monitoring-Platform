<template>
    <el-dialog :title="title" :visible.sync="localDialogFormVisible" :width="width" custom-class="custom-dialog" :before-close="handleBeforeClose">
        <!-- <div v-if="title === '新增车辆'" class="add-vehicle-warning">
            新增车辆之前必须保证车辆数据已保存在数据库！
        </div> -->
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" :inline="false" :label-width="labelWidth" class="form-item">
            <el-row>
            <el-col :span="11">
                <el-form-item label="车辆编号" prop="deviceNo">
                  <el-select v-model="ruleForm.deviceNo" placeholder="车辆编号" :disabled="disabled">
                    <el-option 
                      v-for="list in this.lists"
                      :key="list"
                      :label="list"
                      :value="list"></el-option>
                  </el-select>
                <!-- <el-input v-model="ruleForm.deviceNo" :disabled="disabled"></el-input> -->
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
                <el-date-picker type="datetime" placeholder="机油更换日期" v-model="ruleForm.oilChangeDate" style="width: 100%;" :disabled="disabled" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="样本采样日期" prop="oilSampleDate">
                <el-date-picker type="datetime" placeholder="样本采样日期" v-model="ruleForm.oilSampleDate" style="width: 100%;" :disabled="disabled" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
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
            <div v-if="title === '新增样本'" class="add-vehicle-warning">
                新增样本必须保证数据正确，后续无法更改数据！！数据正确性会影响模型参数！！
            </div>
            <div v-if="title === '新增样本'" style="margin-top: 20px;">
              <el-row type="flex" justify="center">
                  <el-button type="primary" @click="submitForm('ruleForm')">新增</el-button>
                  <el-button @click="resetForm('ruleForm')" style="margin-left: 30px;">取消</el-button>
              </el-row>
            </div>
        </el-form>
    </el-dialog>
</template>

<script>
import {deviceNoList} from '@/api/vehicle'

export default {
  name: 'SampleDialog',
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
    rules: {
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
      default: false
    }
  },
  created() {
    deviceNoList().then(res=>{
      console.log("deviceNoList:", res)
      this.lists = res.data;
    })
  },
  data() {
    return {
      localDialogFormVisible: this.dialogFormVisible,
      lists: ''
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
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // Perform submit action
          this.$confirm('确认样本信息无误?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            console.log('dialogForm:',this.ruleForm);
            this.$emit('submits', this.ruleForm);
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消'
            });
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
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
    loadDeviceNo() {

    }
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