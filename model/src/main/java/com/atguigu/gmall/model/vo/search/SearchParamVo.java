package com.atguigu.gmall.model.vo.search;

import lombok.Data;

@Data
public class SearchParamVo {
    Long category3Id;
    Long category2Id;
    Long category1Id;

    String keyword;
    String[] props;
    String trademark;
    String order = "1:desc";

    Integer pageNo = 1;

}
