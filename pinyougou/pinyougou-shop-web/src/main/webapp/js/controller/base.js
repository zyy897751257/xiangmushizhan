//定义一个插件
let URLComponent = {}

URLComponent.install = function (Vue, options) {
    //定义一个全局的方法 方法名就叫：getUrlParam 
    //获取URL中的参数的名和值封装成一个JSON对象返回
    Vue.prototype.getUrlParam = function () {
        var obj = {}
        var name,value
        var str = window.location.href
        var num = str.indexOf("?")
        str = str.substring(num+1)
        var strings = str.split("&");
        for (var i = 0; i < strings.length; i++) {
            num = strings[i].indexOf("=");
            if (num > 0) {
                name = strings[i].substring(0,num)
                value = strings[i].substring(num+1)
                obj[name] = value
            }

        }

        return obj;
    }
}

Vue.use(URLComponent);