<template>
    <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'
import {life} from '@/api/oillife';

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
        date: [],
        life: []
      }
    }
  },
  mounted() {
    life(this.deviceNo).then(res=>{
      console.log('life:', res);
      this.chartData.date = res.data.date;
      this.chartData.life = res.data.life;
      this.$nextTick(() => {
        console.log(this.chartData)
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
    setOptions({ date, life } = {}) {
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
        title: {
            show: true,
            left: "center",
            top: "10",
            text: "机油剩余寿命百分比曲线",
            textStyle: {
            color: "#666666",
            fontSize: 20,
            fontWeight: 'bold'
            }
        },
        series: [{
          name: 'oil_life_percent', 
          itemStyle: {
            normal: {
              color: '#63c4c7',
              lineStyle: {
                color: '#5a92d0',
                width: 2
              },
              borderWidth: 8,
            },
          },
          smooth: true,
          type: 'line',
          data: life,
          areaStyle: {},
          animationDuration: 2800,
          animationEasing: 'cubicInOut'
        }]
      })
    }
  }
}
</script>