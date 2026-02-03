<template>
    <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'
import {engineBar} from '@/api/oillife';

export default {
  mixins: [resize],
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
    autoResize: {
      type: Boolean,
      default: true
    },
    deviceNo: {
      type: String,
      required: true
    }
    // chartData: {
    //   type: Object,
    //   required: true
    // }
  },
  data() {
    return {
      chart: null,
      chartData: {
        type: ['oil_temperature', 'engine_speed', 'engine_load'],
        date: [],
        oilTempData: [],
        engineSpeedData: [],
        engineLoadData: []
      }
    }
  },
  mounted() {
    engineBar(this.deviceNo).then(res=>{
      console.log('engineBar:',res);
      this.chartData.date = res.data.date;
      this.chartData.oilTempData = res.data.oilTempData;
      this.chartData.engineSpeedData = res.data.engineSpeedData;
      this.chartData.engineLoadData = res.data.engineLoadData;
      this.$nextTick(() => {
        this.initChart()
      })
    })
    
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    },
    setOptions({ date, oilTempData, engineSpeedData, engineLoadData } = {}) {
      this.chart.setOption({
        grid: {
          top: 55,
          left: '0',
          right: '0',
          bottom: '3%',
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
        },
        xAxis: {
          data: date,
          type: 'category',
          boundaryGap: true,
          axisTick: {
            alignWithLabel: true,
          }
        },
        yAxis: [{
          type: 'value',
          axisTick: {
            show: false
          }
        }],
        legend: {
            top: "28",
            data: ['oil_temperature', 'engine_speed','engine_load']
        },
        title: {
            show: true,
            left: "center",
            top: "0",
            text: "机油每日消耗占比",
            textStyle: {
            color: "#666666",
            fontSize: 18,
            fontWeight: 'bold'
            }
        },
        series: [{
          name: 'oil_temperature', 
          barWidth: '60%',
          type: 'bar',
          stack: 'vistors',
          itemStyle: {
              color: '#e47470' // Specify the color here
          },
          data: oilTempData,
          animationDuration: 2800,
          animationEasing: 'cubicInOut'
        },
        {
          name: 'engine_speed', 
          barWidth: '50%',
          type: 'bar',
          stack: 'vistors',
          itemStyle: {
              color: '#7ec050' // Specify the color here
          },
          data: engineSpeedData,
          animationDuration: 2800,
          animationEasing: 'cubicInOut'
        },
        {
          name: 'engine_load', 
          barWidth: '50%',
          type: 'bar',
          stack: 'vistors',
          itemStyle: {
              color: '#f0d08f' // Specify the color here
          },
          data: engineLoadData,
          animationDuration: 2800,
          animationEasing: 'cubicInOut'
        }
        ]
      })
    }
  }
}
</script>