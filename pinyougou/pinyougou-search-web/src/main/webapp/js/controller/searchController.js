var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchMap:{category:"",brand:"",spec:{},price:"",pageNo:1,pageSize:40,sortField:"",sortType:""},
        resultMap:{},
        searchEntity:{},
        pageLabels:[],
        preDott:false,
        nextDott:false

    },
    methods: {
        removeSearchParams:function (key) {
            if (key == 'category' || key == 'brand' || key == 'price') {
                this.searchMap[key] = '';
            } else {
                delete this.searchMap.spec[key];
            }


            this.searchList();
        },
        addSearchParams:function (key,value) {

            if (key == 'category' || key == 'brand' || key == 'price') {
                this.searchMap[key] = value;
            } else {
                this.searchMap.spec[key] = value;
            }


            this.searchList();
        },
        searchList:function () {
            axios.post('/itemSearch/search.shtml',this.searchMap).then(function (response) {
                //获取数据
                app.resultMap=response.data;
                //设置分页
                app.buildPageLabels()

                console.log(response.data)
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/item/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/item/findPage.shtml',{params:{
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
            axios.post('/item/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/item/update.shtml',this.entity).then(function (response) {
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
            axios.get('/item/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/item/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        buildPageLabels:function () {
            this.pageLabels = []
            let firstPage = 1
            let lastPage = this.resultMap.totalPages

            if (lastPage > 5) {
                //总页数大于5
                if (this.searchMap.pageNo <= 3) {
                    //当前页≤3
                    lastPage = 5
                    this.preDott = false
                    this.nextDott = true

                } else if (this.searchMap.pageNo >= lastPage - 2) {
                    //当前页为最后三页
                    firstPage = lastPage - 4
                    this.preDott = true
                    this.nextDott = true

                } else {
                    firstPage = this.searchMap.pageNo - 2
                    lastPage = this.searchMap.pageNo + 2
                    this.preDott = true
                    this.nextDott = false
                }
            } else {
                this.preDott = false
                this.nextDott = false
            }

            for (var i = firstPage;i <= lastPage;i++) {
                this.pageLabels.push(i)
            }
        },
        queryByPageNo:function (pageNo) {
            this.searchMap.pageNo = parseInt(pageNo)
            this.searchList()

        },
        doSort:function (sortField, sortType) {
            this.searchMap.sortField = sortField
            this.searchMap.sortType = sortType
            this.searchList()
        },
        isKeywordsIsBrand:function () {
            if (this.resultMap.brandList != null && this.resultMap.brandList.length > 0) {
                for (var i = 0; i < this.resultMap.brandList.length; i++) {
                    if (this.searchMap.keywords.indexOf(this.resultMap.brandList[i].text) != -1) {
                        //包含品牌词
                        this.searchMap.brand = this.resultMap.brandList[i].text
                        return true
                    }
                }
            }
            return false
        }



    },
    //钩子函数 初始化了事件和
    created: function () {

        var urlParam = this.getUrlParam();
        if (urlParam != null && urlParam.keywords != undefined) {
            this.searchMap.keywords = decodeURIComponent(urlParam.keywords)
        }
        this.searchList();

        this.$mount("#app")

    }

})
