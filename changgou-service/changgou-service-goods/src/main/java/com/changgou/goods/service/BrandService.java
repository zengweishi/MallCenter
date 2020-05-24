package com.changgou.goods.service;

import com.changgou.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/21 15:27
 * @Description:
 */
@Service
public interface BrandService {
    /**
     * 查询所有品牌
     * @return
     */
    List<Brand> findAll();

    /**
     * 根据ID查询品牌
     */
    Brand findBrand(Integer id);

    /**
     * 新增品牌
     */
    void addBrand(Brand brand);

    /**
     * 修改品牌
     */
    void updateBrand(Brand brand);

    /**
     * 删除品牌
     */
    void deleteBrand(Integer id);

    /**
     * 多条件搜索品牌
     */
    List<Brand> findList(Brand brand);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> findPage(int page,int size);

    /**
     * 条件&&分页查询
     */
    PageInfo<Brand> findBrandByPage(Brand brand,int page,int size);
}
