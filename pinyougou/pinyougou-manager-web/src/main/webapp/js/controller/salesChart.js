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
                        title : {
                            text: '销售饼状图',
                            x:'center'
                        },
                        tooltip : {
                            trigger: 'item',
                            formatter: "{a} <br/>{b} : {c} ({d}%)"
                        },
                        legend: {
                            type: 'scroll',
                            orient: 'vertical',
                            right: 10,
                            top: 20,
                            bottom: 20,
                            data: response.data.allSeller,

                            selected: response.data.selected
                        },
                        series : [
                            {
                                name: '商家名称',
                                type: 'pie',
                                radius: '75%',
                                center: ['40%', '50%'],
                                data: response.data.seriesData,
                                itemStyle: {
                                    emphasis: {
                                        shadowBlur: 10,
                                        shadowOffsetX: 0,
                                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                                    }
                                }
                            }
                        ]
                    });
                } else {
                    alert(response.data.message)
                }
            })
        }
    }
})