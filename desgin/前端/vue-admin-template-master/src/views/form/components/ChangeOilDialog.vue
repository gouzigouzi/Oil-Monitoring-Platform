<template>
    <el-dialog
      title="车辆换油时间"
      :before-close="closeDialog"
      :visible.sync="localVisible"
      width="30%"
      center>
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" :inline="false" :label-width="labelWidth" class="form-item">
        <el-row>
            <el-form-item label="机油更换日期" prop="lastChangeDatetime" label-width="120px">
                <el-col :span="18">
                <el-date-picker 
                type="datetime" 
                placeholder="机油更换日期" 
                v-model="ruleForm.lastChangeDatetime" 
                style="width: 100%;" 
                value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
                </el-col>
            </el-form-item>
        </el-row>
        <el-row type="flex" justify="center">
            <el-button type="primary" @click="submitForm('ruleForm')">确 定</el-button>
            <el-button @click="closeDialog" style="margin-left: 30px;">取 消</el-button>
        </el-row>
      </el-form>
      <!-- <span slot="footer" class="dialog-footer">
        
      </span> -->
    </el-dialog>
  </template>
  
  <script>
import {changeOil} from '@/api/oillife'

  export default {
    name: 'ChangeOilDialog',
    props: {
      visible: {
        type: Boolean,
        required: true
      },
      rules: {
        type: Object,
        required: true
      },
      ruleForm: {
        type: Object,
        required: true
      },
    },
    data() {
      return {
        localVisible: this.visible,
        form: {
            deviceNo: '',
            lastChangeDatetime: ''
        }
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
      submitForm(formName){
        this.$refs[formName].validate((valid) => {
        if (valid) {
          // Perform submit action
          this.$confirm('确认换油时间无误?', '提示', {
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
      closeDialog() {
        // this.ruleForm.lastChangeDatetime = '';
        this.$refs.ruleForm.resetFields();
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