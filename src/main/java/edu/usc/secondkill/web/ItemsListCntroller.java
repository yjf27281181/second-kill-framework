package edu.usc.secondkill.web;

import edu.usc.secondkill.common.entities.Result;
import edu.usc.secondkill.common.entities.Seckill;
import edu.usc.secondkill.common.enums.SeckillStatEnum;
import edu.usc.secondkill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Controller
public class ItemsListCntroller {

    @Autowired
    SeckillService seckillService;

    @GetMapping("list")
    private String getList(Model model) {
        List<Seckill> items = seckillService.getSeckillList();
        System.out.println(items);
        model.addAttribute("items", items);

        return "index";
    }

    @GetMapping("/purchase/{seckillId}/{userId}")
    @ResponseBody
    private Result purchase(@PathVariable("seckillId") String seckillId,
                            @PathVariable("userId") String userId,
                            Model model) {
        try {
            return seckillService.startSeckillLock(Long.valueOf(seckillId), Long.valueOf(userId));
        } catch (Exception e) {
            return Result.error(SeckillStatEnum.MUCH);
        }
    }

    @GetMapping("seckill/detail/{seckillId}")
    public String getDetail(@PathVariable("seckillId") long seckillId) {
        return "detail";
    }
}
