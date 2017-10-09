$(document).ready(function(){
    URL_ROOT = "http://127.0.0.1:8080/KeyanWeb";
    URL_SIGN_UP = URL_ROOT + "/signupservlet";

    $("button#sign_up_commit").click(function(){
        uname = new String($("#uname").val());
        uemailaddress = new String($("#uemailaddress").val());
        upassword = new String($("#upassword").val());
        upassword_confirmed = new String($("#upassword_confirmed").val());
        uorganization = new String($("#uorganization").val());
        ucontactway = new String($("#ucontactway").val());

        if (new String(uname) == "") {
            uname = "User" + new String(new Date().getTime());
        }
        // alert(uname);

        $.post("signupservlet", {
            uname: uname,
            uemailaddress: uemailaddress,
            upassword: upassword,
            uorganization: uorganization,
            ucontactway: ucontactway
        }, function(result){
            alert(result);
        });
    });
});


// 对Date的扩展，将 Date 转化为指定格式的String  
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
Date.prototype.Format = function (fmt) { //author: meizz   
    var o = {  
        "M+": this.getMonth() + 1, //月份   
        "d+": this.getDate(), //日   
        "H+": this.getHours(), //小时   
        "m+": this.getMinutes(), //分   
        "s+": this.getSeconds(), //秒   
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度   
        "S": this.getMilliseconds() //毫秒   
    };  
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));  
    for (var k in o)  
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));  
    return fmt;  
} 