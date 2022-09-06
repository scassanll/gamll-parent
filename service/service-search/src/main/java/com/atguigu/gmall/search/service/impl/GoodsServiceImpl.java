package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsSrvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl implements GoodsSrvice {

    @Autowired
    GoodsRepository goodsRepository;

    /**
     * 保存商品
     * @param goods
     */
    @Override
    public void saveGoods(Goods goods) {
        goodsRepository.save(goods);
    }
}
