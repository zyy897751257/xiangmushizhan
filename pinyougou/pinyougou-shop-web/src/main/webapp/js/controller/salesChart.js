var app = new Vue({
    el:"#app",
    data:{
        startDate:"",
        endDate:""
    },
    methods:{
        searchChart:function () {

            // 基于准备好的dom，初始化echarts实例
            var myChart = echarts.init(document.getElementById('main'));

            axios.get("/salesChart/getSalesChart.shtml",{params:{
                startDate:this.startDate,
                endDate:this.endDate
            }}).then(function (response) {
                if (response.data.flag) {
                    myChart.setOption({
                        title: {
                            text: '销售额'
                        },
                        tooltip: {},
                        legend: {
                            data:['销售额']
                        },

                        xAxis: {
                            data: response.data.dates
                        },
                        yAxis: {
                            type: 'value'
                        },
                        series: [{
                            name: '销售额',
                            /*data: response.data.saleCounts,*/
                            data:[160,24,38,400,456,600,1658,32,46,458],
                            type: 'line'
                        }]
                    });
                } else {
                    alert(response.data.message)
                }
            })
        }
    }
})