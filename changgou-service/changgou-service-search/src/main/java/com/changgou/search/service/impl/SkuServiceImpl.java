package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.common.pojo.PageResult;
import com.changgou.common.pojo.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/28 11:33
 * @Description: ES实现SKU搜索
 */
@Service
@Slf4j
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
        log.error("查询条件searchMap:{}",JSON.toJSONString(searchMap));
        //条件构造
        NativeSearchQueryBuilder builder = buildBasicQuery(searchMap);
        //搜索并封装结果
        Map searchResult = getSearchResult(builder);
        //搜索分类和品牌
        searchCategoryList(builder,searchResult,searchMap);
        //搜索规格
        Map<String, Set<String>> specMap = searchSpec(builder);
        searchResult.put("specMap",specMap);
        log.error("搜索结果：{}",JSON.toJSONString(searchMap));
        return searchResult;
    }

    /**
     * 搜索规格展示到界面上的搜索栏
     * @param builder
     * @return
     */
    public Map<String, Set<String>> searchSpec(NativeSearchQueryBuilder builder) {
        //SkInfo中没有指定KeyWord属性(Keyword不分词)，那么此时field中要指明.keyword，不要把规格分词
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
        log.error("规格Map结果：{}",JSON.toJSONString(resultMap));
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
        if (searchMap == null || searchMap.get("category") == null) {
            //从所有数据中获取别名为skuCategory的数据
            StringTerms stringTerms = aggregations.get("skuCategory");
            //封装分类List集合，将结果存入到List中
            ArrayList<String> list = new ArrayList<>();
            for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
                list.add(bucket.getKeyAsString());
            }
            log.error("categoryList结果：{}",JSON.toJSONString(list));
            resultMap.put("categoryList",list);
        }
        if (searchMap == null || searchMap.get("brand") == null) {
            //封装品牌List集合，将结果存入到List中
            StringTerms skuBrandTerms = aggregations.get("skuBrand");
            ArrayList<String> brandList = new ArrayList<>();
            for (StringTerms.Bucket bucket : skuBrandTerms.getBuckets()) {
                brandList.add(bucket.getKeyAsString());
            }
            log.error("brandList结果：{}",JSON.toJSONString(brandList));
            resultMap.put("brandList",brandList);
        }
    }

    /**
     * 封装结果
     * @param builder
     * @return
     */
    private Map getSearchResult(NativeSearchQueryBuilder builder) {
        //高亮域配置
        HighlightBuilder.Field highlightBuilder = new HighlightBuilder.Field("name").preTags("<span style=\"color:red\">").postTags("</span>").fragmentSize(100);
        //添加高亮域
        builder.withHighlightFields(highlightBuilder);
        NativeSearchQuery build = builder.build();
        //分页搜索
        AggregatedPage<SkuInfo> skuInfoPage = elasticsearchTemplate.queryForPage(build, SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<T> list = new ArrayList<T>();
                //循环搜索数据
                for (SearchHit hit : searchResponse.getHits()) {
                    //获取非高亮数据
                    SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);
                    //获取高亮数据
                    HighlightField name = hit.getHighlightFields().get("name");
                    //将高亮数据替换到非高亮的skuInfo实体中
                    if (name != null) {
                        //定义一个字符接收高亮数据
                        StringBuffer stringBuffer = new StringBuffer();
                        for (Text text : name.getFragments()) {
                            log.error("text:{}",JSON.toJSONString(text));
                            stringBuffer.append(text);
                        }
                        //将非高亮数据替换成高亮数据
                        skuInfo.setName(stringBuffer.toString());
                    }
                    list.add((T) skuInfo);
                }
                return new AggregatedPageImpl<T>(list,pageable,searchResponse.getHits().getTotalHits());
            }
        });
        Page<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(build, SkuInfo.class);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("row",skuInfos.getContent());
        resultMap.put("totalPages",skuInfos.getTotalPages());
        return resultMap;
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap) {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        if (searchMap != null) {
            //1.关键字查询
            if (!StringUtils.isEmpty(searchMap.get("keywords"))) {
                builder.withQuery(QueryBuilders.matchQuery("name",searchMap.get("keywords")));
            }

            //构建bool查询 用于分类和品牌
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (!StringUtils.isEmpty(searchMap.get("category"))) {
                //2.分类搜索 match query搜索的时候，首先会解析查询字符串，进行分词，然后查询
                boolQueryBuilder.must(QueryBuilders.matchQuery("categoryName",searchMap.get("category")));
            }
            if (!StringUtils.isEmpty(searchMap.get("brand"))) {
                //3.品牌搜索  term query,输入的查询内容是什么，就会按照什么去查询，并不会解析查询内容，对它分词
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
            }

            //4.构建规格，搜索入参中规格以spec_为前缀标记
            for (String key : searchMap.keySet()) {
                if (key.startsWith("spec_")) {
                    //"specMap."+key.substring(5)+".keyword" 加keyword不对specMap.XXXXX进行分词 直接精确查询
                    boolQueryBuilder.must(QueryBuilders.matchQuery("specMap."+key.substring(5)+".keyword",searchMap.get(key)));
                }
            }

            //5.构建价格区间 0-500 500-1000 1000
            String price = searchMap.get("price");
            if (!StringUtils.isEmpty(price)) {
                String[] priceArr = price.split("-");
                if (priceArr.length > 1) {
                    //price <= y
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(priceArr[1]));
                }
                //x<price
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gt(priceArr[0]));
            }

            //6.排序
            String sortRule = searchMap.get("sortRule"); //ASC DESC
            String sortField = searchMap.get("sortField"); //price
            if (!StringUtils.isEmpty(sortRule) && !StringUtils.isEmpty(sortField)) {
                builder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
            }
            //分页
            Integer pageNo = pageCovert(searchMap);
            Integer size = 3;
            PageRequest pageRequest = PageRequest.of(pageNo-1, size);
            builder.withPageable(pageRequest);

            //添加过滤条件
            builder.withQuery(boolQueryBuilder);
        }
        return builder;
    }

    private Integer pageCovert(Map<String, String> searchMap) {
        try {
            return Integer.valueOf(searchMap.get("pageNum"));
        } catch (Exception e) {
            log.error("页数pageNum异常");
        }
        return 1;
    }
}
