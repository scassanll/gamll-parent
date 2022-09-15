package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderTradeController {

    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * 跳转到订单页
     */
    @GetMapping("/trade.html")
    public String tradePage(Model model){

        Result<OrderConfirmDataVo> orderConfirmDataVo = orderFeignClient.getOrderConfirmDataVo();

        if (orderConfirmDataVo.isOk()) {
            OrderConfirmDataVo data = orderConfirmDataVo.getData();
            model.addAttribute("detailArrayList", data.getDetailArrayList());
            model.addAttribute("totalNum", data.getTotalNum());
            model.addAttribute("totalAmount", data.getTotalAmount());
            model.addAttribute("userAddressList", data.getUserAddressList());
            model.addAttribute("tradeNo", data.getTradeNo());

        }
        return "order/trade";
    }

}
