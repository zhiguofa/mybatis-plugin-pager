package cn.sskxyz.mybatis.batch;

public interface ExplicitMapper<T> {

    void insertSingleData(SQLBatcher mapper, T data);

}
