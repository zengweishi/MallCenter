package com.changgou.goods.dto;

import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/25 14:26
 * @Description:
 */
@Data
public class Goods implements Serializable {

    private static final long serialVersionUID = -2171505758895823836L;

    private Spu spu;
    private List<Sku> skuList;
}
