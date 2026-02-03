<template>
    <el-dialog :title="title" :visible.sync="localDialogFormVisible" :width="width" custom-class="custom-dialog" :before-close="handleBeforeClose">
        <el-table
            element-loading-text="Loading"
            :data="ruleForm"
            ref="multipleTable"
            border
            fit
            stripe
            highlight-current-row
            height="300"
        >
        <el-table-column align="center" label="车辆编号" prop="deviceNo" width="160">
        </el-table-column>
        <el-table-column align="center" label="车辆名称" prop="deviceName" width="160">
        </el-table-column>
        <el-table-column align="center" label="车辆所在地址" prop="deviceLocation">
        </el-table-column>
        <el-table-column align="center" prop="lastChangeDatetime" label="机油更换时间" width="160">
          <template slot-scope="scope">
            <i class="el-icon-time"></i>
            <span style="margin-left: 10px">{{ scope.row.lastChangeDatetime.toLocaleString().replace(/T/g, ' ').substr(0,16) }}</span>
          </template>
        </el-table-column>
        </el-table>
    </el-dialog>
</template>

<script>

export default {
  name: 'ViewDeleteVehicleDialog',
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
      type: Array,
      required: true
    },
    width: {
      type: String,
      default: '50%'
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