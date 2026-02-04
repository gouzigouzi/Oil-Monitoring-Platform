<template>
  <el-table :data="chartData" :class="className" :style="{height:height,width:width}" stripe>
    <el-table-column label="车辆编号" min-width="150" prop="deviceNo" align="center">
    </el-table-column>
    <el-table-column label="历史换油时间" width="195" align="center" prop="oilChangeDate">
      <template slot-scope="scope">
            <i class="el-icon-time"></i>
            <span style="margin-left: 10px">{{ scope.row.oilChangeDate.toLocaleString().replace(/T/g, ' ').substr(0,16) }}</span>
          </template>
    </el-table-column>
    <el-table-column label="机油运行时间" width="145" align="center" prop="oilRuntime">
      <template slot-scope="{row}">
          {{ row.oilRuntime+'小时' }}
      </template>
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
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '330px'
    },
    chartData: {
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
        2: '已失效'
      };
      return statusMap[status] || status; // 默认文本
    }
  }
}
</script>
