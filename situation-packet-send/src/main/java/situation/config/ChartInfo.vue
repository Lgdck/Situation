<template>
    <div class="container">
        <div class="top">
            <div id="chart1"></div>
            <div id="chart2"></div>
            <div id="chart3"></div>
        </div>
        <div class="bottom">
            <div class="bottom-left">
                <div id="chart4"></div>
                <div id="chart5"></div>
            </div>
            <div class="bottom-right">
                <div id="chart6" ref="c6"></div>
            </div>
        </div>
    </div>
</template>
<script>
    /*import instance from "../network";*/
    import * as $echarts from 'echarts';
    import instance from "../network";
    export default {
        data(){
            return{
                chart6:'',
                currentid : -1,
                xtime:[],
                ydelay:[],
            }
        },
        mounted(){
            clearInterval(this.timer);

            // this.drawchart6()
            this.initData()
            this.drawchart1()
            this.drawchart2()
            this.drawchart3()
            this.drawchart4()
            this.drawchart5()



        },
        methods: {
              async initData() {
                let timex = 0;
                contst timer = setInterval(() => { //需要定时执行的代码
                     instance.post("/send/delay/"+this.currentid).then((res) => {

                        if (res.data.hasOwnProperty("nextId")){
                            this.currentid = res.data.nextId
                            if(this.ydelay.length > 60){
                                this.ydelay.shift();
                                this.xtime.shift();
                            }
                            this.ydelay.push(res.data.AverageDelay-22000)
                            this.xtime.push(timex)
                            timex = timex+1;
                            /* console.log(res.data)*/
                            console.log(this.ydelay)
                            console.log(this.xtime)
                        }
                    })
                    // this.chart6 = document.getElementById("chart6").removeAttribute('_echarts_instance_');
                    // const chartDom = document.getElementById('chart6');
                    const chartDom = this.$refs.c6;
                    const myChart = $echarts.init(chartDom);
                    // myChart.clear()
                    let option;

                    option = {
                        xAxis: {
                            type: 'category',
                            data:this.xtime
                        },
                        yAxis: {
                            type: 'value'
                        },
                        series: [
                            {
                                type: 'line',
                                smooth: true,
                                data:this.ydelay
                            }
                        ]
                    };
                    option && myChart.setOption(option,true);
                },1000)



            },
            drawchart1(){
                document.getElementById("chart1").removeAttribute('_echarts_instance_');
                const chartDom = document.getElementById('chart1');
                const myChart = $echarts.init(chartDom);
                let option;

                option = {
                    xAxis: {
                        type: 'category',
                        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            data: [120, 200, 150, 80, 70, 110, 130],
                            type: 'bar'
                        }
                    ]
                };

                option && myChart.setOption(option);

            },
            drawchart2(){
                document.getElementById("chart2").removeAttribute('_echarts_instance_');
                const chartDom = document.getElementById('chart2');
                const myChart = $echarts.init(chartDom);
                let option;

                option = {
                    xAxis: {
                        type: 'category',
                        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            data: [120, 200, 150, 80, 70, 110, 130],
                            type: 'bar'
                        }
                    ]
                };

                option && myChart.setOption(option);

            },
            drawchart3(){
                document.getElementById("chart3").removeAttribute('_echarts_instance_');
                const chartDom = document.getElementById('chart3');
                const myChart = $echarts.init(chartDom);
                let option;

                option = {
                    title: {
                        text: 'Referer of a Website',
                        subtext: 'Fake Data',
                        left: 'center'
                    },
                    tooltip: {
                        trigger: 'item'
                    },
                    legend: {
                        orient: 'vertical',
                        left: 'left'
                    },
                    series: [
                        {
                            name: 'Access From',
                            type: 'pie',
                            radius: '50%',
                            data: [
                                { value: 1048, name: 'Search Engine' },
                                { value: 735, name: 'Direct' },
                                { value: 580, name: 'Email' },
                                { value: 484, name: 'Union Ads' },
                                { value: 300, name: 'Video Ads' }
                            ],
                            emphasis: {
                                itemStyle: {
                                    shadowBlur: 10,
                                    shadowOffsetX: 0,
                                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                                }
                            }
                        }
                    ]
                };

                option && myChart.setOption(option);
            },

            drawchart4(){
                document.getElementById("chart4").removeAttribute('_echarts_instance_');
                const chartDom = document.getElementById('chart4');
                const myChart = $echarts.init(chartDom);
                let option;
                option = {
                    xAxis: {
                        type: 'category',
                        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            data: [820, 932, 901, 934, 1290, 1330, 1320],
                            type: 'line',
                            smooth: true
                        }
                    ]
                };

                option && myChart.setOption(option);
            },
            drawchart5(){
                document.getElementById("chart5").removeAttribute('_echarts_instance_');
                const chartDom = document.getElementById('chart5');
                const myChart = $echarts.init(chartDom);
                let option;

                option = {
                    xAxis: {
                        type: 'category',
                        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [
                        {
                            data: [820, 932, 901, 934, 1290, 1330, 1320],
                            type: 'line',
                            smooth: true
                        }
                    ]
                };

                option && myChart.setOption(option);
            },
            // drawchart6(){
            //     this.chart6 = document.getElementById("chart6").removeAttribute('_echarts_instance_');
            //     const chartDom = document.getElementById('chart6');
            //     const myChart = $echarts.init(chartDom);
            //     let option;
            //
            //     option = {
            //         xAxis: {
            //             type: 'category',
            //             data:[1,2,3]
            //         },
            //         yAxis: {
            //             type: 'value'
            //         },
            //         series: [
            //             {
            //                 type: 'line',
            //                 smooth: true,
            //                 data:[2,3,6]
            //             }
            //         ]
            //     };
            //     option && myChart.setOption(option,true);
            // },
        }

    }
</script>
<style scoped>
    .container{
        height:100%;
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
    }
    .top{
       width: 100%;
       height: 30%;
        display: flex;
       background-color: antiquewhite;
       justify-content: space-around;
       align-items: center;
    }
    .top > div{
        width: 33%;
        height: 99%;
        background-color: peru;
        border-radius: 2%;
    }
    .bottom{
        width: 100%;
        height: 70%;
        display: flex;
        background-color: aqua;
    }
    .bottom-left{
        width: 35%;
        height: 100%;
        background-color: blue;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: space-around;
    }
    .bottom-left > div{
        width: 99%;
        height: 49%;
        border-radius: 2%;
        background-color: aqua;
    }
    .bottom-right{
        width: 75%;
        height: 100%;
        background-color: brown;
        display: flex;
        justify-content: space-around;
        align-items: center;
    }
    .bottom-right > div{
        width: 99%;
        height: 99%;
        background-color: aquamarine;
        border-radius: 2%;
    }
</style>
