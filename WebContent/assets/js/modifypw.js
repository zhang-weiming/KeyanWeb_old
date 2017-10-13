// modifypw.js
$(document).ready(function()
{
    URL_MODIFY_PASSWORD = "modifyuserinfoservlet";
    URL_SIGN_IN = "signinservlet";
    $("span.back-view").click(function()
    {
        window.location.href = "./display.html";
    });
    $("button.modify_password_commit").click(function()
    {
        uemailaddress = $("#uemailaddress").val();
        upassword = $("#upassword").val();
        upassword_confirmed = $("#upassword_confirmed").val();
        ready = true;
        if (uemailaddress == "")
        {
            $("div.error_info_uemailaddress").html("请输入邮箱！");
            ready = false;
        }
        else
        {
            $("div.error_info_uemailaddress").html("");
            ready = true;
        }
        if (upassword == "")
        {
            $("div.error_info_upassword").html("请输入新密码！");
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
        else
        {
            if (upassword_confirmed != upassword)
            {
                $("div.error_info_upassword_confirmed").html("请输入一致的密码！");
                ready = false;
            }
            else
            {
                $("div.error_info_upassword_confirmed").html("");
                ready = true;
            }
        }
        if (ready)
        {
            $.post(URL_MODIFY_PASSWORD,
            {
                postreason: "upassword",
                uemailaddress: uemailaddress,
                upassword: upassword
            }, function(result)
            {
                if (result.indexOf("success") >= 0)
                { // 修改成功
                    $("div.sign_in_state").html("修改成功！正在为您登录...");
                    setTimeout(function(){
                        ;
                    }, 1000);
                    $.post(URL_SIGN_IN,
                    {
                        uemailaddress: uemailaddress,
                        upassword: upassword
                    }, function(result)
                    {
                        if (result.indexOf("success") < 0)
                        {
                            $("div.sign_in_state").html("登录错误！");
                        }
                        else 
                        {
                            setTimeout(function(){
                                $("div.sign_in_state").html("登录成功！");
                            }, 1000);
                            setTimeout(function(){
                                window.location.href = "./display.html";
                            }, 1000);
                        }
                    });
                    $("div.sign_in_state").html("修改成功！正在为您登录...");
                    setTimeout(function(){
                        ;
                    }, 1000);
                }
                else
                {
                    if (result.indexOf("emailaddress_is_not_signed_up") >= 0) 
                    {
                        $("div.error_info_uemailaddress").html("该邮箱未注册！");
                    }
                    else
                    {
                        $("div.sign_in_state").html("抱歉，修改密码错误！");
                    }
                }
            });
        }
    });
});
