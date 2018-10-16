package com.hacknife.briefness;


import com.hacknife.briefness.databinding.XmlBind;
import com.hacknife.briefness.databinding.XmlViewInfo;
import com.hacknife.briefness.util.ClassUtil;
import com.hacknife.briefness.util.StringUtil;
import com.hacknife.briefness.util.ViewCollection;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * author  : Hacknife
 * e-mail  : 4884280@qq.com
 * github  : http://github.com/hacknife
 * project : Briefness
 */

public class JavaInfo extends AbsJavaInfo {


    public JavaInfo(Elements elementUtils, TypeElement classElement) {
        super(elementUtils, classElement);
    }

    @Override
    protected void generateSetDataCode(StringBuilder builder) {
        if (bindLayout.size() > 0) {
            XmlInfo proxyInfo = new XmlInfo(ClassUtil.findLayoutById(typeElement.getQualifiedName().toString(), modulePath), modulePath);
            List<XmlViewInfo> infos = proxyInfo.getViewInfos();
            for (int i = 0; i < infos.size(); i++) {
                String viewName = infos.get(i).view;
                if (!importBuilder.toString().contains(packages + ".BriefnessInjector"))
                    importBuilder.append("import " + packages + ".BriefnessInjector;\n");
                builder.append("    public void set").append(StringUtil.toUpperCase(infos.get(i).ID)).append("(Object obj) {\n")
                        .append("        BriefnessInjector.injector(").append(infos.get(i).ID).append(",obj)").append(";\n").append("    }\n\n");
                builder.append("    public ").append(viewName).append(" get").append(StringUtil.toUpperCase(infos.get(i).ID)).append("() {\n")
                        .append("        return this.").append(infos.get(i).ID).append(";\n    }\n\n");
            }
        }
    }

    protected void generateFieldCode(StringBuilder builder) {

        if (bindLayout.size() > 0) {
            XmlInfo proxyInfo = new XmlInfo(ClassUtil.findLayoutById(typeElement.getQualifiedName().toString(), modulePath), modulePath);
            List<XmlViewInfo> infos = proxyInfo.getViewInfos();
            for (int i = 0; i < infos.size(); i++) {
                String viewName = infos.get(i).view;
                if (infos.get(i).view.contains(".")) {
                    if (!importBuilder.toString().contains(viewName))
                        importBuilder.append("import ").append(viewName).append(";\n");
                    viewName = viewName.substring(viewName.lastIndexOf(".") + 1);
                } else {
                    if (!importBuilder.toString().contains(ViewCollection.getFullNameByName(viewName)))
                        importBuilder.append("import ").append(ViewCollection.getFullNameByName(viewName)).append(";\n");
                }
                builder.append("    ").append(viewName).append(" ").append(infos.get(i).ID).append(";\n");
            }
        }
    }

    @Override
    protected void generateClearData(StringBuilder builder) {
        if (bindLayout.size() > 0) {
            XmlInfo proxyInfo = new XmlInfo(ClassUtil.findLayoutById(typeElement.getQualifiedName().toString(), modulePath), modulePath);
            List<XmlViewInfo> infos = proxyInfo.getViewInfos();
            List<XmlBind> binds = proxyInfo.getBinds();

            builder.append("    @Override\n" +
                    "    public void clear() {\n");

            for (XmlViewInfo info : infos) {
                builder.append("        this.").append(info.ID).append(" = null;\n");
            }

            builder.append("    }\n");

            builder.append("\n    @Override\n" +
                    "    public void clearAll() {\n");
            for (XmlViewInfo info : infos) {
                builder.append("        this.").append(info.ID).append(" = null;\n");
            }
            for (XmlBind bind : binds) {
                builder.append("        this.").append(bind.name).append(" = null;\n");
            }
            builder.append("    }\n");
        }
    }

