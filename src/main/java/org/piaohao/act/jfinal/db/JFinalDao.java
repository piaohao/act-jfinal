package org.piaohao.act.jfinal.db;

import act.app.DbServiceManager;
import act.db.DB;
import act.db.DaoBase;
import act.db.DbService;
import act.inject.param.NoBind;
import act.util.General;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import org.osgl.$;
import org.osgl.logging.L;
import org.osgl.logging.Logger;
import org.osgl.util.C;
import org.osgl.util.E;

import javax.persistence.Id;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static act.Act.app;

@General
@NoBind
@SuppressWarnings("uncheck")
public class JFinalDao<ID_TYPE, MODEL_TYPE extends Model> extends DaoBase<ID_TYPE, MODEL_TYPE, JFinalQuery<MODEL_TYPE>> {

    private static final Logger logger = L.get(JFinalDao.class);

    private Model<MODEL_TYPE> dao;
    private ActiveRecordPlugin arp;
    private volatile DataSource ds;
    private String tableName;
    private Field idField = null;

    JFinalDao(JFinalService service) {
        init(modelType());
        this.arp(service.arp());
    }

    JFinalDao(Class<ID_TYPE> idType, Class<MODEL_TYPE> modelType, JFinalService service) {
        super(idType, modelType);
        init(modelType);
        this.arp(service.arp());
        this.ds = service.dataSource();
        this.dao = $.newInstance(modelType);
    }

    public JFinalDao(Class<ID_TYPE> id_type, Class<MODEL_TYPE> modelType) {
        super(id_type, modelType);
        init(modelType);
    }

    public JFinalDao() {
        init(modelType());
    }

    public void arp(ActiveRecordPlugin arp) {
        this.arp = $.notNull(arp);
        this.tableName = arp.getTableMap().get(modelType()).getName();
    }

    public void modelType(Class<?> type) {
        this.modelType = $.cast(type);
    }

    @Override
    protected void releaseResources() {
        if (arp != null) {
            arp.stop();
        }
    }

    private void init(Class<MODEL_TYPE> modelType) {
        for (Field f : modelType.getDeclaredFields()) {
            Id idAnno = f.getAnnotation(Id.class);
            if (null != idAnno) {
                idField = f;
                f.setAccessible(true);
                break;
            }
        }
        if (null != arp) {
            this.tableName = arp.getTableMap().get(modelType).getName();
        }
    }

    private JFinalService getService(String dbId, DbServiceManager mgr) {
        DbService svc = mgr.dbService(dbId);
        E.invalidConfigurationIf(null == svc, "Cannot find db service by id: %s", dbId);
        E.invalidConfigurationIf(!(svc instanceof JFinalService), "The db service[%s|%s] is not ebean service", dbId, svc.getClass());
        return $.cast(svc);
    }

    public ActiveRecordPlugin arp() {
        if (null != arp) {
            return arp;
        }
        synchronized (this) {
            if (null == arp) {
                DB db = modelType().getAnnotation(DB.class);
                String dbId = null == db ? DbServiceManager.DEFAULT : db.value();
                JFinalService dbService = getService(dbId, app().dbServiceManager());
                E.NPE(dbService);
                arp = dbService.arp();
            }
        }
        return arp;
    }

    public DataSource ds() {
        if (null != ds) {
            return ds;
        }
        synchronized (this) {
            if (null == ds) {
                DB db = modelType().getAnnotation(DB.class);
                String dbId = null == db ? DbServiceManager.DEFAULT : db.value();
                JFinalService dbService = getService(dbId, app().dbServiceManager());
                E.NPE(dbService);
                ds = dbService.dataSource();
            }
        }
        return ds;
    }

    private String selectStr() {
        return "select * from " + tableName;
    }

    @Override
    public MODEL_TYPE findById(ID_TYPE id) {
        return dao.findById(id);
    }

    @Override
    public Iterable<MODEL_TYPE> findBy(String fields, Object... values) throws IllegalArgumentException {
        JFinalQuery<MODEL_TYPE> q = q(fields, values);
        return dao.find(selectStr() + q.getWhere());
    }

