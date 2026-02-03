<template>
  <div class="echarts">
    <div style="width:100;height: 700px;padding-bottom: 20px;" ref="sctterMap"></div>
    <div class="mapChoose">
      <el-button v-if="parentInfo.length > 1" @click="goBack">返回</el-button>
    </div>
  </div>
</template>

<script>
  import echarts from "echarts";
  import resize from "./mixins/resize";
  import {vehicleScatter} from "../../../api/vehicle"
  export default {
    name: "sctterMap",
    mixins: [resize],
    props: {
      mapData: Array
    },
    // watch: {
    //   mapData: {
    //     handler(newData) {
    //       this.initEcharts(newData);
    //     },
    //     deep: true
    //   }
    // },
    data() {
      return {
        chart: null,
        geoJson: {
          features: []
        },
        parentInfo: [{
          cityName: "全国",
          code: 100000
        }],
      };
    },
    mounted() {
      this.getGeoJson(100000);
      // this.loadAMapUI();
    },
    methods: {
      getGeoJson(adcode) {
        let that = this;
        AMapUI.loadUI(["geo/DistrictExplorer"], DistrictExplorer => {
          var districtExplorer = new DistrictExplorer();
          districtExplorer.loadAreaNode(adcode, function (error, areaNode) {
            if (error) {
              console.error(error);
              return;
            }
            let Json = areaNode.getSubFeatures();
            if (Json.length > 0) {
              that.geoJson.features = Json;
            } else if (Json.length === 0) {
              that.geoJson.features = that.geoJson.features.filter(
                item => item.properties.adcode == adcode
              );
              if (that.geoJson.features.length === 0) return;
            }
            console.log('city1', that.parentInfo)
            if(that.parentInfo.length==1){
              console.log('city1', that.parentInfo[0].cityName)
              that.getMapData(that.parentInfo[0].cityName);
            }else if(that.parentInfo.length==2){
              console.log('city2', that.parentInfo[1].cityName);
              that.getMapData(that.parentInfo[1].cityName);
            }else{
              that.getMapData('city');
            }
          });
        });
      },
      //获取数据
      getMapData(cityName) {
        vehicleScatter(cityName).then(res=>{
          let mapData = this.geoJson.features.map(item => {
            const matchedCity = res.data.find(city => city.cityName === item.properties.name);
            console.log('match', matchedCity);
            return {
              name: item.properties.name,
              value: matchedCity ? matchedCity.vehicleCount : 0,
              cityCode: item.properties.adcode
            };
          });
          mapData = mapData.sort(function (a, b) {
            return b.value - a.value;
          });
          console.log(mapData);
          this.initEcharts(mapData);
        }).catch(()=>{
          this.$message({
            type: 'error',
            message: '获取车辆分布数据失败'
          });
        })
      },
      initEcharts(mapData) {
        var min = mapData[mapData.length - 1].value;
        var max = mapData[0].value;
        if (mapData.length === 1) {
          min = 0;
        }
        this.chart = echarts.init(this.$refs.sctterMap);
        console.log(this.chart)
        echarts.registerMap("Map", this.geoJson); //注册
        this.chart.setOption({
            tooltip: {
              trigger: "item",
              formatter: p => {
                let val = p.value;
                if (!val) {
                  val = 0;
                }
                let txtCon = p.name + ":" + val.toFixed(2);
                return txtCon;
              }
            },
            title: {
              show: true,
              left: "center",
              top: "15",
              text: this.parentInfo[this.parentInfo.length - 1].cityName +
                "车辆分布图",
              textStyle: {
                color: "#666666",
                fontSize: 20
              }
            },
            toolbox: {
              feature: {
                restore: {
                  show: false
                },
                dataZoom: {
                  show: false
                },
                magicType: {
                  show: false
                }
              },
              iconStyle: {
                normal: {
                  borderColor: "#1990DA"
                }
              },
              top: 15,
              right: 35
            },
            visualMap: {
              min: min,
              max: max,
              left: "3%",
              bottom: "5%",
              calculable: true,
              seriesIndex: [0],
              inRange: {
                color: ["#0D96F1", "#3a8abc","#105389"]
              },
              textStyle: {
                color: "#24CFF4"
              }
            },
            series: [{
              name: "地图",
              type: "map",
              map: "Map",
              roam: true, //是否可缩放
              zoom: 1.2, //缩放比例
              data: mapData,
              label: {
                normal: {
                  show: true,
                  color: "rgb(249, 249, 249)", //省份标签字体颜色
                  formatter: p => {
                    switch (p.name) {
                      case "内蒙古自治区":
                        p.name = "内蒙古";
                        break;
                      case "西藏自治区":
                        p.name = "西藏";
                        break;
                      case "新疆维吾尔自治区":
                        p.name = "新疆";
                        break;
                      case "宁夏回族自治区":
                        p.name = "宁夏";
                        break;
                      case "广西壮族自治区":
                        p.name = "广西";
                        break;
                      case "香港特别行政区":
                        p.name = "香港";
                        break;
                      case "澳门特别行政区":
                        p.name = "澳门";
                        break;
                      default:
                        break;
                    }
                    return p.name;
                  }
                },
                emphasis: {
                  show: true,
                  color: "#f75a00"
                }
              },
              itemStyle: {
                normal: {
                  areaColor: "#24CFF4",
                  borderColor: "#53D9FF",
                  borderWidth: 1.3,
                  shadowBlur: 15,
                  shadowColor: "rgb(58,115,192)",
                  shadowOffsetX: 7,
                  shadowOffsetY: 6
                },
                emphasis: {
                  areaColor: "#8dd7fc",
                  borderWidth: 1.6,
                  shadowBlur: 25
                }
              }
            }]
          },
          true
        );
        let that = this;
        this.chart.off("click");
        this.chart.on("click", params => {
          // console.log(params.data)
          // this.$emit('province-click', { cityCode: params.data.cityCode, name: params.name });
          if (
            that.parentInfo[that.parentInfo.length - 1].code ==
            params.data.cityCode
          ) {
            return;
          }
          let data = params.data;
          that.parentInfo.push({
            cityName: data.name,
            code: data.cityCode
          });
          that.getGeoJson(data.cityCode);
        });
      },


      //选择切换市县
      chooseArea(val, index) {
        if (this.parentInfo.length === index + 1) {
          return
        }
        this.parentInfo.splice(index + 1)
        this.getGeoJson(this.parentInfo[this.parentInfo.length - 1].code);
      },

      goBack() {
      if (this.parentInfo.length > 1) {
        this.parentInfo.pop();
        this.getGeoJson(this.parentInfo[this.parentInfo.length - 1].code);
      }
    }
    }
  };
</script>
<style lang="scss" scoped>
  .echarts {
    width: 100%;
    height: 100%;
    position: relative;
    background-size: 100% 100%;
  }

  .mapChoose {
    position: absolute;
    left: 20px;
    top: 55px;
    color: #eee;


    .title {
      padding: 5px;
      border-top: 1px solid rgba(147, 235, 248, .8);
      border-bottom: 1px solid rgba(147, 235, 248, .8);
      cursor: pointer;
    }

    .icon {
      font-family: "simsun";
      font-size: 25px;
      margin: 0 11px;
    }
  }
</style>