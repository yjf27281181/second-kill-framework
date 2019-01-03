var seckill = {
    URL: {
        now: function() {
            return "/time/now";
        },
        execution: function(seckillId) {
            return "/purchase/"+seckillId+"/3";
        }
    },
    handleSeckill(seckillId, node) {
        node.hide().html(

            "<div class='card' style='width: 100%; height: 50px;'>" +
            "   <button class='btn btn-primary btn-sm' id='killBtn'" +
            "   style='width: 80%; margin: auto; background-color: #ed9220'>Buy now with 1-Click</button>" +
            "</div>"
        );

        node.show();
        $('#killBtn').one('click',function(){
            $(this).addClass("disable");
            $.get(seckill.URL.execution(seckillId),{}, function(result) {
                $('#killBtn').html("<span class='label label-success'>"+result['msg']+"</span>")
            });
        });
    },

    getStartTime: function() {
        var now_10s = new Date((new Date()).getTime()+10000);
        return ""+now_10s.getFullYear()
            +"/"+01
            +"/"+02
            +" "+now_10s.getHours()
            +":"+now_10s.getMinutes()
            +":"+now_10s.getSeconds();

    },

    countdown: function(seckillId, nowTime, startTime, endTime) {
        if(nowTime>endTime) {
        } else if(nowTime < startTime) {
            $("#getting-started").countdown(startTime)
                .on('update.countdown', function(event) {
                    console.log(event);
                    var format = '%H: %M: %S';

                    if(event.offset.totalDays > 0) {
                        format = '%-d day%!d ' + format;
                    }
                    if(event.offset.weeks > 0) {
                        format = '%-w week%!w ' + format;
                    }
                    $('#getting-started').html(event.strftime(format));
                })
                .on('finish.countdown', function(){
                    $("#countdown").hide();
                    seckill.handleSeckill(seckillId,$("#show-span"));
                })
        } else {
            $("#countdown").hide();
            this.handleSeckill(seckillId,$("#show-span"));
        }
    },
    detail: {
        init: function(params) {
            var userId = params['userId'];
            var startTime = params['startTime'];
            var endTIme = params['endTime'];
            var seckillId = params['seckillId'];

            // $.get(seckill.URL.now(), {}, function(result){
            //     this.countdown(seckillId, result, startTime, endTIme);
            // })
        }

    }

}
//TODO
$(function() {
    console.log(seckill.getStartTime());
    seckill.countdown(1000,"2019/01/01 22:59:50", seckill.getStartTime(),1000000000000);
    console.log("start");
})
