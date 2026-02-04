<template>
    <div :class="className" :style="{height:height,width:width}" />
  </template>
  
  <script>
  import echarts from 'echarts'
  require('echarts/theme/macarons') // echarts theme
  import resize from './mixins/resize'
  import {life, engineBar, enginePie, changeHistory} from '@/api/oillife';

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
        deviceNo: {
          type: String,
          required: true
        }
    },
    data() {
      return {
        chart: null,
        chartData: {
          type: ['oil_temperature', 'engine_speed', 'engine_load'],
          value: []
        }
      }
    },
    mounted() {
      enginePie(this.deviceNo).then(res=>{
        console.log('enginePie:',res);
        this.chartData.value = res.data.value;
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
    
        setOptions({type, value}){
            this.chart.setOption({
                tooltip: {
                    trigger: 'item',
                    formatter: '{a} <br/>{b} : {c} ({d}%)'
                },
                legend: {
                    left: 'center',
                    bottom: '10',
                    data:  type
                },
                title: {
                    show: true,
                    left: "center",
                    top: "0",
                    text: "发动机危险工况数据占比",
                    textStyle: {
                    color: "#666666",
                    fontSize: 18,
                    fontWeight: 'bold'
                    }
                },
                series: [
                {
                    name: '发动机危险工况数据占比',
                    type: 'pie',
                    roseType: 'radius',
                    radius: [15, 95],
                    center: ['50%', '48%'],
                    data: [
                        { value: value[0], name: type[0] },
                        { value: value[1], name: type[1] },
                        { value: value[2], name: type[2] },
                    ],
                    itemStyle: {
                      color: function (params) {
                        // Define a color array
                        var colorList = ['#e47470', '#7ec050', '#f0d08f'];
                        return colorList[params.dataIndex];
                      }
                    },
                    animationEasing: 'cubicInOut',
                    animationDuration: 2600
                }
                ]
            })
        }
    }
  }
  </script>
  