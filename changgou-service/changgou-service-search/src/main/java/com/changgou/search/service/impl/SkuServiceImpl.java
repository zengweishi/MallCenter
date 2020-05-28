package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.common.pojo.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/28 11:33
 * @Description:
 */
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SkuEsMapper skuEsMapper;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 导入数据到ES
     */
    @Override
    public void importSku() {
        Result<List<Sku>> result = skuFeign.findByStatus("1");
        List<SkuInfo> list = JSON.parseArray(JSON.toJSONString(result.getData()), SkuInfo.class);
        for (SkuInfo skuInfo : list) {
            Map<String,Object> map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        skuEsMapper.saveAll(list);
    }

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map<String, String> searchMap) {
        //条件构造
        NativeSearchQueryBuilder builder = getNativeSearchQueryBuilder(searchMap);
        //搜索并封装结果
        return getSearchResult(builder);
    }

    private Map getSearchResult(NativeSearchQueryBuilder builder) {
        NativeSearchQuery build = builder.build();
        Page<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(build, SkuInfo.class);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("row",skuInfos.getContent());
        resultMap.put("totalPages",skuInfos.getTotalPages());
        return resultMap;
    }

    private NativeSearchQueryBuilder getNativeSearchQueryBuilder(Map<String, String> searchMap) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        if (searchMap != null) {
            if (!StringUtils.isEmpty(searchMap.get("keywords"))) {
                builder.withQuery(QueryBuilders.matchQuery("name",searchMap.get("keywords")));
            }
        }
        return builder;
    }
}
