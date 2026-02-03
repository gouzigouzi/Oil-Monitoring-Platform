<template>
    <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

const animationDuration = 6000

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
            default: '300px'
        },
        chartData: {
            type: Array,
            required: true
        }
    },
    data() {
        return {
            chart: null
        }
    },
    watch: {
        chartData: {
            handler(newData) {
                this.initChart(newData);
            },
            deep: true
        }
    },
    mounted() {
        this.$nextTick(() => {
            this.initChart(this.chartData)
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
        initChart(chartData) {
            this.chart = echarts.init(this.$el, 'macarons')
            this.chart.setOption({
                title: {
                    text: '车辆累计运行时间排行榜',
                    show: true,
                    left: "center",
                    top: "15",
                    textStyle: {
                        color: "#666666",
                        fontSize: 20,
                        fontWeight: 'bold'
                    }
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    }
                },
                legend: {},
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'value',
                    boundaryGap: [0, 0.01]
                },
                yAxis: {
                    inverse: true,
                    type: 'category',
                    data: chartData.map(item => item.deviceName)
                },
                series: [
                    {
                        type: 'bar',
                        data: chartData.map(item => item.deviceAccumulatedRuntime)
                    }
                ]   
            })
        }
    }
}
</script>
