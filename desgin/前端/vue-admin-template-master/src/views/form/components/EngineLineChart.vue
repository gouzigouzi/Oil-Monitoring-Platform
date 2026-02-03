<template>
    <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'
import {engineLine} from '@/api/oillife';

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
    engineLine(this.deviceNo).then(res=>{
      console.log('engineLine:',res);
      this.chartData.date = res.data.date;
      this.chartData.oilTempData = res.data.oilTempData;
      this.chartData.engineSpeedData = res.data.engineSpeedData;
      this.chartData.engineLoadData = res.data.engineLoadData;
      this.$nextTick(() => {
      console.log('engineLineChart:',this.chartData)
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
    setOptions({ type, date, oilTempData, engineSpeedData, engineLoadData } = {}) {
      this.chart.setOption({
        xAxis: {
          data: date,
          boundaryGap: false,
          axisTick: {
            show: false
          }
        },
        grid: {
          left: 10,
          right: 10,
          bottom: 20,
          top: 60,
          containLabel: true
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          },
          padding: [5, 10]
        },
        yAxis: {
          axisTick: {
            show: false
          }
        },
        legend: {
            top: "28",
            data: type
        },
        title: {
            show: true,
            left: "center",
            top: "0",
            text: "发动机工况数据示意图",
            textStyle: {
            color: "#666666",
            fontSize: 18,
            fontWeight: 'bold'
            }
        },
        series: [{
          name: type[0], 
          itemStyle: {
            normal: {
              color: '#e47470',
              lineStyle: {
                color: '#ad3832',
                width: 2
              },
              borderWidth: 8,
            },
          },
          smooth: true,
          type: 'line',
          data: oilTempData,
          animationDuration: 2800,
          animationEasing: 'cubicInOut'
        },
        {
          name: type[1], 
          itemStyle: {
            normal: {
              color: '#7ec050',
              lineStyle: {
                color: '#377e22',
                width: 2
              },
              borderWidth: 8,
            },
          },
          smooth: true,
          type: 'line',
          data: engineSpeedData,
          animationDuration: 2800,
          animationEasing: 'cubicInOut'
        },
        {
          name: type[2], 
          itemStyle: {
            normal: {
              color: '#ebc069',
              lineStyle: {
                color: '#f0d08f',
                width: 2
              },
              borderWidth: 8,
            },
          },
          smooth: true,
          type: 'line',
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