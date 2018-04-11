package com.blackchopper.briefness;


import com.blackchopper.briefness.databinding.XmlBind;
import com.blackchopper.briefness.databinding.XmlViewInfo;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * author  : Black Chopper
 * e-mail  : 4884280@qq.com
 * github  : http://github.com/BlackChopper
 * project : Briefness
 */

public class JavaProxyInfo extends AbstractJavaProxyInfo {
    public JavaProxyInfo(Elements elementUtils, TypeElement classElement) {
        super(elementUtils, classElement);
    }

    @Override
    protected void generateLayoutCode(StringBuilder builder) {
        if (bindLayout.size() > 0)
            builder.append("host.setContentView(").append("R.layout." + bindLayout.get(0).layout).append(");\n");
    }

    @Override
    protected void generateBindFieldCode(StringBuilder builder, boolean isActivity) {
        if (bindLayout.size() > 0 & bindLayout.get(0).layout != null) {
            XmlProxyInfo proxyInfo = new XmlProxyInfo(bindLayout.get(0).layout);
            List<XmlViewInfo> infos = proxyInfo.getViewInfos();
            for (int i = 0; i < infos.size(); i++) {
                builder.append(infos.get(i).ID).append("=");
                if (isActivity)
                    builder.append("(" + infos.get(i).view + ")host.findViewById( R.id." + infos.get(i).ID + ");\n");
                else
                    builder.append("(" + infos.get(i).view + ")view.findViewById( R.id." + infos.get(i).ID + ");\n");
            }
        }
        for (int[] ids : bindView.keySet()) {
            switch (ids.length) {
                case 1:
                    generateVariableCode(ids, builder, isActivity);
                    break;
                default:
                    generateVariableCodes(ids, builder, isActivity);
                    break;
            }
        }
    }

    @Override
    protected void generateBindDataCode(StringBuilder builder) {
        if (bindLayout.size() == 0) return;
        XmlProxyInfo proxyInfo = new XmlProxyInfo(bindLayout.get(0).layout);
        List<XmlBind> binds = proxyInfo.getBinds();
        for (XmlBind bind : binds) {
            String bindclazz = bind.clazz.substring(bind.clazz.lastIndexOf(".") + 1);
            importBuilder.append("import " + bind.clazz).append(";\n");
            builder.append("   public void set" + bind.name.substring(0, 1).toUpperCase() + bind.name.substring(1) + "(" + bindclazz + " " + bind.name + "){\n");
            for (XmlViewInfo info : bind.list) {
                if (info.bind == null) continue;
                if (info.bind.endsWith(";")) {
                    String[] method = info.bind.split(";");
                    for (String s : method) {
                        if (s.contains(bind.name))
                            builder.append(info.ID).append(".").append(s).append(";\n");
                    }
                } else {
                    builder.append("ViewInjector.inject(").append(info.ID).append(",").append(info.bind).append(");\n");
                }
            }
            builder.append("    }");
        }
    }

    protected void generateFieldCode(StringBuilder builder) {

        if (bindLayout.size() > 0 & bindLayout.get(0).layout != null) {
            XmlProxyInfo proxyInfo = new XmlProxyInfo(bindLayout.get(0).layout);
            List<XmlViewInfo> infos = proxyInfo.getViewInfos();
            for (int i = 0; i < infos.size(); i++) {
                builder.append(infos.get(i).view).append(" ").append(infos.get(i).ID).append(";\n");
            }
        }
    }


    private void generateVariableCodes(int[] ids, StringBuilder builder, boolean isActivity) {
        try {
            VariableElement element = (VariableElement) bindView.get(ids);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append("host." + name).append(" = ");
            builder.append("new " + type + "{\n");
            for (int id : ids) {
                if (isActivity)
                    builder.append("(" + type.replace("[", "").replace("]", "") + ")(host.findViewById( " + id + ")),\n");
                else
                    builder.append("(" + type.replace("[", "").replace("]", "") + ")(view.findViewById( " + id + ")),\n");
            }
            builder.delete(builder.length() - 2, builder.length()).append("\n");
            builder.append("};\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void generateVariableCode(int[] ids, StringBuilder builder, boolean isActivity) {
        try {
            VariableElement element = (VariableElement) bindView.get(ids);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            type = type.substring(type.lastIndexOf(".") + 1);
            builder.append("host." + name).append(" = ").append("(").append(type).append(")");
            if (isActivity)
                builder.append("host.findViewById( " + ids[0] + " );\n");
            else
                builder.append("view.findViewById( " + ids[0] + " );\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}