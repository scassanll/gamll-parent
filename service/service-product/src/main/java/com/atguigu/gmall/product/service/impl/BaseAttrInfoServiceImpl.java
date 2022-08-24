package com.atguigu.gmall.product.service.impl;

import com.alibaba.nacos.common.JustForTest;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author Scassanl
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2022-08-22 23:43:37
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Resource
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Resource
    BaseAttrValueMapper baseAttrValueMapper;

    /**
     * 查询某个分类下的所有平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(Long category1Id, Long category2Id, Long category3Id) {
        List<BaseAttrInfo> list = baseAttrInfoMapper.getAttrInfoAndValueByCategoryId(category1Id,category2Id, category3Id);
        return list;
    }

    /**
     * 保存属性信息
     * @param info
     */
    @Override
    public void saveAttrInfo(BaseAttrInfo info) {

        Long infoId = info.getId();

        //如果id不为null，执行修改
        if (info.getId()!=null){


            //修改属性名
            baseAttrInfoMapper.updateById(info);

            List<BaseAttrValue> attrValueList = info.getAttrValueList();
            List<Long> listId = new ArrayList<>();

            //遍历没有被删除的属性ID并存入集合
            for (BaseAttrValue attrValue : attrValueList) {
                Long id = attrValue.getId();

                //ID不为null则没有被删除
                if (id != null)
                {
                    listId.add(id);
                }
            }

            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", infoId);
            if (listId.size() > 0) {
                //设置条件：ID不在listId集合中的属性均执行删除
                wrapper.notIn("id", listId);
                baseAttrValueMapper.delete(wrapper);
            }else if (listId.size() == 0){

                //listId为空则删除全部
                baseAttrValueMapper.delete(wrapper);
            }

            //遍历需要修改的每个属性值
            for (BaseAttrValue attrValue : attrValueList) {

                //如果属性id为null则新增属性
                if (attrValue.getId()==null){
                    attrValue.setAttrId(infoId);
                    baseAttrValueMapper.insert(attrValue);

                //不为null则修改属性
                }else {

                    baseAttrValueMapper.updateById(attrValue);
                }
            }

        //如果id为null，执行新增
        }else {

            //保存平台属性名称
            baseAttrInfoMapper.insert(info);

            //遍历属性中的每个值并插入数据库
            List<BaseAttrValue> attrValueList = info.getAttrValueList();
            for (BaseAttrValue value : attrValueList) {

                //回填属性名的自增id
                value.setId(infoId);

                //执行新增mapper
                baseAttrValueMapper.insert(value);
            }
        }
    }
}




