﻿var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{
            tbSpecification:{},
            optionList:[]
        },
        ids:[],
        searchEntity:{}
    },
    methods: {
        searchList:function (curPage) {
            axios.post('/specification/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;
                console.log(response)
                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/specification/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/specification/findPage.shtml',{params:{
                pageNo:this.pageNo
            }}).then(function (response) {
                console.log(app);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data.list;
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            }).catch(function (error) {

            })
        },
        //该方法只要不在生命周期的
        add:function () {
            axios.post('/specification/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log(error.data);
            });
        },
        update:function () {
            axios.post('/specification/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log(error.data);
            });
        },
        save:function () {
            if(this.entity.tbSpecification.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {

            axios.get('/specification/findOne/'+id+'.shtml').then(function (response) {
                console.log(response)
                app.entity = response.data;
            }).catch(function (error) {
                console.log(error.data);
            });
        },
        dele:function () {
            axios.post('/specification/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log(error.data);
            });
        },
        addTableRow:function () {
            this.entity.optionList.push({})
        },
        removeTableRow:function (index) {
            this.entity.optionList.splice(index,1)
        }

    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);

    }

})
