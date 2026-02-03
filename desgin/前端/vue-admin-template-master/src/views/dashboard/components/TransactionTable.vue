<template>
  <el-table :data="listData" style="width: 100%;padding-top: 15px;" stripe="">
    <el-table-column label="车辆名称" min-width="100" prop="deviceName" align="center">
    </el-table-column>
    <el-table-column label="车辆所在地点" width="200" align="center" prop="deviceLocation">
    </el-table-column>
    <el-table-column label="上一次换油时间" width="150" align="center" prop="lastChangeDatetime">
    </el-table-column>
    <el-table-column label="机油状态" width="100" align="center" prop="oilStatus">
      <template slot-scope="{row}">
        <el-tag :type="statusType(row.oilStatus)">
          {{ statusText(row.oilStatus) }}
        </el-tag>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>

export default {
  props: {
      listData: {
        type: Array,
        required: true
      }
  },
  filters: {
    orderNoFilter(str) {
      return str.substring(0, 30)
    }
  },
  data() {
    return {
      // list: [{
      //     last_change_date: '2024-05-02',
      //     device_no: '舟山-1',
      //     address: '上海市普陀区金沙江路 1518 弄',
      //     status: '健康'
      //   }, {
      //     last_change_date: '2024-02-04',
      //     device_no: '温州-2',
      //     address: '上海市普陀区金沙江路 1517 弄',
      //     status: '需换油'
      //   }, {
      //     last_change_date: '2024-05-01',
      //     device_no: '温州-1',
      //     address: '上海市普陀区金沙江路 1519 弄',
      //     status: '健康'
      //   }, {
      //     last_change_date: '2016-05-03',
      //     device_no: '金华-3',
      //     address: '上海市普陀区金沙江路 1516 弄',
      //     status: '需换油'
      //   }]
    }
  },
  created() {
  },
  methods: {
    statusType(status) {
        const statusMap = {
          0: 'success',
          1: 'warning',
          2: 'danger'
        };
        return statusMap[status] || 'info'; // 默认类型
    },
    statusText(status) {
      const statusMap = {
        0: '健康',
        1: '已老化',
        2: '需换油'
      };
      return statusMap[status] || status; // 默认文本
    },
  }
}
</script>