    @Override
    public Iterable<MODEL_TYPE> findByIdList(Collection<ID_TYPE> idList) {
        dao.findById();
        StringBuilder sqlBuilder = new StringBuilder(" where ").append(idField.getName()).append(" in (");
        int i = 0;
        for (ID_TYPE id : idList) {
            if (i++ > 0) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append("'").append(id).append("'");
        }
        sqlBuilder.append(")");
        return dao.find(selectStr() + sqlBuilder.toString());
    }

    @Override
    public MODEL_TYPE findOneBy(String fields, Object... values) throws IllegalArgumentException {
        JFinalQuery<MODEL_TYPE> q = q(fields, values);
        return q.first();
    }

    @Override
    public Iterable<MODEL_TYPE> findAll() {
        return dao.find(selectStr());
    }

    @Override
    public List<MODEL_TYPE> findAllAsList() {
        return C.list(findAll());
    }

    @Override
    public MODEL_TYPE reload(MODEL_TYPE entity) {
        return entity;
    }

    @Override
    public ID_TYPE getId(MODEL_TYPE entity) {
        if (entity instanceof Model) {
            return (ID_TYPE) arp().getTableMap().get(entity.getClass()).getPrimaryKey()[0];
        } else if (null != idField) {
            try {
                return (ID_TYPE) idField.get(entity);
            } catch (IllegalAccessException e) {
                throw E.unexpected(e);
            }
        } else {
            return null;
        }
    }

    @Override
    public long count() {
        return Db.queryLong("select count(*) from " + tableName);
    }

    @Override
    public long countBy(String fields, Object... values) throws IllegalArgumentException {
        JFinalQuery<MODEL_TYPE> q = q(fields, values);
        return Db.queryLong("select count(*) from " + tableName + q.getWhere());
    }

    @Override
    public MODEL_TYPE save(MODEL_TYPE entity) {
        entity.save();
        return entity;
    }

    public MODEL_TYPE saveTx(MODEL_TYPE entity) {
        Db.tx(() -> entity.save());
        return entity;
    }

    @Override
    public List<MODEL_TYPE> save(Iterable<MODEL_TYPE> iterable) {
        List<MODEL_TYPE> list = C.list(iterable);
        if (list.isEmpty()) {
            return list;
        }
        Db.tx(() -> {
            Db.batchSave(list, list.size());
            return true;
        });
        return list;
    }

    @Override
    public void save(MODEL_TYPE entity, String fields, Object... values) throws IllegalArgumentException {
        int len = values.length;
        E.illegalArgumentIf(len == 0, "no values supplied");
        String[] sa = fields.split("[,;:]+");
        E.illegalArgumentIf(sa.length != len, "The number of values does not match the number of fields");
        for (int i = 0; i < len; i++) {
            entity.set(sa[i], values[i]);
        }
        entity.update();
    }

    @Override
    public void delete(MODEL_TYPE entity) {
        entity.delete();
    }

    @Override
    public void delete(JFinalQuery<MODEL_TYPE> query) {
    }

    @Override
    public void deleteById(ID_TYPE id) {
        dao.deleteById(id);
    }

    @Override
    public void deleteBy(String fields, Object... values) throws IllegalArgumentException {
        delete(q(fields, values));
    }

    @Override
    public void deleteAll() {
        Db.update("delete from " + tableName);
    }

    @Override
    public void drop() {
        Db.update("delete from " + tableName);
    }

    @Override
    public JFinalQuery<MODEL_TYPE> q() {
        return new JFinalQuery<MODEL_TYPE>(this, modelType());
    }

    @Override
    public JFinalQuery<MODEL_TYPE> createQuery() {
        return q();
    }

    public static final String ID = "_id";

    @Override
    public JFinalQuery<MODEL_TYPE> q(String keys, Object... values) {
        int len = values.length;
        E.illegalArgumentIf(len == 0, "no values supplied");
        String[] sa = keys.split("[,;:]+");
        E.illegalArgumentIf(sa.length != len, "The number of values does not match the number of fields");
        JFinalQuery<MODEL_TYPE> q = q();
        StringBuilder sqlBuilder = new StringBuilder(" where ");
        for (int i = 0; i < len; ++i) {
            sqlBuilder.append(sa[i]).append("='").append(values[i]).append("'");
            if (i > 0) {
                sqlBuilder.append(" and");
            }
        }
        q.setWhere(sqlBuilder.toString());
        return q;
    }

    @Override
    public JFinalQuery<MODEL_TYPE> createQuery(String s, Object... objects) {
        return q(s, objects);
    }
}
