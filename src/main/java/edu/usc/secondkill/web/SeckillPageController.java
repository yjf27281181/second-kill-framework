package edu.usc.secondkill.web;


import edu.usc.secondkill.common.entities.Result;
import edu.usc.secondkill.common.entities.Seckill;
import edu.usc.secondkill.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(tags = "second kill items")
@RestController
@RequestMapping("/seckillPage")
public class SeckillPageController {

    @Autowired
    private SeckillService seckillService;

    //todo
    //private ActiveMQSender activeMQSender;

    //@Autowired
    //private HttpClient httpClient;

//    @Value("${qq.captcha.url}")
//    private String url;
//
//    @Value()
//    private String aid;
//
//    @Value()
//    private String appSeretKey;

    @ApiOperation(value = "get all items", nickname="YanJF")
    @PostMapping("/list")
    public Result list() {
        List<Seckill> list = seckillService.getSeckillList();
        return Result.ok(list);
    }
}
