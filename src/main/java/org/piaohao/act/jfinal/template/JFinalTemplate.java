package org.piaohao.act.jfinal.template;

import act.Act;
import act.view.TemplateBase;
import com.jfinal.template.FastStringWriter;
import com.jfinal.template.Template;
import org.osgl.$;
import org.osgl.http.H;
import org.osgl.util.E;

import java.io.Writer;
import java.util.Map;

public class JFinalTemplate extends TemplateBase {

    Template tmpl;

    JFinalTemplate(Template tmpl) {
        this.tmpl = $.notNull(tmpl);
    }

    @Override
    protected void merge(Map<String, Object> renderArgs, H.Response response) {
        if (Act.isDev()) {
            super.merge(renderArgs, response);
            return;
        }
        try {
            tmpl.render(renderArgs, response.writer());
        } catch (Exception e) {
            throw E.unexpected(e, "Error output freemarker template");
        }
    }

    @Override
    protected String render(Map<String, Object> renderArgs) {
//        Writer w = new StringWriter();
//        try {
//            tmpl.process(renderArgs, w);
//        } catch (ParseException e) {
//            throw new JFinalTemplateException(e);
//        } catch (TemplateException e) {
//            throw new JFinalTemplateException(e);
//        } catch (IOException e) {
//            throw E.ioException(e);
//        } catch (Exception e) {
//            throw E.unexpected(e);
//        }
//        return w.toString();
        Writer w = new FastStringWriter();
        tmpl.render(renderArgs, w);
        return w.toString();
    }
}
