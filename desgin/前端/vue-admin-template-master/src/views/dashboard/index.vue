<template>
  <div class="dashboard-container">
    <panel-group :accumulatedData="accumulatedData"/>
    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px; border-radius: 20px;" :gutter="32">
        <el-col :xs="24" :sm="24" :lg="12" class="left">
          <scatter-map :mapData="mapData"/>
        </el-col>
        <el-col :xs="24" :sm="24" :lg="12" class="right">
          <el-row class="bar">
            <bar-chat :chartData="chartData"/>
          </el-row>
          <el-row style="padding-top: 20px;">
            <transaction-table :listData="listData"/>
          </el-row>
        </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import PanelGroup from './components/PanelGroup'
import scatterMap from './components/sctterMap'
import BarChat from './components/BarChat.vue'
import TransactionTable from './components/TransactionTable.vue'
import {rank, accumulated} from '@/api/vehicle'


export default {
  name: 'Dashboard',
  components: {
    PanelGroup,
    scatterMap,
    BarChat,
    TransactionTable
  },
  data() {
    return {
      mapData: [], // 存储中国地图的省份数据
      chartData: [], // 存储柱状图的数据
      listData: [],
      accumulatedData: {
        accumulatedVehicleCount: 0,
        accumulatedSavingMoney: 0,
        accumulatedExtraRuntime: 0,
        accumulatedSavedChangeCount: 0
      }
    }
  },
  computed: {
    ...mapGetters([
      'name'
    ])
  }, 
  methods: {
    dataLoad(){
      rank().then(res=>{
          console.log('rank:', res.data);
          this.chartData = res.data;
          this.listData = res.data;
        }
      )
    },
    accumulatedLoad(){
      accumulated().then(res=>{
        console.log('accumulated:', res.data);
        this.accumulatedData = res.data;
      })
    }
  },
  mounted() {
    // 初始加载中国地图的数据
    this.dataLoad();
    this.accumulatedLoad();
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;
  height: 100vh;
}
.left {
  height: 80%;
}
.right {
  height: 80%;
}
</style>
