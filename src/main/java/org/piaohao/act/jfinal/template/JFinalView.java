package org.piaohao.act.jfinal.template;

import act.app.App;
import act.util.ActContext;
import act.view.Template;
import act.view.View;
import com.jfinal.kit.PathKit;
import com.jfinal.template.Engine;

public class JFinalView extends View {

    public static final String ID = "jfinal";

    private Engine engine;
    private String suffix;

    @Override
    public String name() {
        return ID;
    }

    @Override
    protected Template loadTemplate(String resourcePath, ActContext context) {
        com.jfinal.template.Template jfinalTemplate = engine.getTemplate(ID + resourcePath);
        return new JFinalTemplate(jfinalTemplate);
    }

    @Override
    protected void init(final App app) {
//        conf = new Configuration(Configuration.VERSION_2_3_23);
//        conf.setDefaultEncoding("UTF-8");
//        conf.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//        conf.setClassLoaderForTemplateLoading(app.classLoader(), templateHome());
//        suffix = app.config().get("view.freemarker.suffix");
//        if (null == suffix) {
//            suffix = ".ftl";
//        } else {
//            suffix = suffix.startsWith(".") ? suffix : S.concat(".", suffix);
//        }
        engine = new Engine();
        engine.setBaseTemplatePath(PathKit.getRootClassPath());
    }

//    public List<String> loadContent(String template) {
//        TemplateLoader loader = conf.getTemplateLoader();
//        try {
//            Method lookup = TemplateCache.class.getDeclaredMethod("lookupTemplate", String.class, Locale.class, Object.class);
//            lookup.setAccessible(true);
//            Field cache = Configuration.class.getDeclaredField("cache");
//            cache.setAccessible(true);
//            TemplateLookupResult result = $.invokeVirtual(cache.get(conf), lookup, template, Locale.getDefault(), null);
//            Method templateSource = TemplateLookupResult.class.getDeclaredMethod("getTemplateSource");
//            templateSource.setAccessible(true);
//            Reader reader = loader.getReader($.invokeVirtual(result, templateSource), conf.getEncoding(conf.getLocale()));
//            return IO.readLines(reader);
//        } catch (IOException e1) {
//            throw E.ioException(e1);
//        } catch (Exception e) {
//            throw E.unexpected(e);
//        }
//    }

}
