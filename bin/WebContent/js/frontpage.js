$(document).ready(function(){
	$("textarea.sents").val("宝马的油耗太高了"); // 自动填补textara
	$("button.am-btn.am-btn-default").click(function(){
		var sInput = $("textarea.sents").val();
//		alert("点击成功");

		// 与 PUServlet 交互
		$.post("puservlet", {
			sents: sInput
		}, function(result){
//			alert(result);
			
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
				
				// 图-圆环-正、负面句子个数统计
				$("div#main1").css("height", "400px");
				showpie(document.getElementById('main1'), [
						{value: parseInt(pos_neg_counts[0]),name: '负面', selected:false}, 
						{value: parseInt(pos_neg_counts[1]),name: '正面'}
					]
				);
				// 图-圆环-正、负面句子个数统计
				
				/*
				// 与 TransEServlet 交互
				var positions_and_sents = pos_strs_positions + "|" + sInput;
				$.post("transeservlet", {
					sentsTransE: positions_and_sents
				}, function(result){
					var pairs = result.split("|");
					
					if(pairs[0] == "ERROR") {
						$("div#main2").html(pairs[1]);
					}
					else {
						var vertex_arr = new Array(2 * pairs.length);
						var arc_arr = new Array(pairs.length);

						for(var i = 0; i < pairs.length; i++) {
							var temp_pairs = pairs[i].split(" ");

							vertex_arr[2 * i] = {category: 0, name: temp_pairs[0], value: 20};
							vertex_arr[2 * i + 1] = {category: 1, name: temp_pairs[1], value: 20};

							arc_arr[i] = {source: temp_pairs[0], target: temp_pairs[1] , weight: 2};
						}

						// 图-球球-TransE处理结果
						$("div#main2").css("height", "400px");
						showforce(document.getElementById('main2'), vertex_arr, arc_arr);
						// 图-球球-TransE处理结果

					}
					
				});
				// 与 TransEServlet 交互
				*/
			} // if
			else {
				alert("null");
			}
			
		});
		// 与 PUServlet 交互
	});
});
