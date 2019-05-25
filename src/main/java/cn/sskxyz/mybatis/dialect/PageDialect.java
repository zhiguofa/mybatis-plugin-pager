package cn.sskxyz.mybatis.dialect;

import cn.sskxyz.mybatis.mode.Page;

public interface PageDialect {

    String countSql(String sql);

    String pageSql(String sql, Page page);
}
