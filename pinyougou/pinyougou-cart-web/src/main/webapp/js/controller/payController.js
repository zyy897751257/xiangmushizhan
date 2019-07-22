var app = new Vue({
    el:"#app",
    data:{
        payObject:{}
    },
    methods:{
        createNative:function () {
            axios.get("/pay/createNative.shtml").then(function (response) {
                if (response.data) {
                    app.payObject = response.data
                    app.payObject.total_fee = app.payObject.total_fee/100;
                    var qr = new QRious({
                        element: document.getElementById('qrious'),
                        size:250,
                        level:'H',
                        value:app.payObject.code_url
                    });
                    if (qr) {
                        app.queryPayStatus(app.payObject.out_trade_no);
                    }
                }

            })
        },
        queryPayStatus:function (out_trade_no) {
            axios.get("/pay/queryPayStatus.shtml",{
                params:{
                    out_trade_no:out_trade_no
                }
            }).then(function (response) {
                if (response.data) {
                    if (response.data.success) {
                        //支付成功
                        window.location.href="paysuccess.html?money="+app.payObject.total_fee;
                    } else {
                        //支付失败
                        if (response.data.message == '超时') {
                            app.createNative();
                        }
                        window.location.href="payfail.html";
                    }
                } else {
                    alert("错误")
                }

            })
        }
    },

    created:function () {
        //页面一加载就应当调用
        if(window.location.href.indexOf("pay.html")!=-1){
            this.createNative();
        }else {
            let urlParamObject = this.getUrlParam();
            if(urlParamObject.money)
                this.totalMoney=urlParamObject.money;
        }
    }
})