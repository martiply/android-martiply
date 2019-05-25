package com.martiply.android.suggestion;

import com.martiply.android.sqlite.QueryHistory;

import java.util.List;

public class EventGotQueryHistory {

    public List<QueryHistory> queryHistories;

    public EventGotQueryHistory(List<QueryHistory> queryHistories) {
        this.queryHistories = queryHistories;
    }
}
