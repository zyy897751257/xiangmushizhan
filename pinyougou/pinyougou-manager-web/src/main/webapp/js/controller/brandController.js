var app = new Vue({
    el: "#app",
    data: {
        pages:15,
        pageNo:1,
        list:[],
        entity:{},
        ids:[],
        searchEntity:{},
        dialogFormVisible: false,
        formLabelWidth: '120px',
        multipleSelection: []
    },
    methods: {
        handleSelectionChange(val) {
            this.multipleSelection = val;
            this.ids = []
            if (this.multipleSelection != null) {
                for (var i = 0;i<this.multipleSelection.length;i++) {
                    this.ids.push(this.multipleSelection[i].id)
                }
            }
            console.log(this.ids)
        },
        //上传之前进行文件格式校验
        beforeUpload(file){
            const isXLS = file.type === 'application/vnd.ms-excel';
            if(isXLS){
                return true;
            }
            const isXLSX = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
            if (isXLSX) {
                return true;
            }
            this.$message.error('上传文件只能是xls或者xlsx格式!');
            return false;
        },
        //下载模板文件（路径）
        downloadTemplate(){
            window.location.href="../template/template.xlsx";
        },
        //上传成功提示
        handleSuccess(response, file) {
            if(response.success){
                this.$message({
                    message: response.message,
                    type: 'success'
                });
                window.location.reload();
            }else{
                this.$message.error(response.message);
            }
            console.log(response, file);
        },
        searchList:function (curPage) {
            axios.post('/brand/search.shtml?pageNo='+curPage,this.searchEntity).then(function (response) {
                //获取数据
                app.list=response.data.list;

                //当前页
                app.pageNo=curPage;
                //总页数
                app.pages=response.data.pages;
            });
        },
        //查询所有品牌列表
        findAll:function () {
            console.log(app);
            axios.get('/brand/findAll.shtml').then(function (response) {
                console.log(response);
                //注意：this 在axios中就不再是 vue实例了。
                app.list=response.data;

            }).catch(function (error) {

            })
        },
         findPage:function () {
            var that = this;
            axios.get('/brand/findPage.shtml',{params:{
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
            axios.post('/brand/add.shtml',this.entity).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(app.pageNo)
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        update:function () {
            axios.post('/brand/update.shtml',this.entity).then(function (response) {
                /*console.log(response);*/
                if(response.data.success){
                    window.location.reload()
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
            axios.get('/brand/findOne/'+id+'.shtml').then(function (response) {
                app.entity=response.data;
                console.log(app.entity)
            }).catch(function (error) {
                console.log("1231312131321");
            });
        },
        dele:function () {
            axios.post('/brand/delete.shtml',this.ids).then(function (response) {
                console.log(response);
                if(response.data.success){
                    app.searchList(app.pageNo);
                }
            }).catch(function (error) {
                console.log("1231312131321");
            });
        }



    },
    //钩子函数 初始化了事件和
    created: function () {
      
        this.searchList(1);

    }

})
