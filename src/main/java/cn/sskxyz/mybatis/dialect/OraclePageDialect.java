package cn.sskxyz.mybatis.dialect;

import cn.sskxyz.mybatis.mode.Page;

public class OraclePageDialect implements PageDialect {
    @Override
    public String countSql(String sql) {
        return null;
    }

    @Override
    public String pageSql(String sql, Page page) {
        return null;
    }
}
