package com.atguigu.gmall.model.vo.search;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import lombok.Data;

import java.util.List;

/**
 * 给检索页响应的所有数据
 */
@Data
public class SearchResponseVo {
    private SearchParamVo searchParam;
    private String trademarkParam;
    private List<SearchAttr> propsParmList;

    private List<TrademarkVo> trademarkList;

    private List<AttrVo> attrsList;

    private OrderMapVo orderMap;
    private List<Goods> goodsList;


    private Integer pageNo;
    private Integer totakPages;
    private String urlParam;
}
