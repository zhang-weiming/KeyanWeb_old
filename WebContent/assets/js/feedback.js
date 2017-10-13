// feedback.js
$(document).ready(function()
{
	URL_CONFIRM_SESSION = "confirmsession";
    URL_FEEDBACK = "feedbackservlet";
    $("span.back-view").click(function()
    {
        window.history.back();
    });
    $("button.feedback-commit").click(function()
    {
        uemailaddress = "";
        $.post(URL_CONFIRM_SESSION, function(result)
        {
            resultArr = result.split("|");
            uemailaddress = $.trim(resultArr[1]);
            if (resultArr[0].indexOf("success") < 0) 
            { // 该用户未登录
                $("div.error_info_feedback").html("请您先登录！");
                return;
            }
            else
            {
                feedback_text = $("textarea#feedback").val();
                ready = true;
                if (feedback_text == "")
                {
                    $("div.error_info_feedback").html("请填写您的建议！");
                    ready = false;
                }
                else
                {
                    $("div.error_info_feedback").html("");
                    ready = true;
                }
                if (ready)
                {
                    $.post(URL_FEEDBACK,
                    {
                        uemailaddress: uemailaddress,
                        feedinfo: feedback_text
                    }, function(result)
                    {
                        if (result.indexOf("success") >= 0)
                        { // 反馈提交成功
                            $("div.error_info_feedback").html("提交成功！");
                            setTimeout(function()
                            {
                                window.location.href = "./display.html";
                            }, 1000);
                        }
                        else
                        {
                            if (result.indexOf("emailaddress_is_not_signed_up") >= 0)
                            {
                                $("div.error_info_feedback").html("您的登录邮箱有误！");
                            }
                            else
                            {
                                $("div.error_info_feedback").html("抱歉，提交失败！");
                            }
                        }
                    });
                }
            }
        });
    });
    $("button.forget_password").click(function(){
        window.location.href = "./modifypw.html";
    });
});
