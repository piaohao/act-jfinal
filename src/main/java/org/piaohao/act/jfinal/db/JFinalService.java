package org.piaohao.act.jfinal.db;

import act.app.App;
import act.db.Dao;
import act.db.sql.DataSourceConfig;
import act.db.sql.SqlDbService;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.xiaoleilu.hutool.util.ClassUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import org.osgl.$;
import org.osgl.util.E;
import org.osgl.util.S;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public final class JFinalService extends SqlDbService {
    ActiveRecordPlugin arp;
    private DruidPlugin dp;
    public String mappingKitClassName;

    public JFinalService(final String dbId, final App app, final Map<String, String> config) {
        super(dbId, app, config);
        dp = new DruidPlugin(config.get("url"), config.get("username"), config.get("password"));
        dp.setTestOnBorrow(true);
        dp.setTestWhileIdle(true);
        dp.setTestOnReturn(true);
        dp.addFilter(new StatFilter());
        WallFilter wall = new WallFilter();
        wall.setDbType("mysql");
        dp.addFilter(wall);
        arp = new ActiveRecordPlugin(dp);
        mappingKitClassName = config.get("mappingKitClass");
        if (S.notBlank(mappingKitClassName)) {
            app.eventBus().trigger(new FoundMappingKitConfiguration(this));
        } else {
            start();
        }
    }

    public void start() {
        dp.start();
        arp.start();
    }

    @Override
    protected boolean supportDdl() {
        return true;
    }

    @Override
    protected DataSource createDataSource() {
        return dp.getDataSource();
    }

    @Override
    protected void releaseResources() {
        if (arp != null) {
            arp.stop();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <DAO extends Dao> DAO defaultDao(Class<?> modelType) {
        Class<?> idType = findModelIdTypeByAnnotation(modelType, Id.class);
        while (Object.class != modelType && null != modelType) {
            Method[] methods = modelType.getDeclaredMethods();
            boolean finded = false;
            for (Method method : methods) {
                if (method.isAnnotationPresent(Id.class)) {
                    idType = method.getReturnType();
                    finded = true;
                    break;
                }
            }
            if (finded) {
                break;
            }
            modelType = modelType.getSuperclass();
        }
        E.illegalArgumentIf(null == idType, "Cannot find out Dao for model type[%s]: unable to identify the ID type", modelType);
        return $.cast(new JFinalDao(idType, modelType, this));
    }

    @Override
    public <DAO extends Dao> DAO newDaoInstance(Class<DAO> daoType) {
        E.illegalArgumentIf(!JFinalDao.class.isAssignableFrom(daoType), "expected JFinalDao, found: %s", daoType);
        JFinalDao dao = $.cast(app().getInstance(daoType));
        dao.arp(this.arp());
        return (DAO) dao;
    }

    @Override
    public Class<? extends Annotation> entityAnnotationType() {
        return Entity.class;
    }

    public ActiveRecordPlugin arp() {
        return arp;
    }

}
