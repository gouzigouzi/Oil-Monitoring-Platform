<template>
    <el-dialog :title="title" :visible.sync="localDialogFormVisible" :width="width" custom-class="custom-dialog" :before-close="handleBeforeClose">
        <!-- <div v-if="title === '新增车辆'" class="add-vehicle-warning">
            新增车辆之前必须保证车辆数据已保存在数据库！
        </div> -->
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" :inline="false" :label-width="labelWidth" class="form-item">
            <el-row>
            <el-col :span="11">
                <el-form-item label="车辆编号" prop="deviceNo">
                <el-input v-model="ruleForm.deviceNo" :disabled="disabled"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="车辆型号" prop="deviceType">
                <el-input v-model="ruleForm.deviceType"></el-input>
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
                value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
            <el-col :span="11">
                <el-form-item label="发动机型号" prop="deviceEngineType">
                <el-input v-model="ruleForm.deviceEngineType"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="机油型号" prop="oilType">
                <el-input v-model="ruleForm.oilType"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
            <el-col :span="11">
                <el-form-item label="车辆负责人" prop="deviceManager">
                <el-input v-model="ruleForm.deviceManager"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="联系方式" prop="deviceManagerPhone">
                <el-input v-model="ruleForm.deviceManagerPhone"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row>
              <el-col :span="11">
                <el-form-item label="车辆所在省份" prop="deviceLocationProvince">
                    <el-select v-model="ruleForm.deviceLocationProvince" placeholder="所在省份" @change="handleProvinceChange">
                        <el-option 
                          v-for="province in provinceList"
                          :key="province.name"
                          :label="province.name"
                          :value="province.name">
                        </el-option>
                    </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="11" style="margin-left: 20px;">
                <el-form-item label="车辆所在市" prop="deviceLocationCity">
                    <el-select v-model="ruleForm.deviceLocationCity" placeholder="所在市">
                        <el-option 
                          v-for="city in cities"
                          :key="city"
                          :label="city"
                          :value="city">
                        </el-option>
                    </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
            <el-form-item label="车辆所在地址" prop="deviceLocation">
                <el-input type="textarea" v-model="ruleForm.deviceLocation"></el-input>
            </el-form-item>
            </el-row>
            <div v-if="title === '新增车辆'" class="add-vehicle-warning">
                新增车辆之前必须保证车辆数据已保存在数据库！
            </div>
            <el-row type="flex" justify="center">
                <el-button type="primary" @click="submitForm('ruleForm')">{{buttonText}}</el-button>
                <el-button @click="resetForm('ruleForm')" style="margin-left: 30px;">取消</el-button>
            </el-row>
        </el-form>
    </el-dialog>
</template>

<script>
import { provinceList } from "./provinces/province.js"

export default {
  name: 'VehicleDialog',
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
    }
  },
  data() {
    return {
      localDialogFormVisible: this.dialogFormVisible,
      provinceList,
      cities: [],
      disabled: this.title==='新增车辆'? false:true
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
  computed: {
    buttonText() {
      return this.title === '车辆信息' ? '修改' : '新增';
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // Perform submit action
          this.$confirm('确认车辆信息无误?', '提示', {
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
    handleProvinceChange(value) {
      this.ruleForm.deviceLocationCity = '';
      if (value==='北京' || value==='上海' || value==='天津' || value==='重庆' || value==='台湾' || value==='香港' || value==='澳门'){
        this.ruleForm.deviceLocationCity = value;
        this.cities = [];
        return;
      }
      const selected = this.provinceList.find(province => province.name === value);
      var cityArray = new Array();
      if (value==='海南'){
        const cityList = selected.cityList
        for(var c=0; c<cityList.length-1; c++){
          var city = cityList[c];
          var cityName = city.name;
          cityArray[c] = cityName;
        }
        const areaList = selected.cityList[2].areaList
        console.log(areaList)
        for(var c=0; c<areaList.length; c++){
          cityArray[c+2] = areaList[c];
        }
        this.cities = cityArray;
      }else{
        if(selected.length!==0){
          const cityList = selected.cityList
          for(var c=0; c<cityList.length; c++){
            var city = cityList[c];
            var cityName = city.name;
            cityArray[c] = cityName;
          }
          this.cities = cityArray;
        }else{
          this.cities = []
        }
      }
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