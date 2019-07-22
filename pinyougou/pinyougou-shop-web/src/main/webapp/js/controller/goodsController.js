var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{
            tbGoods:{

            },
            tbGoodsDesc:{
                itemImages:[],
                customAttributeItems:[],
                specificationItems:[]
            },
            tbItemList:[]
        },
        ids:[],
        searchEntity:{},
        image_entity:{
            color:"",
            url:""
        },
        itemCat1List:[],
        itemCat2List:[],
        itemCat3List:[],
        brandList:[],
        specList:[]

    },
    methods: {
        searchList:function (curPage) {
            axios.post('/goods/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;
                 console.log(app.list)
                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/goods/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/goods/findPage.shtml',{params:{
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
            this.entity.tbGoodsDesc.introduction=editor.html()
            axios.post('/goods/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.entity={tbGoods:{},tbGoodsDesc:{},tbItemList:[]}
                    editor.html("")
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            this.entity.tbGoodsDesc.introduction=editor.html()
            axios.post('/goods/update.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        save:function () {
            if(this.entity.tbGoods.id!=null){
                this.update();
            }else{
                this.add();
            }
        },
        findOne:function (id) {
            axios.get('/goods/findOne/'+id+'.shtml').then(function (response) {
                console.log(response.data)
                app.entity=response.data;
                editor.html(app.entity.tbGoodsDesc.introduction)
                app.entity.tbGoodsDesc.itemImages = JSON.parse(app.entity.tbGoodsDesc.itemImages)
                app.entity.tbGoodsDesc.customAttributeItems = JSON.parse(app.entity.tbGoodsDesc.customAttributeItems)
                app.entity.tbGoodsDesc.specificationItems = JSON.parse(app.entity.tbGoodsDesc.specificationItems)

                for (var i = 0;i<app.entity.tbItemList.length;i++) {
                    app.entity.tbItemList[i].spec = JSON.parse(app.entity.tbItemList[i].spec)
                }

                console.log(app.entity.tbGoodsDesc.customAttributeItems)
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/goods/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(1);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        upload:function () {
            var formData = new FormData();
            formData.append("file",file.files[0])
            axios({
                url:"http://localhost:9110/upload/uploadFile.shtml",
                data: formData,
                method:"post",
                header:{
                    "Content-Type": "multipart/form-data"
                },
                withCredentials:true
            }).then(function(response) {
                console.log(response.data)
                if (response.data.success) {
                    app.image_entity.url=response.data.message
                }
            }).catch(function (error) {
                console.log(error)

            })
        },
        addImageEntity:function () {
            //添加图片
            this.entity.tbGoodsDesc.itemImages.push(this.image_entity);
            this.image_entity = {color:"", url:""}
        },
        removeImage:function (index) {
            this.entity.tbGoodsDesc.itemImages.splice(index,1)
        },
        findCategory1List:function () {
            axios.get("/itemCat/findByParentId/0.shtml").then(function (response) {
                app.itemCat1List = response.data
            })
        },
        updateChecked:function ($event, specName, specValue) {//specName:attributeName的值，被改变的attributeValue的值
            var searchObject = app.searchObjectByKey(app.entity.tbGoodsDesc.specificationItems,specName,"attributeName");
            if (searchObject != null) {

                if ($event.target.checked) {
                    searchObject.attributeValue.push(specValue)
                    
                } else {
                    searchObject.attributeValue.splice(searchObject.attributeValue.indexOf(specValue),1)
                    if (searchObject.attributeValue.length == 0) {
                        this.entity.tbGoodsDesc.specificationItems.splice(this.entity.tbGoodsDesc.specificationItems.indexOf(searchObject),1)
                    }
                }
            } else {
                app.entity.tbGoodsDesc.specificationItems.push({
                        "attributeName":specName,
                        "attributeValue":[specValue]

                    }
                )

            }
            console.log(app.entity.tbGoodsDesc.specificationItems)
        },
        isChecked:function (specName, specValue) {
            var obj = app.searchObjectByKey(app.entity.tbGoodsDesc.specificationItems,specName,"attributeName");

            if (obj != null) {
                if (obj.attributeValue.indexOf(specValue) != -1) {
                    return true;
                }
            }

            return false;
        }
        
        ,searchObjectByKey:function (list, specName, key) {
            for (var i = 0;i < list.length;i++) {
                var element = list[i];
                if (element[key] == specName) {
                    return element;
                }
            }
            return null;
        },
        /*//点击复选框的时候 调用生成 sku列表的的变量
        createList:function () {
            //1.定义初始化的值
            this.entity.tbItemList=[{'spec':{},'price':0,'num':0,'status':'0','isDefault':'0'}];

            //2.循环遍历 specificationItems

            var specificationItems=this.entity.tbGoodsDesc.specificationItems;
            for(var i=0;i<specificationItems.length;i++){
                //3.获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表
                var obj = specificationItems[i];
                this.entity.tbItemList=this.addColumn(
                    this.entity.tbItemList,
                    obj.attributeName,
                    obj.attributeValue);
            }
        },

        /!**
         *获取 规格的名称 和规格选项的值 拼接 返回一个最新的SKU的列表 方法
         * @param list
         * @param columnName  网络
         * @param columnValue  [移动3G,移动4G]
         *!/
        addColumn: function (list, columnName, columnValue) {
            var newList=[];

            for (var i = 0; i < list.length; i++) {
                var oldRow = list[i];//
                for (var j = 0; j < columnValue.length; j++) {
                    var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                    var value = columnValue[j];//移动3G
                    newRow.spec[columnName] = value;
                    newList.push(newRow);
                }
            }

            return newList;
        }*/
        /*createList:function () {
            var specificationItems = app.entity.tbGoodsDesc.specificationItems
            app.entity.tbItemList = [{'spec':{}, 'price':9999, 'num':0, 'status':'0', 'isDefault':'0'}]

            for (var i = 0;i < specificationItems.length;i++) {
                var obj = specificationItems[i]   //obj:{ "attributeName": "网络", "attributeValue": [ "移动3G", "联通3G", "电信3G" ] }
                var columnValue = obj.attributeValue
                var columnName = obj.attributeName
                var newList = []
                var itemList = app.entity.tbItemList
                for (var j = 0;j < itemList.length;j++) {
                    var oldRow = itemList[j]
                    for (var k = 0;k < columnValue.length;k++) {
                        var newRow = JSON.parse(JSON.stringify(oldRow))
                        newRow.spec[columnName] = columnValue[k]
                        newList.push(newRow)
                    }
                }

                app.entity.tbItemList = newList
            }
        },*/
        createList:function (event, specName, specValue) {
            //判断tbItemList是否为空
            var itemList = JSON.parse(JSON.stringify(app.entity.tbItemList))
            if (itemList.length > 0 ) {
                //判断触发事件是勾选还是取消勾选
                if (event.target.checked) {
                    //勾选
                    //操作tbItemList---添加
                    for (var i = 0; i < itemList.length; i++) {
                        var spec = itemList[i].spec
                        if (spec[specName] == null) {
                            //当前勾选属性第一次被勾选
                            app.entity.tbItemList[i].spec[specName] = specValue
                        } else {
                            //当前勾选属性不是第一次被勾选，则需要复制并改变复制过来元素的值
                            //var oldRow = itemList[i]
                            var newRow = itemList[i]//JSON.parse(JSON.stringify(oldRow))
                            newRow.spec[specName] = specValue
                            newRow.price = 0
                            newRow.num = 0
                            newRow.status = 0
                            newRow.isDefault = 0
                            app.entity.tbItemList.push(newRow)
                        }
                    }
                    console.log(app.entity.tbItemList)
                } else {
                    //取消勾选

                }

            } else {
                //如果为空。就肯定是勾选事件
                let newRow = {'spec':{},'price':0,'num':0,'status':'0','isDefault':'0'}
                newRow.spec[specName] = specValue
                app.entity.tbItemList.push(newRow)
                console.log(app.entity.tbItemList)
            }

            console.log(app.entity.tbItemList)

        }


    },
    watch:{
        'entity.tbGoods.category1Id':function (newValue,oldValue) {
            if (newValue != undefined) {
                axios.get("/itemCat/findByParentId/"+newValue+".shtml").then(function (response) {
                    app.itemCat2List = response.data
                    app.itemCat3List = undefined
                    app.$set(app.entity.tbGoods,"typeTemplateId",undefined)
                })
            }
        },
        'entity.tbGoods.category2Id':function (newValue,oldValue) {
            if (newValue != undefined) {
                axios.get("/itemCat/findByParentId/"+newValue+".shtml").then(function (response) {
                    app.itemCat3List = response.data
                })
            }
        },
        'entity.tbGoods.category3Id':function (newValue,oldValue) {

            if (newValue != undefined) {
                axios.get("/itemCat/findOne/"+newValue+".shtml").then(function (response) {
                    app.$set(app.entity.tbGoods,"typeTemplateId",response.data.typeId)
                })
            }
        },
        'entity.tbGoods.typeTemplateId':function (newValue, oldValue) {
            if (newValue != undefined) {

                axios.get("/typeTemplate/findOneAndSpecIds/"+newValue+".shtml").then(function (response) {
                    let data = response.data;
                    app.brandList = JSON.parse(data.brandIds);
                    if (app.entity.tbGoods.id == null) {
                        app.entity.tbGoodsDesc.customAttributeItems = JSON.parse(data.customAttributeItems)
                    }

                    app.specList = JSON.parse(data.specIds);
                    console.log(app.specList)
                })
            }
        },
        'entity.tbGoods.isEnableSpec':function (newValue, oldValue) {
            if (newValue == 0) {
                app.entity.tbGoodsDesc.specificationItems = []
                app.entity.tbItemList = []
            }
        },
        'entity.tbItemList':function (newValue,oldValue) {
            if (JSON.stringify(newValue) == JSON.stringify([{'spec':{}, 'price':0, 'num':0, 'status':'0', 'isDefault':'0'}])) {
                app.entity.tbItemList = []
            }
        }


    },
    //钩子函数 初始化了事件和
    created: function () {


        this.findCategory1List();

        var request = this.getUrlParam();

        console.log(request)

        this.findOne(request.id)

    }

})
