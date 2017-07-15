package org.piaohao.act.jfinal.db;

import act.app.App;
import act.app.DbServiceManager;
import org.osgl.inject.BeanSpec;
import org.osgl.inject.GenericTypedBeanLoader;

import javax.enterprise.context.ApplicationScoped;
import java.lang.reflect.Type;
import java.util.List;

@ApplicationScoped
public class JFinalDaoLoader implements GenericTypedBeanLoader<JFinalDao> {

    private DbServiceManager dbServiceManager;
    public JFinalDaoLoader() {
        dbServiceManager = App.instance().dbServiceManager();
    }

    @Override
    public JFinalDao load(BeanSpec beanSpec) {
        List<Type> typeList = beanSpec.typeParams();
        int sz = typeList.size();
        if (sz > 1) {
            Class<?> modelType = BeanSpec.rawTypeOf(typeList.get(1));
            return (JFinalDao) dbServiceManager.dao(modelType);
        }
        return null;
    }
}
