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
        
        ready = true;
        re_email = new RegExp(/^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/); 
        if (uemailaddress == "") {
            $("div.error_info_uemailaddress").html("请输入邮箱！");
            ready = false;
        }
        else {
            if (re_email.test(uemailaddress)) {
                $("div.error_info_uemailaddress").html("");
            }
            else {
                $("div.error_info_uemailaddress").html("邮箱格式不正确！");
                ready = false;
            }
        }
        if (upassword == "") {
            $("div.error_info_upassword").html("请输入密码！");
            ready = false;
        }
        else {
            $("div.error_info_upassword").html("");
        }
        if (upassword_confirmed == "") {
            $("div.error_info_upassword_confirmed").html("请再次输入密码！");
            ready = false;
        }
        else if (upassword_confirmed == upassword) {
            $("div.error_info_upassword_confirmed").html("密码不一致！请重新填写。");
            ready = false;
        }
        else {
            $("div.error_info_upassword_confirmed").html("");
        }
        if (uorganization == "") {
            $("div.error_info_uorganization").html("请输入您所在单位/学校！");
            ready = false;
        }
        else {
            $("div.error_info_uorganization").html("");
        }
        if (ucontactway == "") {
            $("div.error_info_ucontactway").html("请输入您的联系方式！");
            ready = false;
        }
        else {
            $("div.error_info_ucontactway").html("");
        }

        if (ready) {
            $.post("signupservlet", {
                uname: uname,
                uemailaddress: uemailaddress,
                upassword: upassword,
                uorganization: uorganization,
                ucontactway: ucontactway
            }, function(result){
                if (result.indexOf("success") >= 0) {
                    window.location.href = "./display.html";
                }
                else {
                    if (result.indexOf("have_signed_up") >= 0) {
                        $("div.error_info_uemailaddress").html("该邮箱已注册！");
                    }
                }
            });
        }
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