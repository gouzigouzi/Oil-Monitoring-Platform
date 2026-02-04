package com.oil.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.oil.mapper.postgres", sqlSessionFactoryRef = "postgresSqlSessionFactory")
public class PostgresDataSourceConfig {

    @Primary
    @Bean(name = "postgresDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.postgres")
    public DataSource postgresDataSource() {
        DataSource build = DataSourceBuilder.create().build();
        return build;
    }

    @Primary
    @Bean(name = "postgresSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("postgresDataSource") DataSource dataSource,
            @Qualifier("mybatisPlusInterceptor") MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.addInterceptor(mybatisPlusInterceptor);
        sqlSessionFactory.setConfiguration(configuration);
        return sqlSessionFactory.getObject();
    }

    @Primary
    @Bean(name = "postgresSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("postgresSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
