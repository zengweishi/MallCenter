package com.changgou.goods.service.impl;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/21 15:27
 * @Description:
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    @Override
    public Brand findBrand(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void addBrand(Brand brand) {
        brandMapper.insertSelective(brand);
    }

    @Override
    public void updateBrand(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void deleteBrand(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Brand> findList(Brand brand) {
        //构建查询条件
        Example example = createExample(brand);
        //根据查询条件查询
        return brandMapper.selectByExample(example);
    }

    @Override
    public PageInfo<Brand> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page,size);
        //分页查询
        return new PageInfo<Brand>(brandMapper.selectAll());
    }

    /**
     * 条件&&分页查询
     * @param brand
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Brand> findBrandByPage(Brand brand, int page, int size) {
        //分页
        PageHelper.startPage(page,size);
        //条件
        Example example = createExample(brand);
        return new PageInfo<Brand>(brandMapper.selectByExample(example));
    }

    /**
     * 构建查询条件
     * @param brand
     * @return
     */
    private Example createExample(Brand brand) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if (brand != null) {
            //品牌名称
            if (!StringUtils.isEmpty(brand.getName())) {
                criteria.andLike("name","%"+brand.getName()+"%");
            }
            //品牌图片地址
            if (!StringUtils.isEmpty(brand.getImage())) {
                criteria.andLike("image","%"+brand.getImage()+"%");
            }
            //品牌首字母
            if (!StringUtils.isEmpty(brand.getLetter())) {
                criteria.andLike("letter","%"+brand.getLetter()+"%");
            }
            //品牌ID
            if (brand.getId() != null) {
                criteria.andEqualTo("id",brand.getId());
            }
            //排序
            if (brand.getSeq() != null) {
                criteria.andEqualTo("seq",brand.getSeq());
            }
        }
        return example;
    }


}
