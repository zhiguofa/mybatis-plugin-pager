package cn.sskxyz.mybatis.dialect;

import java.util.HashMap;
import java.util.Map;

public class PageDialectFactory {

    public static PageDialect createPageDialect() {
        return createPageDialect("mysql");
    }

    public static PageDialect createPageDialect(String idForDialect) {
        Map<String, PageDialect> dialectMap = new HashMap<>();
        dialectMap.put("mysql", new MysqlPageDialect());
        dialectMap.put("oracle", new OraclePageDialect());
        return new DelegatingPageDialect(idForDialect, dialectMap);
    }
}
