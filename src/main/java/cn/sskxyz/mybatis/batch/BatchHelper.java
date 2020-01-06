package cn.sskxyz.mybatis.batch;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class BatchHelper {

    private SqlSessionFactory sessionFactory;

    public BatchHelper(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * url增加rewriteBatchedStatements=true参数
     * 如果是mariadb 驱动，需设置useGeneratedKeys="false"，  mysql驱动不需要
     */
    public <T> void batchInsert(Class<? extends SQLBatcher> mapperClass, ExplicitMapper explicitMapper, List<T> data, int batchSize) {
        if (data != null && data.size() > 0) {
            SqlSession session = sessionFactory.openSession(ExecutorType.BATCH, false);
            try {
                SQLBatcher mapper = session.getMapper(mapperClass);
                int i = 0;
                while (i < data.size()) {
                    explicitMapper.insertSingleData(mapper, data.get(i++));
                    if (i % batchSize == 0) {
                        session.commit();
                    }
                }
                if (data.size() % batchSize != 0) {
                    session.commit();
                }
            } finally {
                session.close();
            }
        }
    }
}

