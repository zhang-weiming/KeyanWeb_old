// gauge.js
function showgaue(place,data)
{
	require.config({
		paths: 
		{
            echarts: 'http://echarts.baidu.com/build/dist'
        }
    });
    require(
        [
            'echarts',
            'echarts/chart/gauge',
        ],
		function (ec) 
		{
            var myChart1 = ec.init(place);
			window.onresize = myChart1.resize;
            var option = {
				tooltip : 
				{
					formatter: "{a} <br/>{b} : {c}%"
				},
				toolbox: 
				{
					show : true,
					feature : 
					{
						mark : {show: true},
						restore : {show: true},
						saveAsImage : {show: true}
					}
				},
				series : [
					{
						name:'模型精度',
						type:'gauge',
						detail : {formatter:'{value}%'},
						data:data
					}
				]
			};      
            myChart1.setOption(option);
			window.addEventListener("resize",function()
			{
				myChart1.resize();
			});
        }
    );
}