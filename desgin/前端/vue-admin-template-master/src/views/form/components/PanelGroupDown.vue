<template>
  <el-row :gutter="40" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
        <div class="card-panel-icon-wrapper icon-people">
          <svg-icon icon-class="date" class-name="card-panel-icon"/>
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            上次换油时间
          </div>
          <div class="card-panel-num">
            {{chartData.lastChangeDatetime.toLocaleString().replace(/T/g, ' ').substr(0,10)}}
          </div>
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('messages')">
        <div class="card-panel-icon-wrapper icon-message">
          <svg-icon icon-class="life" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            机油剩余寿命
          </div>
          <div class="card-panel-num">
            <count-to :start-val="0" :end-val=chartData.oilLifeTimePercent :duration="1000" style="font-size: 26px"/>
          </div>
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('purchases')">
        <div class="card-panel-icon-wrapper icon-money">
          <svg-icon icon-class="health" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            机油健康程度
          </div>
          <div class="card-panel-num" :style=statusType(chartData.oilLifeTimePercent)>
            {{statusText(chartData.oilLifeTimePercent)}}
          </div>
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel">
        <div class="card-panel-icon-wrapper icon-shopping">
          <svg-icon icon-class="oil" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            车辆换油次数
          </div>
          <div class="card-panel-num">
            <count-to :start-val="0" :end-val=chartData.changeCount :duration="1000" style="font-size: 26px"/>
          </div>
        </div>
      </div>
    </el-col>
  </el-row>
</template> 

<script>
import CountTo from 'vue-count-to'
import { status } from 'nprogress';

export default {
  components: {
    CountTo
  },
  props: {
    chartData: {
      type: Object,
      required: true
    }
  },
  methods: {
    statusType(data) {
        let status = 0;
        if (data>30){
          status = 0;
        }else if(data<=30 && data>10){
          status = 1;
        }else{
          status = 2;
        }
        const statusMap = {
          0: 'color: green',
          1: 'color: orange',
          2: 'color: red'
        };
        return statusMap[status] || 'color: red'; // 默认类型
    },
    statusText(data) {
      let status = 0;
      if (data>30){
        status = 0;
      }else if(data<=30 && data>10){
        status = 1;
      }else{
        status = 2;
      }
      const statusMap = {
        0: '健康',
        1: '已老化',
        2: '需换油'
      };
      return statusMap[status] || '健康'; // 默认文本
    },
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 0px;

  .card-panel-col {
    margin-bottom: 32px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    border-radius: 20px;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);

    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }

      .icon-people {
        background: #40c9c6;
      }

      .icon-message {
        background: #36a3f7;
      }

      .icon-money {
        background: #fdd292;
      }

      .icon-shopping {
        background: #8fe674;
      }
    }

    .icon-people {
      color: #94ddfa;
    }

    .icon-message {
      color: #36a3f7;
    }

    .icon-money {
      color: #f4516c;
    }

    .icon-shopping {
      color: #34bfa3
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    card-panel-icon-wrapper-loader {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
    }
    .card-panel-icon-loader {
      float: left;
      font-size: 60px;
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;
      // margin-right: 100px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 22px;
        margin-bottom: 12px;
        text-align: right;
      }

      .card-panel-num {
        text-align: right;
        font-size: 26px;
      }
    }
  }
}

@media (max-width:550px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;

    .svg-icon {
      display: block;
      margin: 14px auto !important;
      float: none !important;
    }
  }
}
</style>
