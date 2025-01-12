/*
MyBatisLoggingInterceptor 클래스: MyBatis SQL 및 매개변수 로깅용 인터셉터

기능 및 용도:
1. MyBatis에서 실행되는 SQL 쿼리와 매개변수를 로깅하여 디버깅 용도로 사용.
2. MyBatis의 `Interceptor`를 구현하여 StatementHandler의 `prepare` 메서드를 가로채 SQL 및 매개변수를 추출하고 로깅.

주요 구성 요소:
- @Intercepts:
  - MyBatis에서 인터셉터를 적용할 메서드를 지정.
  - `type = StatementHandler`는 SQL 실행에 관련된 작업을 가로채는 데 사용.
  - `method = "prepare"`는 SQL 준비 단계에서 인터셉터를 동작하게 설정.
  - `args = {Connection.class, Integer.class}`는 해당 메서드의 매개변수를 명시.

- intercept(Invocation invocation):
  - 인터셉터의 핵심 로직을 구현.
  - `StatementHandler`에서 SQL과 매개변수를 가져와 로깅.
  - `invocation.proceed()`를 호출하여 원래 MyBatis의 로직을 이어서 실행.

- plugin(Object target):
  - MyBatis가 인터셉터를 플러그인으로 감쌀 수 있도록 설정.
  - `Plugin.wrap(target, this)`를 통해 인터셉터를 적용.

- setProperties(Properties properties):
  - 인터셉터에 사용자 정의 속성을 전달하는 메서드 (사용하지 않으면 비워 둠).

로깅 내용:
1. SQL:
   - 실행할 SQL 쿼리를 로깅.
2. Parameters:
   - SQL에 전달되는 매개변수를 로깅.

활용:
- 이 인터셉터는 디버깅 또는 개발 환경에서 SQL과 매개변수를 확인하는 데 유용.
- 성능 문제를 방지하기 위해 프로덕션 환경에서는 사용을 제한하거나 제거하는 것이 권장됨.
*/

package com.jinjin.bidsystem.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jinjin.bidsystem.config.SecurityConfig;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})

/*
 * Mybais의 sql과 paramter를 로깅하여 디버깅 용도로 사용용
 */
public class MyBatisLoggingInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        String sql = handler.getBoundSql().getSql();
        Object parameterObject = handler.getBoundSql().getParameterObject();

        logger.info("Executing SQL: " + sql);
        logger.info("\n\n------ Parameters: " + parameterObject + "\n");

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // Custom properties (필요한 경우)
    }
}
