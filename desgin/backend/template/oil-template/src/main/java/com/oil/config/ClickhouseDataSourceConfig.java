package com.oil.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.oil.mapper.clickhouse", sqlSessionFactoryRef = "clickhouseSqlSessionFactory")
public class ClickhouseDataSourceConfig {

    @Bean(name = "clickhouseDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.clickhouse")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "clickhouseSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("clickhouseDataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        return sqlSessionFactory.getObject();
    }

    @Bean(name = "clickhouseSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("clickhouseSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
