package org.piaohao.act.jfinal.db;

import org.osgl.inject.Module;

@SuppressWarnings("unused")
public class JFinalModule extends Module {

    @Override
    protected void configure() {
        registerGenericTypedBeanLoader(JFinalDao.class, new JFinalDaoLoader());
    }

}
