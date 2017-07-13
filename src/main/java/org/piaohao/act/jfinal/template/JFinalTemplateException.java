package org.piaohao.act.jfinal.template;

import act.Act;
import act.app.SourceInfo;
import com.jfinal.template.TemplateException;
import com.jfinal.template.stat.ParseException;
import org.osgl.util.C;
import org.osgl.util.E;

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
        sourceInfo = getJavaSourceInfo(t.getCause());
        if (t instanceof ParseException) {
            templateInfo = new JFinalSourceInfo((ParseException) t);
        } else if (t instanceof TemplateException) {
            templateInfo = new JFinalSourceInfo((TemplateException) t);
        } else {
            throw E.unexpected("Unknown exception type: %s", t.getClass());
        }
    }

    @Override
    public String errorMessage() {
        Throwable t = getCauseOrThis();
        boolean isParseException = t instanceof ParseException;
        boolean isTemplateException = t instanceof TemplateException;
        if (isParseException || isTemplateException) {
            return t.getMessage();
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
        return s.contains("com.jfinal.template.expr.ast.MethodInfo.invoke");
    }

    private static class JFinalSourceInfo extends SourceInfo.Base {
        String tag1 = "\nTemplate: \"";
        String tag2 = "\". Line: ";

        JFinalSourceInfo(ParseException e) {
            parse(e.getMessage());
        }

        JFinalSourceInfo(TemplateException e) {
            parse(e.getMessage());
        }

        private void parse(String message) {
            int fileNameIndexStart = message.indexOf(tag1);
            int rowIndexStart = message.indexOf(tag2);
            if (fileNameIndexStart > 0 && rowIndexStart > 0) {
                fileName = message.substring(fileNameIndexStart + tag1.length(), rowIndexStart);
                lineNumber = Integer.parseInt(message.substring(rowIndexStart + tag2.length()));
                lines = readTemplateSource(fileName);
            }
        }

        private static List<String> readTemplateSource(String template) {
            JFinalView view = (JFinalView) Act.viewManager().view(JFinalView.ID);
            return view.loadContent(template);
        }
    }

}
