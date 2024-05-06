package com.qshy.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.qshy.mapper")
public class MPConfig {
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor page = new MybatisPlusInterceptor();
        page.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return page;
    }
//    int pageNum=2;//当前页码
//    int pageSize=3;//当前页码包含3条数据
//
//    IPage<User> page=new Page<>(pageNum,pageSize);
//
//    //条件构造器,可以根据条件进行分页查询
//    //LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        userMapper.selectPage(page,null);//queryWrapper=null，则是查询所有数据进行分页

}
