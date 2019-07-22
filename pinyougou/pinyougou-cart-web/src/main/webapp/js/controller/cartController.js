var app = new Vue({
    el:'#app',
    data:{
        cartList:[],
        addressList:[],
        address:{},
        order:{paymentType:'1'},
        totalNum:0,
        totalMoney:0,
        addressNew:{}
    },
    methods:{
        findCartList:function () {
            axios.get("/cart/findCartList.shtml").then(function (response) {
                app.cartList = response.data;
                app.totalNum = 0;
                app.totalMoney = 0;

                let allCart = response.data
                for (let i = 0;i<allCart.length;i++) {
                    let cart = allCart[i];
                    for(let j=0;j<cart.orderItemList.length;j++){
                        app.totalNum+=cart.orderItemList[j].num;
                        app.totalMoney+=cart.orderItemList[j].totalFee;
                    }
                }

            })
        },
        addGoodsToCartList:function (itemId, num) {
            axios.get("/cart/addGoodsToCartList.shtml",{
                params:{
                    itemId:itemId,
                    num:num
                }
            }).then(function (response) {
                if (response.data.success) {
                    app.findCartList()
                } else {
                    alert(response.data.message)
                }
            })
        },
        findAddressList:function () {
            axios.get("/address/findAddressByUserId.shtml").then(function (response) {
                app.addressList = response.data;
                for (var i = 0;i<app.addressList.length;i++) {
                    if (app.addressList[i].isDefault == '1') {
                        app.address = app.addressList[i];
                        break;
                    }
                }
            })
        },
        selectAddress:function (address) {
            this.address = address
        },
        isSelectedAddress:function (address) {
            if (address == this.address) {
                return true;
            }

            return false;
        },
        addAddress:function () {
            axios.post("/address/add.shtml",this.addressNew).then(function (response) {
                if (response.data.success) {
                    app.findAddressList();
                } else {
                    alert(response.data.message)
                }
            })
        },
        selectType:function (type) {
            console.log(type);
            //this.$set(this.order,"paymentType",type);
            this.order.paymentType = type
        },
        submitOrder:function () {
            this.order.receiveAreaName = this.address.address
            this.order.receiveMobile = this.address.mobile;
            this.order.receiver = this.address.contact;
            axios.post("/order/add.shtml",this.order).then(function (response) {
                if (response.data.success) {
                    window.location.href = "pay.html"
                } else {
                    alert(response.datat.message)
                }
            })
        }
    },

    created:function () {
        this.findCartList();
        if (window.location.href.indexOf("getOrderInfo.html") != -1) {
            this.findAddressList();
        }
    }
})