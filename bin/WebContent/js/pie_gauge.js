function testFunc() {
	alert("OK");
}


function showgaue(place,data){
	require.config({
        paths: {
            echarts: 'http://echarts.baidu.com/build/dist'
        }
    });
    require(
        [
            'echarts',
            // 按需加载所需图表，如需动态类型切换功能，别忘了同时加载相应图表
            'echarts/chart/gauge',
        ],
        function (ec) {
            var myChart = ec.init(place);
            var option = {
				tooltip : {
					formatter: "{a} <br/>{b} : {c}%"
				},
				toolbox: {
					show : true,
					feature : {
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
            myChart.setOption(option);
        }
    );
}
function showpie(place, data) {
//   data:[
//        {value:335, name:'直接访问'},
//        {value:310, name:'邮件营销'}
//   ]
    require.config({
        paths: {
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
        function (ec) {
            var myChart = ec.init(place);
            var option = {
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    x: 'left',
                    data: ['负面','正面']
                },
                toolbox: {
                    show: true,
                    feature: {
                        mark: {show: true},
                        dataView: {show: true, readOnly: false},
                        magicType: {
                            show: true,
                            type: ['pie', 'funnel'],
                            option: {
                                funnel: {
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
                        itemStyle: {
                            normal: {
                                label: {
                                    show: false
                                },
                                labelLine: {
                                    show: false
                                }
                            },
                            emphasis: {
                                label: {
                                    show: true,
                                    position: 'center',
                                    textStyle: {
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

            myChart.setOption(option);
        }
    );
}