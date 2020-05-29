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
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

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
        Map searchResult = getSearchResult(builder);
        //搜索分类和品牌
        searchCategoryList(builder,searchResult,searchMap);
        //搜索规格
        Map<String, Set<String>> specMap = searchSpec(builder);
        searchResult.put("specMap",specMap);
        return searchResult;
    }

    public Map<String, Set<String>> searchSpec(NativeSearchQueryBuilder builder) {
        builder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword"));
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        Aggregations aggregations = skuInfos.getAggregations();
        StringTerms stringTerms = aggregations.get("skuSpec");
        ArrayList<String> list = new ArrayList<>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            //获取规格json串
            list.add(bucket.getKeyAsString());
        }
        //将规格封装到一个Map中
        Map<String,Set<String>> resultMap = new HashMap<>();
        for (String specStr : list) {
            Map<String,String> map = JSON.parseObject(specStr, Map.class);
            for (Map.Entry<String,String> entry : map.entrySet()) {
                if (resultMap.get(entry.getKey()) != null) {
                    resultMap.get(entry.getKey()).add(entry.getValue());
                } else {
                    HashSet<String> set = new HashSet<>();
                    set.add(entry.getValue());
                    resultMap.put(entry.getKey(),set);
                }
            }
        }
        return resultMap;
    }

    /**
     * 搜索分类分组数据展示在界面上的搜索栏
     * @return
     */
    public void searchCategoryList(NativeSearchQueryBuilder builder,Map<String,Object> resultMap,Map<String, String> searchMap) {
        //根据categoryName进行统计，统计出来的列名命名为skuCategory(指定查询域并取别名)
        builder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));
        builder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));
        //执行搜索
        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(builder.build(), SkuInfo.class);
        //获取所有的分组查询数据
        Aggregations aggregations = skuInfos.getAggregations();
        if (searchMap == null || searchMap.get("category") != null) {
            //从所有数据中获取别名为skuCategory的数据
            StringTerms stringTerms = aggregations.get("skuCategory");
            //封装分类List集合，将结果存入到List中
            ArrayList<String> list = new ArrayList<>();
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                list.add(bucket.getKeyAsString());
            }
            resultMap.put("categoryList",list);
        }
        if (searchMap == null || searchMap.get("brand") != null) {
            //封装品牌List集合，将结果存入到List中
            StringTerms skuBrandTerms = aggregations.get("skuBrand");
            ArrayList<String> brandList = new ArrayList<>();
            for (StringTerms.Bucket bucket : skuBrandTerms.getBuckets()) {
                brandList.add(bucket.getKeyAsString());
            }
            resultMap.put("brandList",brandList);
        }
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
