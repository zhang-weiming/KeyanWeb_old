// pie.js
function showpie(place, data, myId) 
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
            // 按需加载所需图表，如需动态类型切换功能，别忘了同时加载相应图表
            'echarts/chart/pie',
            'echarts/chart/funnel'
        ],
        function (ec) 
        {
            var myChart2 = ec.init(place);
			window.onresize = myChart2.resize;
            var option = {
                tooltip: 
                {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: 
                {
                    orient: 'vertical',
                    x: 'left',
                    data: ['负面','正面']
                },
                toolbox: 
                {
                    show: true,
                    feature: 
                    {
                        mark: {show: true},
                        dataView: {show: true, readOnly: false},
                        magicType: 
                        {
                            show: true,
                            type: ['pie', 'funnel'],
                            option: 
                            {
                                funnel: 
                                {
                                    x: '25%',
                                    width: '50%',
                                    funnelAlign: 'center',
                                    max: 1548
                                }
                            }
                        },
                        restore: {show: true},
                        saveAsImage: {show: true}
                    }
                },
                calculable: true,
                series: [
                    {
                        name: '句子数量',
                        type: 'pie',
                        radius: ['50%', '70%'],
                        itemStyle: 
                        {
                            normal: 
                            {
                                label: 
                                {
                                    show: false
                                },
                                labelLine: 
                                {
                                    show: false
                                }
                            },
                            emphasis: 
                            {
                                label: 
                                {
                                    show: true,
                                    position: 'center',
                                    textStyle: 
                                    {
                                        fontSize: '30',
                                        fontWeight: 'bold'
                                    }
                                }
                            }
                        },
                        data: data
                    }
                ]
            };
            myChart2.setOption(option);
            window.addEventListener("resize",function()
            {
				myChart2.resize();
            });
            
            var last_time = 0;
            var current_time = new Date().getSeconds;
            myChart2.on('click', function(params){
                // window.location.href = encodeURIComponent(params.name) + '.html';
                current_time = new Date().getSeconds;
                if ((current_time - last_time) > 2) {
                    last_time = current_time;
                }
                else {
                    last_time = current_time;
                    window.open(encodeURIComponent(params.name) + '.html?myId=' + myId);
                }
            });
        }
    );
}
