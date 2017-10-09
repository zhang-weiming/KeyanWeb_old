$(document).ready(function(){
    URL_ROOT = "http://127.0.0.1:8080/KeyanWeb";
    URL_SIGN_IN = URL_ROOT + "/signinservlet";
    
    $("button#sign_in").click(function(){
        uemailaddress = new String($("#uemailaddress").val());
        upassword = new String($("#upassword").val());

        $.post("signinservlet", {
            uemailaddress: uemailaddress,
            upassword: upassword
        }, function(result){
            if (result.indexOf("success") >= 0) { // 登录成功
                window.location.href = "./display.html";
                // $(window).attr("location", "//display.html");
            }
            else {
                alert(result);
            }
        });
    });
});