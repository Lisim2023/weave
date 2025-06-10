package cn.filaura.weave.ref;


import java.util.Collection;

/**
 * 直接从引用数据源查询数据
 *
 * @see RefDataProvider
 */
public class DirectDataSourceRefDataProvider implements RefDataProvider {

    private RefDataSource refDataSource;



    public DirectDataSourceRefDataProvider(RefDataSource refDataSource) {
        this.refDataSource = refDataSource;
    }



    @Override
    public void getRefData(Collection<RefInfo> refInfos) {
        for (RefInfo query : refInfos) {
            RefInfo data = refDataSource.queryRefData(query.getTable(), query.getColumns(), query.getKey(), query.getKeyValues());
            if (data != null ) {
                query.setResults(data.getResults());
            }
        }
    }



    public RefDataSource getRefDataSource() {
        return refDataSource;
    }

    public void setRefDataSource(RefDataSource refDataSource) {
        this.refDataSource = refDataSource;
    }
}
