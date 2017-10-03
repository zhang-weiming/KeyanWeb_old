$(document).ready(function(){
	// 自动填补textara
	$("textarea.sents").val("宝马的油耗太高了。红色的很嗲，白色敞篷的也喜欢的~。奇瑞汽车下线了这是结果，而不是奇瑞委屈的到处说自己一腔热血工业报国无门。奥迪的吉普中看不中用~苦逼的赶路孩子只能窝在最后座的小小空间里~。新览胜的灯更是无力吐槽。分享图片三选一，我最喜欢这台红色的雪佛兰，外观大气，空间大，中控台超有fell，我不喜欢丰田卡罗拉，老爸你要相信我的眼光，雪佛兰科鲁慈保值率高啊。比亚迪回复玻璃本性，没有默德萨克是最大败笔。个人比较讨厌开别克的，因为经常发现别克大白天的开着车灯甚至远光，每次被晃发现又是个别克，心中一万只羊驼奔腾，最近在上海各种原因各种机会开了各种别克，才发现原来几乎都是特么自动开灯的，别克的设计师大概都是偏远山区出来的吧，默认不是自动会死啊。速腾内饰也不好看。。比亚迪核心业务前景遭质疑-华尔街日报姐早就说了，千万不要买他们家的车子.。byd的策略根本就是有问题，为了圈钱，不是为了造车……");
	// 自动填补textara

	$("button#button-file-upload").click(function(){
		var formData = new FormData($("#uploadForm")[0]);
		$.ajax({
			url : 'http://182.254.247.94:8080/KeyanWeb/fileuploadservlet',
			type : 'POST', 
			data : formData,
			async : false,
			cache: false,
			processData : false, // 告诉jQuery不要去处理发送的数据
			contentType : false,// 告诉jQuery不要去设置Content-Type请求头
			beforeSend: function() {
				console.log("正在进行，请稍候");
			},
			success : function(responseStr) { 
				// if(responseStr.status === 0) {
				// 	console.log("成功" + responseStr);
				// } else {
				// 	console.log("失败");
				// }
				console.log(responseStr);
			}, 
			error : function(responseStr) { 
				console.log("error: " + responseStr);
			} 
		});
	});

	$("button.am-btn.am-btn-default").click(function(){
		var sInput = $("textarea.sents").val();
//		sInput_parts = sInput.split(/[。！？]/);
//		str = "";
//		for(i = 0; i < sInput_parts.length; i++) {
//			str += "[" + i + "]" + sInput_parts[i] + "<br />";
//		}
//		$("div.div-test").html("<hr />" + str);
//		alert("alert:\n" + str);

		
		// 与 PUServlet 交互
		$.post("http://182.254.247.94:8080/KeyanWeb/puservlet", {
			sents: sInput
		}, function(result){
//			alert("changed");
			
			if(result != "null") {
				var parts = result.split("|");
				var pos_neg_counts = parts[0].split(" "); // pos neg 分别统计出来的个数
				var pos_strs_positions = parts[1]; // 输入文本中，所有 pos 类句子的下标

//				 // 图-仪表盘-准确率
//				 showgaue(document.getElementById('main1'),[
//				 		{value: 50, name: '准确率'}
//				 	]
//				 );
//				 // 图-仪表盘-准确率
				
				// 分类颜色标注
				pos_color='#F4A460';
				neg_color='#7FFFD4';
				/*
				$("button#button-color-selector-pos").click(function(){
					pos_color = $("input#input-color-selector-pos").val();
					for(i = 0; i < positions.length; i++) {
						// $("td.td-column-content").eq(parseInt(positions[i])).css("background-color", "#5ff");
						$("td#td-column-content-" + positions[i]).css("background-color", pos_color);
					}
				});
				$("button#button-color-selector-neg").click(function(){
					neg_color = $("input#input-color-selector-neg").val();
					$("tr").remove(".tr-display");
					positions = $.trim(pos_strs_positions).split(" ");
					sInput_parts = sInput.split(/[。！？]/);
					a = 1;
					for(i = 0; i < sInput_parts.length; i++) {
						if($.trim(sInput_parts[i]) != "") {
							temp_str = "<tr class=\"tr-display\">" + 
										"<td class=\"td-column-label\">" + (a) + "</td>" + 
										"<td " + "id=\"td-column-content-" + i + "\" class=\"td-column-content\">" + sInput_parts[i] + "</td>" + 
									"</tr>";
							$(".table-display").append(temp_str);
							$("td#td-column-content-" + i).css("background-color", neg_color);
							a++;
						}
					}
					for(i = 0; i < positions.length; i++) {
						$("td#td-column-content-" + positions[i]).css("background-color", pos_color);
					}
				});
				*/
				$("div.label-img").removeClass("before-img");
				$("tr").remove(".tr-display");
				positions = $.trim(pos_strs_positions).split(" ");
				sInput_parts = sInput.split(/[。！？]/);
				a = 1;
				for(i = 0; i < sInput_parts.length; i++) {
					if($.trim(sInput_parts[i]) != "") {
						temp_str = "<tr class=\"tr-display\">" + 
									"<td class=\"td-column-label\">" + (a) + "</td>" + 
									"<td " + "id=\"td-column-content-" + i + "\" class=\"td-column-content\">" + sInput_parts[i] + "</td>" + 
								"</tr>";
						$(".table-display").append(temp_str);
						$("td#td-column-content-" + i).css("background-color", "#BFEFFF");
						a++;
					}
				}
				// $("td.td-column-content").each(function(){

				// });
				// $("dt-column-label").width($("dt-column-label").height());
				for(i = 0; i < positions.length; i++) {
					// $("td.td-column-content").eq(parseInt(positions[i])).css("background-color", "#5ff");
					$("td#td-column-content-" + positions[i]).css("background-color", "#FFDEAD");
				}

				// $("#display-classification").html(parseInt(positions[0]) + parseInt(positions[1]));
				// 分类颜色标注
				
				// 图-圆环-正、负面句子个数统计
				$("div.pu-img").removeClass("before-img");
				$("div#main1").css("height", "400px");
//				$("div#main1").css("height", "100%");
				showpie(document.getElementById('main1'), [
						{value: parseInt(pos_neg_counts[0]),name: '负面', selected:false}, 
						{value: parseInt(pos_neg_counts[1]),name: '正面'}
					]
				);
				// 图-圆环-正、负面句子个数统计
				
				// // TempServlet 展示pos预测结果
				// $.post("tempservlet", {
				// 	tempData: pos_strs_positions + "|" + sInput
				// }, function(result){
				// 	alert(result);
				// });
				// // TempServlet 展示pos预测结果
				
				
				// 与 TransEServlet 交互
				$.post("http://localhost:8080/KeyanWeb/transeservlet", {
					positions: pos_strs_positions,
					sents: sInput
				}, function(result){
					var pairs = result.split("|");
					
					if(pairs[0] == "ERROR") {
						$("div#main2").html(pairs[1]);
					}
					else {
						var vertex_arr = new Array(2 * pairs.length);
						var arc_arr = new Array(pairs.length);

						temp_vertex_str = "";
						temp_arc_str = "";
						for(var i = 0; i < pairs.length; i++) {
							var temp_pairs = pairs[i].split(" ");
							vertex_arr[2 * i] = {category: 0, name: temp_pairs[0], value: 20};
							vertex_arr[2 * i + 1] = {category: 1, name: temp_pairs[1], value: 20};
							arc_arr[i] = {source: temp_pairs[0], target: temp_pairs[1] , weight: 1};
							
//							temp_elem = "{category: 0, name: \'" + temp_pairs[0] + "\', value: 20}, ";
//							if(temp_vertex_str.indexOf(temp_elem) < 0) temp_vertex_str += temp_elem;
//							temp_elem = "{category: 1, name: \'" + temp_pairs[1] + "\', value: 20}, ";
//							if(temp_vertex_str.indexOf(temp_elem) < 0) temp_vertex_str += temp_elem;
//							temp_elem = "{source: \'" + temp_pairs[0] + "\', target: \'" + temp_pairs[1] + "\', weight: 2}, ";
//							if(temp_arc_str.indexOf(temp_elem) < 0) temp_arc_str += temp_elem;
						}
						arc_arr[0].weight = 5;

						// 图-球球-TransE处理结果
						$("div.transE-img").removeClass("before-img");
						$("div#main2").css("height", "400px");
//						$("div#main2").css("height", "100%");
						showforce(document.getElementById('main2'), vertex_arr, arc_arr);
						// 图-球球-TransE处理结果
//						$("div.div-test").html(temp_vertex_str + "<br /><br />" + temp_arc_str);
					}
					
				});
				// 与 TransEServlet 交互
				
			} // if
			else {
				alert("null");
			}
			
		});
		// 与 PUServlet 交互
		
	});


	
	// 发送登录请求
    $("button#sign_in_button").click(function(){
        url = "http://localhost:8080/KeyanWeb/signinservlet";
        $.post(url, {
            uemailaddress: $("input#sign_in_uemailaddress").val(),
			upassword: $("input#sign_in_upassword").val()
        }, function(result){
			// alert('OK');
			$("input#sign_in_result").val(result);
        });
    });
	// 发送登录请求

	// 发送注册请求
    $("button#register-submit").click(function(){
        url = "http://localhost:8080/KeyanWeb/signupservlet";
        $.post(url, {
			uname: $("input#uname").val(),
            uemailaddress: $("input#uemailaddress").val(),
			upassword: $("input#sign_up_upassword").val(),
            uorganization: $("input#uorganization").val(),
            ucontactway: $("input#ucontactway").val()
        }, function(result){
			$("input#sign_up_result").val(result);
        });
    });
	// 发送注册请求



});
