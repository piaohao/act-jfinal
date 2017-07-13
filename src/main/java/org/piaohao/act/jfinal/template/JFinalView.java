package org.piaohao.act.jfinal.template;

import act.app.App;
import act.util.ActContext;
import act.view.Template;
import act.view.View;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;
import com.jfinal.template.FileStringSource;
import com.xiaoleilu.hutool.util.StrUtil;
import org.osgl.util.C;
import org.osgl.util.E;

import java.io.IOException;
import java.util.List;

public class JFinalView extends View {

    public static final String ID = "jfinal";

    private Engine engine;

    @Override
    public String name() {
        return ID;
    }

    @Override
    protected Template loadTemplate(String resourcePath, ActContext context) {
        com.jfinal.template.Template jfinalTemplate = null;
        try {
            jfinalTemplate = engine.getTemplate(ID + resourcePath);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                if (e.getMessage().startsWith("File not found : ")) {
                    return null;
                }
            }
        }
        return new JFinalTemplate(jfinalTemplate);
    }

    @Override
    protected void init(final App app) {
        engine = new Engine();
        engine.setBaseTemplatePath(PathKit.getRootClassPath());
    }

    public List<String> loadContent(String template) {
        try {
            FileStringSource fileStringSource = new FileStringSource(engine.getBaseTemplatePath(), template, "UTF-8");
            return StrUtil.split(fileStringSource.getContent(), '\n');
        } catch (Exception e) {
            throw E.unexpected(e);
        }
    }
}
