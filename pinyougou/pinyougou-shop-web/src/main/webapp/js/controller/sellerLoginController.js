var app = new Vue({
    el:"#app",
    data:{
        username:""
    },
    methods:{
        getUsername:function () {
            axios.get("/login/getSellername.shtml").then(function (response) {
                console.log(response.data)
                app.username = response.data
            }).catch(function (error) {
                console.log(error.data)
            })
        }
    },
    created:function () {
        this.getUsername()
    }
})