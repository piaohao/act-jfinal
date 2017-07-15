package org.piaohao.act.jfinal.db;

import act.db.Dao;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.Generics;
import org.osgl.util.S;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JFinalQuery<MODEL_TYPE> implements Dao.Query<MODEL_TYPE, JFinalQuery<MODEL_TYPE>> {

    protected Class<MODEL_TYPE> modelType;

    JFinalDao dao;
    String where;

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public JFinalQuery() {
        List<Type> typeParams = Generics.typeParamImplementations(getClass(), JFinalQuery.class);
        int sz = typeParams.size();
        if (sz > 1) {
            dao = $.cast(typeParams.get(1));
        }
        if (sz > 0) {
            modelType = $.cast(typeParams.get(0));
        }
    }

    public JFinalQuery(JFinalDao dao, Class<MODEL_TYPE> modelType) {
        this.modelType = modelType;
        this.dao = dao;
    }

    @Override
    public JFinalQuery<MODEL_TYPE> offset(int pos) {
        return this;
    }

    @Override
    public JFinalQuery<MODEL_TYPE> limit(int limit) {
        return this;
    }

    @Override
    public JFinalQuery<MODEL_TYPE> orderBy(String... fieldList) {
        return this;
    }

    @Override
    public MODEL_TYPE first() {
        throw E.unsupport();
    }

    @Override
    public Iterable<MODEL_TYPE> fetch() {
        throw E.unsupport();
    }

    @Override
    public long count() {
        throw E.unsupport();
    }

}
