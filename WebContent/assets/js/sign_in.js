$(document).ready(function(){
    URL_ROOT = "http://127.0.0.1:8080/KeyanWeb";
    URL_SIGN_IN = URL_ROOT + "/signinservlet";
    
    $("button#sign_in").click(function(){
        uemailaddress = new String($("#uemailaddress").val());
        upassword = new String($("#upassword").val());

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
                $("div.error_info_uemailaddress").html("请输入正确的邮箱！");
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

        if (ready) {
            $.post("signinservlet", {
                uemailaddress: uemailaddress,
                upassword: upassword
            }, function(result){
                if (result.indexOf("success") >= 0) { // 登录成功
                    window.location.href = "./display.html";
                    // $(window).attr("location", "//display.html");
                }
                else {
                    $("div.error_info_upassword").html("邮箱或密码错误！");
                }
            });
        }
    });
});