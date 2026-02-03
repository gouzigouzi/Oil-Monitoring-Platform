<template>
    <el-dialog :title="title" :visible.sync="localDialogFormVisible" :width="width" custom-class="custom-dialog" :before-close="handleBeforeClose">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" :inline="false" :label-width="labelWidth" class="form-item">
            <el-form-item label="拒绝理由" prop="rejectReason">
                <el-radio-group v-model="ruleForm.rejectReason" size="medium">
                    <el-row>
                        <el-col :span="10">
                            <el-radio border label="车辆编号有误"></el-radio>
                        </el-col>
                        <el-col :span="10" style="margin-left:30px">
                            <el-radio border label="车辆信息有误"></el-radio>
                        </el-col>
                    </el-row>
                    <el-row style="margin-top:20px">
                        <el-col :span="10">
                            <el-radio border label="车辆不准删除"></el-radio>
                        </el-col>
                        <el-col :span="10" style="margin-left:30px">
                            <el-radio border label="机油样本信息有误"></el-radio>
                        </el-col>
                    </el-row>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="详细说明" prop="detailReason">
                <el-input
                type="textarea"
                maxlength="30"
                show-word-limit
                placeholder="请输入内容"
                v-model="ruleForm.detailReason">
                </el-input>
            </el-form-item>
            <el-row type="flex" justify="center">
                <el-button type="primary" @click="submitForm('ruleForm')">确定</el-button>
                <el-button @click="resetForm('ruleForm')" style="margin-left: 30px;">取消</el-button>
            </el-row>
        </el-form>
    </el-dialog>
</template>

<script>

export default {
  name: 'RejectDialog',
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
      default: '30%'
    },
    labelWidth: {
      type: String,
      default: '100px'
    },
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
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // Perform submit action
          this.$confirm('确认拒绝该申请?', '提示', {
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
      this.$emit('reset');
    },
    handleBeforeClose(done) {
        this.resetForm(); // Reset the form
        done(); // Close the dialog
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