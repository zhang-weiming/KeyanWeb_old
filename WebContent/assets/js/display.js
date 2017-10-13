// display.js
$(document).ready(function()
{
	URL_CONFIRM_SESSION = "confirmsession";
	$("textarea.sents").val("宝马的油耗太高了。红色的很嗲，白色敞篷的也喜欢的~。奇瑞汽车下线了这是结果，而不是奇瑞委屈的到处说自己一腔热血工业报国无门。奥迪的吉普中看不中用~苦逼的赶路孩子只能窝在最后座的小小空间里~。新览胜的灯更是无力吐槽。分享图片三选一，我最喜欢这台红色的雪佛兰，外观大气，空间大，中控台超有fell，我不喜欢丰田卡罗拉，老爸你要相信我的眼光，雪佛兰科鲁慈保值率高啊。比亚迪回复玻璃本性，没有默德萨克是最大败笔。个人比较讨厌开别克的，因为经常发现别克大白天的开着车灯甚至远光，每次被晃发现又是个别克，心中一万只羊驼奔腾，最近在上海各种原因各种机会开了各种别克，才发现原来几乎都是特么自动开灯的，别克的设计师大概都是偏远山区出来的吧，默认不是自动会死啊。速腾内饰也不好看。。比亚迪核心业务前景遭质疑-华尔街日报姐早就说了，千万不要买他们家的车子.。byd的策略根本就是有问题，为了圈钱，不是为了造车……");
	$("span#sign_out").hide();
	$.post(URL_CONFIRM_SESSION, function(result)
	{
		resultArr = result.split("|");
		uemailaddress = resultArr[1];
		if (resultArr[0].indexOf("success") >= 0) 
		{ // 该用户已登录
			$("span#sign_in_or_up").html(
				"<a class=\"user_center_link\">" + 
					uemailaddress + 
				"</a>" + "，欢迎回来！ "
			);
			$("span#sign_out").show();
			$("#sign_out").click(function()
			{
				r = confirm("确定要退出吗？");
				if (r) 
				{
					$.post("signout", function(result)
						{
							if (result.indexOf("success") >= 0) 
							{
								location.reload();
							}
						}
					);
				}
			});
		}
	});
	$("#input_text_commit").click(function()
	{
		$("p.error_info").html("提交成功！正在处理，请稍等。");
		var sInput = $("textarea.sents").val();
		$.post("puservlet", {
			sents: sInput
		}, function(result)
		{
			if(result != "null") 
			{
				$("p.error_info").hide();
				var parts = result.split("|");
				var pos_neg_counts = parts[0].split(" "); // pos neg 分别统计出来的个数
				var pos_strs_positions = parts[1]; // 输入文本中，所有 pos 类句子的下标
				pos_color='#F4A460';
				neg_color='#7FFFD4';
				$("#DC-legend").css("display", "block");
				$("div.label-img").removeClass("before-img");
				$("tr").remove(".tr-display");
				positions = $.trim(pos_strs_positions).split(" ");
				sInput_parts = sInput.split(/[。！？]/);
				a = 1;
				for(i = 0; i < sInput_parts.length; i++) 
				{
					if($.trim(sInput_parts[i]) != "") 
					{
						temp_str = "<tr class=\"tr-display\">" + 
										"<td class=\"td-column-label\">" + 
											(a) + 
										"</td>" + 
										"<td " + "id=\"td-column-content-" + i + "\" class=\"td-column-content\">" + 
											sInput_parts[i] + 
										"</td>" + 
									"</tr>";
						$(".table-display").append(temp_str);
						$("td#td-column-content-" + i).css("background-color", "#BFEFFF");
						a++;
					}
				}
				for(i = 0; i < positions.length; i++) 
				{
					$("td#td-column-content-" + positions[i]).css("background-color", "#FFDEAD");
				}
				$("div.pu-img").removeClass("before-img");
				$("div#main1").css("height", "400px");
				showpie(document.getElementById('main1'), [
						{value: parseInt(pos_neg_counts[0]),name: '负面', selected:false}, 
						{value: parseInt(pos_neg_counts[1]),name: '正面'}
					]
				);
				$.post("transeservlet", 
				{
					positions: pos_strs_positions,
					sents: sInput
				}, function(result)
				{
					var pairs = result.split("|");
					if(pairs[0] == "ERROR") 
					{
						$("div#main2").html(pairs[1]);
					}
					else 
					{
						var vertex_arr = new Array(2 * pairs.length);
						var arc_arr = new Array(pairs.length);
						temp_vertex_str = "";
						temp_arc_str = "";
						for(var i = 0; i < pairs.length; i++) 
						{
							var temp_pairs = pairs[i].split(" ");
							vertex_arr[2 * i] = {category: 0, name: temp_pairs[0], value: 20};
							vertex_arr[2 * i + 1] = {category: 1, name: temp_pairs[1], value: 20};
							arc_arr[i] = {source: temp_pairs[0], target: temp_pairs[1] , weight: 1};
						}
						arc_arr[0].weight = 5;
						$("div.transE-img").removeClass("before-img");
						$("div#main2").css("height", "400px");
						showforce(document.getElementById('main2'), vertex_arr, arc_arr);
					}
				});
			} // if
			else {
				$("p.error_info").html("抱歉，没有分析结果！");
			}
		});
	});
});
