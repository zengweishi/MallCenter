package com.changgou.search.service;

import java.util.Map;

public interface SkuService {
    void importSku();

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    Map search(Map<String,String> searchMap);
}
