package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.vo.search.*;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsSrvice;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class GoodsServiceImpl implements GoodsSrvice {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    ElasticsearchRestTemplate esRestTemplate;

    /**
     * 保存商品
     * @param goods
     */
    @Override
    public void saveGoods(Goods goods) {
        goodsRepository.save(goods);
    }

    @Override
    public void deleteGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    @Override
    public SearchResponseVo search(SearchParamVo paramVo) {

        //动态构建出搜索条件
        Query query =buildQueryDsl(paramVo);

        //去es中搜索
        SearchHits<Goods> goods = esRestTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));

        //进行结果转换
        SearchResponseVo responseVo = buildSearchResponseResult(goods,paramVo);
        return responseVo;
    }

    /**
     * 更新热度分
     * @param skuId
     * @param score
     */
    @Override
    public void updateHotScore(Long skuId, Long score) {
        //找到商品
        Goods goods = goodsRepository.findById(skuId).get();
        //更新得分
        goods.setHotScore(score);
        //同步到ES
        goodsRepository.save(goods);
    }

    /**
     * 根据检索到的记录，构建响应数据
     * @param goods
     * @return
     */
    private SearchResponseVo buildSearchResponseResult(SearchHits<Goods> goods,SearchParamVo paramVo) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();

        //当时前端传来的所有参数返回给前端
        searchResponseVo.setSearchParam(paramVo);
        //构建品牌面包屑
        if (!StringUtils.isEmpty(paramVo.getTrademark())) {
            searchResponseVo.setTrademarkParam("品牌："+paramVo.getTrademark().split(":")[1]);
        }
        //平台属性面包屑
        if (paramVo.getProps()!=null && paramVo.getProps().length>0) {
            List<SearchAttr> propsParamList = new ArrayList<>();
            for (String prop : paramVo.getProps()) {
                String[] split = prop.split(":");
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.parseLong(split[0]));
                searchAttr.setAttrName(split[2]);
                searchAttr.setAttrValue(split[1]);
                propsParamList.add(searchAttr);
            }
            searchResponseVo.setPropsParmList(propsParamList);
        }


        //所有品牌列表 。需要ES聚合分析
        List<TrademarkVo> trademarkVos =  buildTrademarkList(goods);
        searchResponseVo.setTrademarkList(trademarkVos);

        //所有属性列表 。需要ES聚合分析
        List<AttrVo> attrsList = buildAttrList(goods);
        searchResponseVo.setAttrsList(attrsList);

        //回显：返回排序信息
        if (!StringUtils.isEmpty(paramVo.getOrder())) {

            String order = paramVo.getOrder();
            OrderMapVo mapVo = new OrderMapVo();
            mapVo.setType(order.split(":")[0]);
            mapVo.setType(order.split(":")[1]);

            searchResponseVo.setOrderMap(mapVo);
        }

        //所有搜索到的商品列表
        List<Goods> goodsList = new ArrayList<>();
        List<SearchHit<Goods>> hits = goods.getSearchHits();
        for (SearchHit<Goods> hit : hits) {

            //这条命中记录的商品
            Goods content = hit.getContent();
            //如果模糊检索，会有高亮标题
            if (!StringUtils.isEmpty(paramVo.getKeyword())){
                String highlightTitle = hit.getHighlightField("title").get(0);
                //设置高亮标题
                content.setTitle(highlightTitle);
            }
            goodsList.add(content);
        }
        searchResponseVo.setGoodsList(goodsList);

        //页码
        searchResponseVo.setPageNo(paramVo.getPageNo());
        //总页码
        long totalHits = goods.getTotalHits();
        long ps = totalHits%SysRedisConst.SEARCH_PAGE_SIZE == 0?
                totalHits/SysRedisConst.SEARCH_PAGE_SIZE:
                (totalHits/SysRedisConst.SEARCH_PAGE_SIZE+1);


        searchResponseVo.setTotakPages(new Integer(ps+""));

        //老链接拼接
        
        String url = makeUrlParam(paramVo);
        
        searchResponseVo.setUrlParam(url);
        return searchResponseVo;

    }

    /**
     * 分析得到，当前检索的结果中，所有商品涉及了多少种平台属性
     * @param goods
     * @return
     */
    private List<AttrVo> buildAttrList(SearchHits<Goods> goods) {

        List<AttrVo> attrVos  = new ArrayList<>();

        //拿到整个属性的聚合结果
        ParsedNested attrAgg = goods.getAggregations().get("attrAgg");

        //拿到属性id的聚合结果
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");

        //遍历所有属性id
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            AttrVo attrVo = new AttrVo();

            //属性id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            //属性名
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            //所有属性值
            List<String> attrValues = new ArrayList<>();
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            for (Terms.Bucket valueBucket : attrValueAgg.getBuckets()) {
                String value = valueBucket.getKeyAsString();
                attrValues.add(value);
            }
            attrVo.setAttrValueList(attrValues);

            attrVos.add(attrVo);
        }
        return attrVos;
    }

    /**
     * 分析得到当前结果中涉及了多少品牌
     * @param goods
     * @return
     */
    private List<TrademarkVo> buildTrademarkList(SearchHits<Goods> goods) {
        List<TrademarkVo> trademarkVos = new ArrayList<>();

        //拿到tmIdAgg聚合
        ParsedLongTerms tmIdAgg = goods.getAggregations().get("tmIdAgg");

        //拿到品牌Id桶聚合中的每个数据
        for (Terms.Bucket bucket : tmIdAgg.getBuckets()){
            TrademarkVo trademarkVo = new TrademarkVo();

            //获取品牌id
            Long tmId = bucket.getKeyAsNumber().longValue();
            trademarkVo.setTmId(tmId);

            //获取品牌名
            ParsedStringTerms tmNameAgg = bucket.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmName(tmName);

            //获取品牌logo
            ParsedStringTerms tmLogoAgg = bucket.getAggregations().get("tmLogoAgg");
            String tmLogo = tmLogoAgg.getBuckets().get(0).getKeyAsString();
            trademarkVo.setTmLogoUrl(tmLogo);

            trademarkVos.add(trademarkVo);

        }
        return trademarkVos;
    }

    /**
     * 生成老链接
     * @param paramVo
     * @return
     */
    private String makeUrlParam(SearchParamVo paramVo) {

        //连接头list.html?&k=v
        StringBuilder builder = new StringBuilder("list.html?");

        //拼接三级分类所有参数
        if (paramVo.getCategory1Id()!=null){
            builder.append("category1Id="+paramVo.getCategory1Id());
        }
        if (paramVo.getCategory2Id()!=null){
            builder.append("category2Id="+paramVo.getCategory2Id());
        }
        if (paramVo.getCategory3Id()!=null){
            builder.append("category3Id="+paramVo.getCategory3Id());
        }

        //拼接关键字
        if (!StringUtils.isEmpty(paramVo.getKeyword())){
            builder.append("&keyword="+paramVo.getKeyword());
        }

        //拼品牌
        if (!StringUtils.isEmpty(paramVo.getTrademark())){
            builder.append("&trademark="+paramVo.getTrademark());
        }

        //拼属性
        if (paramVo.getProps()!=null && paramVo.getProps().length>0){
            for (String prop : paramVo.getProps()) {
                builder.append("&props="+prop);
            }
        }

//        拼排序
//          builder.append("&order="+paramVo.getOrder());
//        拼页码
//        builder.append("&pageNo="+paramVo.getPageNo());

        String url = builder.toString();

        return url;
    }

    /**
     * 根据前端传来的参数构建检索条件
     * @param paramVo
     * @return
     */
    private Query buildQueryDsl(SearchParamVo paramVo) {

        //准备bool
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //给bool中准备must的各个条件
        if (paramVo.getCategory1Id() != null){

            boolQuery.must(QueryBuilders.termQuery("category1Id",paramVo.getCategory1Id()));

        }
        if (paramVo.getCategory2Id() != null){

            boolQuery.must(QueryBuilders.termQuery("category2Id",paramVo.getCategory2Id()));

        }
        if (paramVo.getCategory3Id() != null){

            boolQuery.must(QueryBuilders.termQuery("category3Id",paramVo.getCategory3Id()));

        }

        //前端传入keyword，要进行全文检索
        if (!StringUtils.isEmpty(paramVo.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("title",paramVo.getKeyword()));
        }
        if (!StringUtils.isEmpty(paramVo.getTrademark())){
            long tmId = Long.parseLong(paramVo.getTrademark().split(":")[0]);
            boolQuery.must(QueryBuilders.termQuery("ymId",tmId));
        }

        //准备一个原生检索条件
        NativeSearchQuery query = new NativeSearchQuery(boolQuery);

        //前端传入属性
        String[] props = paramVo.getProps();
        if (props!=null && props.length > 0){
            for (String prop : props) {
                String[] split = prop.split(":");
                Long attrId = Long.parseLong(split[0]);
                String attrValue = split[1];
                BoolQueryBuilder nestedBool = QueryBuilders.boolQuery();
                nestedBool.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBool.must(QueryBuilders.termQuery("attrs.attrValue",attrValue));

                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBool, ScoreMode.None);
                boolQuery.must(nestedQuery);
            }
        }


            if (!StringUtils.isEmpty(paramVo.getOrder())) {

                String[] split = paramVo.getOrder().split(":");

                String orderField = "hotScore";
                switch (split[0]){
                    case "1": orderField = "hotScore";break;
                    case "2": orderField = "price";break;
                    case "3": orderField = "createTime";break;
                    default: orderField = "hotScore";
                }
                Sort sort = Sort.by(orderField);
                if (split[1].equals("asc")){
                    sort = sort.ascending();
                }else {
                    sort = sort.descending();
                }
                query.addSort(sort);
            }

            if (paramVo.getPageNo()!=null && paramVo.getPageNo() > 0){

                PageRequest request = PageRequest.of(paramVo.getPageNo()-1, SysRedisConst.SEARCH_PAGE_SIZE);
                query.setPageable(request);
            }

            //高亮显示
            if (!StringUtils.isEmpty(paramVo.getKeyword())){
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.field("title")
                                .preTags("<span style='color:red'>")
                                .postTags("</span>");

                HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);
                query.setHighlightQuery(highlightQuery);
            }


        //品牌聚合 - 品牌聚合分析条件
        TermsAggregationBuilder tmIdAgg = AggregationBuilders
                .terms("tmIdAgg")
                .field("tmId")
                .size(1000);

        //3.1 品牌聚合 - 品牌名子聚合
        TermsAggregationBuilder tmNameAgg = AggregationBuilders.terms("tmNameAgg").field("tmName").size(1);
        //3.2 品牌聚合 - 品牌logo子聚合
        TermsAggregationBuilder tmLogoAgg = AggregationBuilders.terms("tmLogoAgg").field("tmLogoUrl").size(1);

        tmIdAgg.subAggregation(tmNameAgg);
        tmIdAgg.subAggregation(tmLogoAgg);

        //品牌id聚合条件拼装完成
        query.addAggregation(tmIdAgg);

        //属性聚合
        //4.1 属性的整个嵌入式聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "attrs");

        //4.2 attrid 聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(100);

        //4.3 attrname 聚合
        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1);

        //4.4 attrvalue 聚合
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(100);

        attrIdAgg.subAggregation(attrNameAgg);
        attrIdAgg.subAggregation(attrValueAgg);
        attrAgg.subAggregation(attrIdAgg);

        //添加整个属性的聚合条件
        query.addAggregation(attrAgg);

        return query;
    }
}
