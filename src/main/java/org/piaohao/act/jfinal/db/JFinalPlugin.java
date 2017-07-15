package org.piaohao.act.jfinal.db;

import act.app.App;
import act.db.DbPlugin;
import act.db.DbService;
import act.inject.param.ParamValueLoaderService;

import java.util.Map;

public class JFinalPlugin extends DbPlugin {
    @Override
    public DbService initDbService(String id, App app, Map<String, String> conf) {
        return new JFinalService(id, app, conf);
    }
}
