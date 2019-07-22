var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{parentId:0},
        ids:[],
        searchEntity:{},
        entity1:{},
        entity2:{},
        grade:1
    },
    methods: {
        searchList:function (p_entity) {

            if (this.grade == 1) {
                this.entity1 = {}
                this.entity2 = {}
            } else if (this.grade == 2) {
                this.entity1 = p_entity
                this.entity2 = {}
            } else if (this.grade == 3) {
                this.entity2 = p_entity
            }

            this.findAll(p_entity.id)

            /*axios.post('/itemCat/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });*/
        },
        //查询所有品牌列表
        findAll:function (id) {
            console.log(app);
            axios.get('/itemCat/findByParentId/'+id+'.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;
                app.entity.parentId = id
            }).catch(function (error) {

            })
        },
         findPage:function (id) {
            var that = this;
            axios.get('/itemCat/findPage.shtml',{params:{
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
            console.log(this.entity)
            axios.post('/itemCat/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(entity.parentId);

                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/itemCat/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/itemCat/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/itemCat/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList({id:0});
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList({id:0});

    }

})
