package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class searchController {

    @Autowired
    SearchFeignClient searchFeignClient;

    /**
     * 检索首页
     * @param searchParam
     * @return
     */
    @GetMapping("/list.html")
    public String search(SearchParamVo searchParam,Model model){

        Result<SearchResponseVo> search = searchFeignClient.search(searchParam);
        SearchResponseVo data = search.getData();

        //把result数据展示到页面
        //1、以前检索页点击传来的数据全部返回给前端页面
        model.addAttribute("searchParam",searchParam);

        //2、品牌面包屑位置的显示
        model.addAttribute("trademarkParam",data.getTrademarkParam());

        //3、属性面包屑集合，集合内的每一个元素都拥有attrName、attrValue、attrId.
        model.addAttribute("propsParamList",data.getPropsParmList());

        //4、所有品牌集合，集合内的每一个元素都包含tmId、tmLogoUrl、tmName.
        model.addAttribute("trademarkList",data.getTrademarkList());

        //5、所有属性集合
        model.addAttribute("attrsList",data.getAttrsList());

        //6、排序信息
        model.addAttribute("orderMap",data.getOrderMap());

        //7、所有商品列表集合，每个元素是一个对象，拥有es中每个商品的详细数据
        model.addAttribute("goodsList",data.getGoodsList());

        //8、分页信息
        model.addAttribute("pageNo",data.getPageNo());
        model.addAttribute("totalPages",data.getTotakPages());

        //9、url信息
        model.addAttribute("urlParam",data.getUrlParam());


        return "list/index";
    }

}
