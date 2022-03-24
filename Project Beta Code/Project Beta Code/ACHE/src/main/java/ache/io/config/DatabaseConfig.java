package ache.io.config;

import lombok.extern.log4j.Log4j2;

import javax.sql.DataSource;

//@Configuration
//@PropertySource("classpath:/application.yml")
public class DatabaseConfig {

//    @Bean
//    @ConfigurationProperties(prefix="spring.datasource.hikari")
//    public HikariConfig hikariConfig() {
//        return new HikariConfig();
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        DataSource dataSource = new HikariDataSource(hikariConfig());
//        log.info(dataSource);
//        return dataSource;
//    }
//
//    @Bean
//    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mapper/**/*.xml"));
//        sqlSessionFactoryBean.setTypeAliasesPackage("ache.io.repository");
//        return sqlSessionFactoryBean.getObject();
//    }
//
//    @Bean
//    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//
//    protected DatabaseConfig(ApplicationContext applicationContext) {
//        super();
//        this.applicationContext = applicationContext;
//    }
//
//
//    private final ApplicationContext applicationContext;

}
