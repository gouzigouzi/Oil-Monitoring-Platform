<template>
    <el-dialog :title="title" :visible.sync="localDialogFormVisible" :width="width" custom-class="custom-dialog" append-to-body :before-close="handleBeforeClose">
        <el-form :model="ruleForm" :rules="this.rules" ref="ruleForm" :inline="false" :label-width="labelWidth" class="form-item">
            <el-row style="padding-left: 20px;">
            <el-col :span="18">
                <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="ruleForm.oldPassword" type="password"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row style="padding-left: 20px;">
            <el-col :span="18">
                <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="ruleForm.newPassword" type="password"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row style="padding-left: 20px;">
            <el-col :span="18">
                <el-form-item label="确认新密码" prop="confirmNewPassword">
                <el-input v-model="ruleForm.confirmNewPassword" type="password"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row type="flex" justify="center">
                <el-button type="primary" @click="submitForm('ruleForm')">修改</el-button>
                <el-button @click="resetForm('ruleForm')" style="margin-left: 30px;">取消</el-button>
            </el-row>
        </el-form>
    </el-dialog>
</template>

<script>

export default {
  name: 'ChangePasswordDialog',
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
      default: '30%'
    },
    labelWidth: {
      type: String,
      default: '100px'
    }
  },
  data() {
    return {
      localDialogFormVisible: this.dialogFormVisible,
      rules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码'},
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        confirmNewPassword: [
          { required: true, message: '请再次输入新密码'},
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
          { validator: this.validatePassword, trigger: 'blur' }
        ],
      },
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
  },
  methods: {
    validatePassword(rule, value, callback) {
      if (value!==this.ruleForm.newPassword) {
        return callback(new Error('两次密码必须一致'));
      }
      callback();
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // Perform submit action
          this.$confirm('确认信息无误?', '提示', {
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