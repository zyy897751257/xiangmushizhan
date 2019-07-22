var app = new Vue({
    el:"#app",
    data:{
        pages: 15,
        pageNo: 1,
        list: [],
        entity: {},
        ids: [],
        seckillId:0,
        messageInfo:"",
        goodsInfo:{count:0},
        timeString:'',
        searchEntity: {}
    },
    methods:{
        //转换时间
        convertTimeString:function (alltime) {
            var allsecond = Math.floor(alltime/1000);//毫秒转成秒
            var days = Math.floor(allsecond/(60*60*24));//天数
            var hours = Math.floor((allsecond-days*60*60*24)/(60*60));//小时
            var minutes = Math.floor((allsecond - days*60*60*24 - hours*60*60)/60);//分钟
            var seconds = allsecond - days*60*60*24 - hours*60*60 - minutes*60;//秒数

            days = days + "天";

            if (hours < 10) {
                hours = "0" + hours;
            }
            if (minutes < 10) {
                minutes = "0" + minutes;
            }
            if (seconds < 10) {
                seconds = "0" + seconds;
            }

            return days + hours + ":" + minutes + ":" + seconds;
        },

        caculate:function (alltime) {
            var clock = window.setInterval(function () {
                alltime = alltime - 1000;
                app.timeString = app.convertTimeString(alltime);
                if (alltime<0) {
                    window.clearInterval(clock);
                }
            },1000)
        },
        submitOrder:function () {
            axios.get("/seckillOrder/submitOrder.shtml?id="+this.seckillId).then(function (response) {
                if (response.data.success) {
                    app.queryStatus();
                } else {
                    if (response.data.message == "403") {
                        var url = window.location.href
                        window.location.href = "http://localhost:9111/page/login.shtml?url="+url
                    } else {
                        app.messageInfo=response.data.message;
                    }
                }

            })
        },
        queryStatus:function() {
            var count = 0;

            var queryOrder = window.setInterval(function () {
                count += 1;
                axios.get("/seckillOrder/queryOrderStatus.shtml").then(function (response) {
                    if (response.data.success) {
                        window.clearInterval(queryOrder);
                        //跳转支付页面
                        window.location.href="http://localhost:9111/pay.html"
                    } else {
                        if (response.data.message == "403")  {
                            var url = window.location.href
                            window.location.href = "http://localhost:9111/page/login.shtml?url="+url
                        } else {
                            app.messageInfo = response.data.message + "......" + count
                        }
                    }
                })
            },1000)
        },
        getGoodsInfoById:function (id) {
            axios.get("/seckillGoods/getGoodsInfoById.shtml?id="+id).then(function (response) {
                app.caculate(response.data.time);
                app.goodsInfo.count = response.data.count
            })
        }

    },
    created:function () {

        var obj = this.getUrlParam();
        this.getGoodsInfoById(obj.id);
        this.seckillId = obj.id;

    }
})