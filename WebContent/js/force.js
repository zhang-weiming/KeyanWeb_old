function showforce(place,nodes,links){
    require.config({
        paths : {
            echarts: 'http://echarts.baidu.com/build/dist'
        }
    });
    require(
        [
            'echarts',
            // 按需加载所需图表，如需动态类型切换功能，别忘了同时加载相应图表
            'echarts/chart/force',
            'echarts/chart/chord'
        ],
        function (ec) {
            var myChart = ec.init(place);
            var option = {
                    title : {
                        x:'right',
                        y:'bottom'
                    },
                    tooltip : {
                        trigger : 'item',
                        formatter: '{a} : {b}'
                    },
                    toolbox : {
                        show : true,
                        feature : {
                            restore : {show: true},
                            magicType: {show: true, type: ['force', 'chord']},
                            saveAsImage : {show: true}
                        }
                    },
                    legend : {
                        x: 'left',
                        data : ['头实体','尾实体'],
                    },
                    series : [
                        {
                            type : 'force',
                            name : "实体",
                            ribbonType : false,
                            categories : [{
                                    name : '头实体'
                                },
                                {
                                    name : '尾实体'
                                }
							],
                            itemStyle: {
                                normal: {
                                    label: {
                                        show: true,
                                        textStyle: {
                                            color: '#333'
                                        }
                                    },
                                    nodeStyle : {
                                        brushType : 'both',
                                        borderColor : 'rgba(255,215,0,0.4)',
                                        borderWidth : 1
                                    },
                                    linkStyle: {
                                        type: 'curve'
                                    }
                                },
                                emphasis: {
                                    label: {
                                        show: false
                                        // textStyle: null      // 默认使用全局文本样式，详见TEXTSTYLE
                                    },
                                    nodeStyle : {
                                        r: 30
                                    },
                                    linkStyle : {}
                                }
                            },
							symbolSize : 20,
                            useWorker : false,
                            minRadius : 20,
                            maxRadius : 25,
                            gravity : 1.1,
                            scaling : 1.1,
                            roam : 'move',
                            nodes : nodes,
                            links : links,
                        }
                    ]
                };
            myChart.setOption(option);
            var ecConfig = require('echarts/config');
            function focus(param) {
                var data = param.data;
                var links = option.series[0].links;
                var nodes = option.series[0].nodes;
                if (
                    data.source !== undefined
                    && data.target !== undefined
                ) { //点击的是边
                    var sourceNode = nodes.filter(function (n) {return n.name == data.source})[0];
                    var targetNode = nodes.filter(function (n) {return n.name == data.target})[0];
                    console.log("选中了边 " + sourceNode.name + ' -> ' + targetNode.name + ' (' + data.weight + ')');
                } else { // 点击的是点
                    console.log("选中了" + data.name + '(' + data.value + ')');
                }
            }
            myChart.on(ecConfig.EVENT.CLICK, focus)

            myChart.on(ecConfig.EVENT.FORCE_LAYOUT_END, function () {
                console.log(myChart.chart.force.getPosition());
            });
        }
    );
}