package org.piaohao.act.jfinal.template;

import com.jfinal.template.TemplateException;
import com.jfinal.template.stat.ParseException;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.E;

import java.lang.reflect.Method;
import java.util.List;

public class JFinalTemplateException extends act.view.TemplateException {

    public JFinalTemplateException(ParseException t) {
        super(t);
    }

    public JFinalTemplateException(TemplateException t) {
        super(t);
    }

    @Override
    protected void populateSourceInfo(Throwable t) {
//        sourceInfo = getJavaSourceInfo(t.getCause());
//        if (t instanceof ParseException) {
//            templateInfo = new FreeMarkerSourceInfo((ParseException) t);
//        } else if (t instanceof freemarker.template.TemplateException) {
//            templateInfo = new FreeMarkerSourceInfo((freemarker.template.TemplateException) t);
//        } else {
//            throw E.unexpected("Unknown exception type: %s", t.getClass());
//        }
    }

    @Override
    public String errorMessage() {
        Throwable t = getCauseOrThis();
        boolean isParseException = t instanceof ParseException;
        boolean isTemplateException = t instanceof TemplateException;
        if (isParseException || isTemplateException) {
            try {
                Method m;
                if (isParseException) {
                    m = ParseException.class.getDeclaredMethod("getDescription");
                } else {
                    m = TemplateException.class.getDeclaredMethod("getDescription");
                }
                m.setAccessible(true);
                return $.invokeVirtual(t, m);
            } catch (NoSuchMethodException e) {
                throw E.unexpected(e);
            }
        }
        return t.toString();
    }

    @Override
    public List<String> stackTrace() {
        Throwable t = getCause();
        if (t instanceof ParseException) {
            return C.list();
        }
        return super.stackTrace();
    }

    @Override
    protected boolean isTemplateEngineInvokeLine(String s) {
        //return s.contains("freemarker.ext.beans.BeansWrapper.invokeMethod");
        return false;
    }

}
