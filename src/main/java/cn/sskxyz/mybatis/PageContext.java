package cn.sskxyz.mybatis;

import cn.sskxyz.mybatis.mode.Page;

import java.sql.PreparedStatement;

public class PageContext {
    private Page page;
    private PreparedStatement preparedStatement;
    private String countSql;

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setPreparedStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public String getCountSql() {
        return countSql;
    }

    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }
}
