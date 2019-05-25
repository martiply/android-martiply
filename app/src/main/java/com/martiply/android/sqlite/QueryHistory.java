package com.martiply.android.sqlite;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "QueryHistory")
public class QueryHistory {

    @Id
    private String query;

    @Property(nameInDb = "ts")
    private Long ts;

    @Generated(hash = 815636513)
    public QueryHistory(String query, Long ts) {
        this.query = query;
        this.ts = ts;
    }

    @Generated(hash = 847348890)
    public QueryHistory() {
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Long getTs() {
        return this.ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }
}
