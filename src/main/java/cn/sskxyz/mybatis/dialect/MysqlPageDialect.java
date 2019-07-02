package cn.sskxyz.mybatis.dialect;

import cn.sskxyz.mybatis.mode.Page;

public class MysqlPageDialect implements PageDialect {

    @Override
    public String countSql(String sql) {
        return "select count(*) from (" + sql + ")t";
    }

    @Override
    public String pageSql(String sql, Page page) {
        int offset = (page.getCurrentPage() - 1) * page.getPageSize();
        String pageSql = sql + " limit " + offset + " , " + page.getPageSize();
        return pageSql;
    }
}
