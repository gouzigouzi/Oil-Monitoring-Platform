<template>
    <el-dialog :title="title" :visible.sync="localDialogFormVisible" :width="width" custom-class="custom-dialog" :before-close="handleBeforeClose">
        <!-- <div v-if="title === '新增车辆'" class="add-vehicle-warning">
            新增车辆之前必须保证车辆数据已保存在数据库！
        </div> -->
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" :inline="false" :label-width="labelWidth" class="form-item">
            <el-row style="padding-left: 20px;">
            <el-col :span="18">
                <el-form-item label="用户名" prop="username">
                <el-input v-model="ruleForm.username"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row style="padding-left: 20px;">
            <el-col :span="18">
                <el-form-item label="所属公司" prop="company">
                <el-input v-model="ruleForm.company"></el-input>
                </el-form-item>
            </el-col>
            </el-row>
            <el-row type="flex" justify="center">
                <el-button type="primary" @click="submitForm('ruleForm')">新增</el-button>
                <el-button @click="resetForm('ruleForm')" style="margin-left: 30px;">取消</el-button>
            </el-row>
        </el-form>
    </el-dialog>
</template>

<script>

export default {
  name: 'UserDialog',
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
  computed: {
  },
  methods: {
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