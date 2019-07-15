package cn.sskxyz.mybatis.dialect;

import cn.sskxyz.mybatis.mode.Page;

import java.util.HashMap;
import java.util.Map;

public class DelegatingPageDialect implements PageDialect {

    private final String idForPage;

    private final Map<String, PageDialect> pageDialectMap;

    private final PageDialect pageDialect;


    public DelegatingPageDialect(String idForPage, Map<String, PageDialect> pageDialectMap) {
        if (idForPage == null) {
            throw new IllegalArgumentException("idForPage 不能为null");
        }
        if (!pageDialectMap.containsKey(idForPage)) {
            throw new IllegalArgumentException("idForPage " + idForPage + "没有找到分页方言");
        }

        this.idForPage = idForPage;
        this.pageDialectMap = new HashMap<>(pageDialectMap);
        this.pageDialect = pageDialectMap.get(idForPage);
    }

    @Override
    public String countSql(String sql) {
        return pageDialect.countSql(sql);
    }

    @Override
    public String pageSql(String sql, Page page) {
        return pageDialect.pageSql(sql, page);
    }
}