    @Override
    protected void generateBindDataCode(StringBuilder builder) {
        if (bindLayout.size() == 0) return;
        XmlInfo proxyInfo = new XmlInfo(ClassUtil.findLayoutById(typeElement.getQualifiedName().toString(), modulePath), modulePath);
        List<XmlBind> binds = proxyInfo.getBinds();
        for (XmlBind bind : binds) {
            String bindclazz = bind.clazz.substring(bind.clazz.lastIndexOf(".") + 1);
            boolean special = false;
            if (bind.clazz.equalsIgnoreCase("android.os.Bundle") | bind.clazz.equalsIgnoreCase("java.util.Map")) {
                special = true;
            }
            if (!importBuilder.toString().contains(bind.clazz))
                importBuilder.append("import " + bind.clazz).append(";\n");
            fieldBuilder.append("    ").append(bindclazz).append(" ").append(bind.name).append(";\n");
            builder.append("\n    public void set" + bind.name.substring(0, 1).toUpperCase() + bind.name.substring(1) + "(" + bindclazz + " " + bind.name + ") {\n");
            builder.append("        if (" + bind.name + " == null) return;\n");
            builder.append("        this.").append(bind.name).append(" = ").append(bind.name).append(";\n");
            for (XmlViewInfo info : bind.list) {
                if (info.bind == null) continue;
                if (info.bind.endsWith(";")) {
                    String[] method = info.bind.split(";");
                    String[] methodSource = info.bindSource.split(";");
                    for (int i = 0; i < method.length; i++) {
                        if (method[i].contains(bind.name)) {
                            if (methodSource[i].endsWith(")")) {
                                if (special) {
                                    builder.append("        ").append(info.ID).append(".").append(XmlInfo.specialBind2String(methodSource[i])).append(";\n");
                                } else {
                                    builder.append("        ").append(info.ID).append(".").append(method[i]).append(";\n");
                                }
                            } else {
                                if (!importBuilder.toString().contains(packages + ".BriefnessInjector"))
                                    importBuilder.append("import " + packages + ".BriefnessInjector;\n");
                                if (special) {
                                    builder.append("        BriefnessInjector.injector(").append(info.ID).append(",").append(XmlInfo.specialBind2String(methodSource[i])).append(");\n");
                                } else {
                                    builder.append("        BriefnessInjector.injector(").append(info.ID).append(",").append(method[i]).append(");\n");
                                }
                            }
                        }

                    }
                } else {
                    if (!importBuilder.toString().contains(packages + ".BriefnessInjector"))
                        importBuilder.append("import " + packages + ".BriefnessInjector;\n");
                    if (special) {
                        builder.append("        BriefnessInjector.injector(").append(info.ID).append(",").append(XmlInfo.specialBind2String(info.bindSource)).append(");\n");
                    } else {

                        String[] split = info.bind.split("get");
                        System.out.print(info.bind + "\n");
                        boolean isList = split.length == 3;
                        if (isList) {
                            String index = split[2].substring(split[2].indexOf("(") + 1, split[2].indexOf(")"));
                            String list = split[0] + "get" + split[1].substring(0, split[1].length() - 1);
                            builder.append("        if (" + list + " != null && " + list + ".size() > " + index + ") {\n");
                            builder.append("            BriefnessInjector.injector(").append(info.ID).append(", ").append(info.bind).append(");\n");
                            builder.append("        }\n");
                        } else {
                            builder.append("        BriefnessInjector.injector(").append(info.ID).append(", ").append(info.bind).append(");\n");
                        }

                    }
                }
            }
            builder.append("    }\n");
        }
    }

    @Override
    protected void generateLayoutCode(StringBuilder builder) {
        if (bindLayout.size() > 0) {
            importBuilder.append("import com.hacknife.briefness.Utils;\n");
            builder.append("        if (!Utils.contentViewExist(host)) {\n");
            builder.append("            host.setContentView(").append("R.layout." + ClassUtil.findLayoutById(typeElement.getQualifiedName().toString(), modulePath)).append(");\n");
            builder.append("        }\n");
        }

    }


    @Override
    protected void generateBindFieldCode(StringBuilder builder, boolean isActivity) {
        if (bindLayout.size() > 0) {
            XmlInfo proxyInfo = new XmlInfo(ClassUtil.findLayoutById(typeElement.getQualifiedName().toString(), modulePath), modulePath);
            List<XmlViewInfo> infos = proxyInfo.getViewInfos();
            for (int i = 0; i < infos.size(); i++) {
                builder.append("        ").append(infos.get(i).ID).append(" = ");
                String viewName = infos.get(i).view;
                if (infos.get(i).view.contains(".")) {
                    viewName = viewName.substring(viewName.lastIndexOf(".") + 1);
                }
                if (isActivity)
                    builder.append("(" + viewName + ") host.findViewById(R.id." + infos.get(i).ID + ");\n");
                else
                    builder.append("(" + viewName + ") view.findViewById(R.id." + infos.get(i).ID + ");\n");
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

    private void generateVariableCode(int[] ids, StringBuilder builder, boolean isActivity) {
        try {
            VariableElement element = (VariableElement) bindView.get(ids);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            type = type.substring(type.lastIndexOf(".") + 1);
            builder.append("        host." + name).append(" = ").append("(").append(type).append(")");
            if (isActivity)
                builder.append(" host.findViewById(" + ids[0] + ");\n");
            else
                builder.append(" view.findViewById(" + ids[0] + ");\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateVariableCodes(int[] ids, StringBuilder builder, boolean isActivity) {
        try {
            VariableElement element = (VariableElement) bindView.get(ids);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            if (type.contains(".")) {
                type = type.substring(type.lastIndexOf(".") + 1);
            }
            builder.append("        host." + name).append(" = ");
            builder.append("new " + type + "{\n");
            for (int id : ids) {
                if (isActivity)
                    builder.append("                (" + type.replace("[", "").replace("]", "") + ") (host.findViewById(" + id + ")),\n");
                else
                    builder.append("                (" + type.replace("[", "").replace("]", "") + ") (view.findViewById(" + id + ")),\n");
            }
            builder.delete(builder.length() - 2, builder.length()).append("\n");
            builder.append("        };\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
