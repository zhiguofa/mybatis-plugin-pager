package cn.sskxyz.mybatis;

import cn.sskxyz.mybatis.dialect.MysqlPageDialect;
import cn.sskxyz.mybatis.dialect.PageDialect;
import cn.sskxyz.mybatis.mode.Page;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;


@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})
})
public class PagePlugin implements Interceptor {

    private ThreadLocal<PageContext> pageContextHolder = new ThreadLocal<>();
    private PageDialect pageDialect;

    @SuppressWarnings({"unchecked"})
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if ("query".equals(invocation.getMethod().getName())) {
            Object daoArgObj = invocation.getArgs()[1];
            if (daoArgObj instanceof Map) {
                Map<String, Object> daoArgs = (Map<String, Object>) daoArgObj;
                if (daoArgs != null && daoArgs.size() > 0) {
                    Object pageObj = daoArgs.values().stream().filter(obj -> obj instanceof Page).findFirst().orElse(null);
                    if (pageObj != null) {
                        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
                        Object parameterObj = invocation.getArgs()[1];
                        Object rowBounds = invocation.getArgs()[2];
                        Object resultHandler = invocation.getArgs()[3];

                        Configuration configuration = ms.getConfiguration();
                        BoundSql boundSql = ms.getBoundSql(parameterObj);

                        String countSql = pageDialect.countSql(boundSql.getSql());
                        PageContext context = new PageContext();
                        context.setPage((Page) pageObj);
                        context.setCountSql(countSql);
                        pageContextHolder.set(context);

                        String pageSql = pageDialect.pageSql(boundSql.getSql(), context.getPage());
                        SqlSource sqlSource = new StaticSqlSource(configuration, pageSql, boundSql.getParameterMappings());
                        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, ms.getId(), sqlSource, ms.getSqlCommandType());
                        builder.resultMaps(ms.getResultMaps());
                        builder.cache(ms.getCache());
                        builder.databaseId(ms.getDatabaseId());
                        builder.fetchSize(ms.getFetchSize());
                        builder.resource(ms.getResource());
                        return invocation.getMethod().invoke(invocation.getTarget(), builder.build(), parameterObj, rowBounds, resultHandler);
                    }
                }
            }
        }

        if ("prepare".equals(invocation.getMethod().getName())) {
            PageContext context = pageContextHolder.get();
            if (context != null) {
                Connection connection = (Connection) invocation.getArgs()[0];
                PreparedStatement pstmt = connection.prepareStatement(context.getCountSql());
                context.setPreparedStatement(pstmt);
            }
        }

        if ("setParameters".equals(invocation.getMethod().getName())) {
            PageContext context = pageContextHolder.get();
            if (context != null) {
                PreparedStatement preparedStatement = context.getPreparedStatement();
                ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
                parameterHandler.setParameters(preparedStatement);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    long total = rs.getLong(1);
                    context.getPage().setTotal(total);
                }
                rs.close();
                preparedStatement.close();
                pageContextHolder.remove();
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.pageDialect = new MysqlPageDialect();
    }
}
