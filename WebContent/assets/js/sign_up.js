// sign_up.js
$(document).ready(function()
{
    URL_SIGN_UP = "signupservlet";
    $("span.back-view").click(function()
    {
        window.history.back();
    });
    $("button#sign_up_commit").click(function()
    {
        uname = new String($("#uname").val());
        uemailaddress = new String($("#uemailaddress").val());
        upassword = new String($("#upassword").val());
        upassword_confirmed = new String($("#upassword_confirmed").val());
        uorganization = new String($("#uorganization").val());
        ucontactway = new String($("#ucontactway").val());
        if (new String(uname) == "") 
        {
            uname = "User" + new String(new Date().getTime());
        }
        ready = true;
        re_email = new RegExp(/^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/); 
        if (uemailaddress == "") 
        {
            $("div.error_info_uemailaddress").html("请输入邮箱！");
            ready = false;
        }
        else 
        {
            if (re_email.test(uemailaddress)) 
            {
                $("div.error_info_uemailaddress").html("");
                ready = true;
            }
            else 
            {
                $("div.error_info_uemailaddress").html("邮箱格式不正确！");
                ready = false;
            }
        }
        if (upassword == "") 
        {
            $("div.error_info_upassword").html("请输入密码！");
            ready = false;
        }
        else 
        {
            $("div.error_info_upassword").html("");
            ready = true;
        }
        if (upassword_confirmed == "") 
        {
            $("div.error_info_upassword_confirmed").html("请再次输入密码！");
            ready = false;
        }
        else if (upassword_confirmed == upassword) 
        {
            $("div.error_info_upassword_confirmed").html("密码不一致！请重新填写。");
            ready = false;
        }
        else 
        {
            $("div.error_info_upassword_confirmed").html("");
            ready = true;
        }
        if (uorganization == "") 
        {
            $("div.error_info_uorganization").html("请输入您所在单位/学校！");
            ready = false;
        }
        else 
        {
            $("div.error_info_uorganization").html("");
            ready = true;
        }
        if (ucontactway == "") 
        {
            $("div.error_info_ucontactway").html("请输入您的联系方式！");
            ready = false;
        }
        else 
        {
            $("div.error_info_ucontactway").html("");
            ready = true;
        }
        if (ready) 
        {
            $.post(URL_SIGN_UP, 
            {
                uname: uname,
                uemailaddress: uemailaddress,
                upassword: upassword,
                uorganization: uorganization,
                ucontactway: ucontactway
            }, function(result)
            {
                if (result.indexOf("success") >= 0) 
                {
                    window.location.href = "./display.html";
                }
                else 
                {
                    if (result.indexOf("have_signed_up") >= 0) 
                    {
                        $("div.error_info_uemailaddress").html("该邮箱已注册！");
                    }
                }
            });
        }
    });
});  
Date.prototype.Format = function (fmt) 
{ //author: meizz   
    var o = {  
        "M+": this.getMonth() + 1, //月份   
        "d+": this.getDate(), //日   
        "H+": this.getHours(), //小时   
        "m+": this.getMinutes(), //分   
        "s+": this.getSeconds(), //秒   
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度   
        "S": this.getMilliseconds() //毫秒   
    };  
    if (/(y+)/.test(fmt)) 
    {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length)); 
    } 
    for (var k in o)  
    {
        if (new RegExp("(" + k + ")").test(fmt))
        {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));  
        }
    }
    return fmt;  
} 