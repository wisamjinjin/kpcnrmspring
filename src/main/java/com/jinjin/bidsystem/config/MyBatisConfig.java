/* MyBatisConfig 클래스: MyBatis 설정을 위한 Spring Configuration 클래스 */

/*
기능 및 용도:
1. MyBatis와 Spring 간의 통합 설정을 제공
2. MyBatis Mapper 인터페이스 및 매퍼 XML 파일과의 연동을 설정
3. SQL 실행 시 사용할 SqlSessionFactory와 SqlSessionTemplate Bean을 생성 및 관리

주요 구성 요소:
- @Configuration: 
  - 이 클래스가 Spring의 설정 파일임을 나타냄.

- @MapperScan("com.jinjin.bidsystem.mapper"): 
  - 지정된 경로에서 MyBatis Mapper 인터페이스를 스캔하고 자동으로 Bean으로 등록.

- sqlSessionFactory:
  - DataSource를 사용하여 MyBatis의 SqlSessionFactory를 생성.
  - 매퍼 XML 파일의 경로를 설정하여 SQL 매핑 파일과 연동.
  - (옵션) MyBatisLoggingInterceptor를 추가하여 SQL 및 파라미터 로깅 가능.

- sqlSessionTemplate:
  - SqlSessionFactory를 사용하여 MyBatis와의 상호작용을 관리하는 SqlSessionTemplate 객체 생성.
  - 데이터베이스 트랜잭션 관리 및 MyBatis Mapper 호출을 간소화.
*/

/*
  기타:
  . 실행되는 sql과 parameter를 디버깅용으로 logging하기 위해서는 아래 sql logging용 comment를 해제한다.  */
package com.jinjin.bidsystem.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.jinjin.bidsystem.interceptor.MyBatisLoggingInterceptor; // sql logging용

import javax.sql.DataSource;

@Configuration
@MapperScan("com.jinjin.bidsystem.mapper")  // Mapper 인터페이스 경로
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
            sessionFactoryBean.setPlugins(new MyBatisLoggingInterceptor()); // sql logging용
        return sessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
