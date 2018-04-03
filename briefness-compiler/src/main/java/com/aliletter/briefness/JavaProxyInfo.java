package com.aliletter.briefness;


import com.aliletter.briefness.databinding.JavaLayout;
import com.aliletter.briefness.databinding.XmlViewInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Author: aliletter
 * Github: http://github.com/aliletter
 * Data: 2017/9/15.
 */

public class JavaProxyInfo {
    public static final String PROXY = "Briefnessor";

    private String packageName;
    private String proxyClassName;
    private TypeElement typeElement;

    public Map<int[], Element> bindView = new LinkedHashMap<>();
    public List<JavaLayout> bindLayout = new ArrayList<>();

    public Map<int[], Element> bindClick = new LinkedHashMap<>();


    public JavaProxyInfo(Elements elementUtils, TypeElement classElement) {
        this.typeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = ClassValidator.getClassName(classElement, packageName);
        this.packageName = packageName;
        this.proxyClassName = className + "$$" + PROXY;
    }

    public String getProxyClassFullName() {
        return packageName + "." + proxyClassName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }


    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated code. Do not modify! \n");

        XmlProxyInfo proxyInfo1 = new XmlProxyInfo(bindLayout.get(0).layout);
        builder.append(proxyInfo1.binds.toString()).append("\n");


        builder.append("package ").append(packageName).append(";\n");
        builder.append("import com.aliletter.briefness.*;\n");
        builder.append("import android.view.View;\n");
        builder.append("import android.widget.*;\n");
        builder.append("import android.app.Activity;\n");
        builder.append("import java.util.ArrayList;\n\n");
        builder.append("import com.aliletter.briefness.ViewInjector;\n\n");

        builder.append("public class ").append(proxyClassName).append(" implements " + PROXY);
        builder.append("{\n");
        if (bindClick.size() > 0) {
            XmlProxyInfo proxyInfo = new XmlProxyInfo(bindLayout.get(0).layout);
            List<XmlViewInfo> infos = proxyInfo.viewInfos;
            for (int i = 0; i < infos.size(); i++) {
                builder.append(infos.get(i).view).append(" ").append(infos.get(i).ID).append(";\n");
            }
        }
        generateComplierCode(builder);
        builder.append("\n").append("}\n");
        return builder.toString();
    }

    private void generateComplierCode(StringBuilder builder) {
        if (bindClick.size() > 0) {
            XmlProxyInfo info = new XmlProxyInfo(bindLayout.get(0).layout);
        }
        builder.append("@Override\n ");
        builder.append("public void bind(final " + "Object" + " host, Object source ) {\n");
        builder.append("if ((source instanceof Activity)&&(source != null)) {\n");
        generateLayoutCode(builder, true);
        if (bindClick.size() > 0) {
            XmlProxyInfo proxyInfo = new XmlProxyInfo(bindLayout.get(0).layout);
            List<XmlViewInfo> infos = proxyInfo.viewInfos;
            for (int i = 0; i < infos.size(); i++) {
                builder.append(infos.get(i).ID).append("=");
                builder.append("(" + infos.get(i).view + ")(((Activity)source).findViewById( R.id." + infos.get(i).ID + "));\n");
            }
        }
        for (int[] ids : bindView.keySet()) {
            switch (ids.length) {
                case 1:
                    generateVariableCode(ids, builder, true);
                    break;
                default:
                    generateVariableCodes(ids, builder, true);
                    break;
            }
        }
        generateMethodCode(builder, true);
        builder.append("\n}else if((source instanceof View )&&(source != null)){\n");
        generateLayoutCode(builder, false);
        if (bindClick.size() > 0) {
            XmlProxyInfo proxyInfo = new XmlProxyInfo(bindLayout.get(0).layout);
            List<XmlViewInfo> infos = proxyInfo.viewInfos;
            for (int i = 0; i < infos.size(); i++) {
                builder.append(infos.get(i).ID).append("=");
                builder.append("(" + infos.get(i).view + ")(((View)source).findViewById( R.id." + infos.get(i).ID + "));\n");
            }
        }
        for (int[] ids : bindView.keySet()) {
            switch (ids.length) {
                case 1:
                    generateVariableCode(ids, builder, false);
                    break;
                default:
                    generateVariableCodes(ids, builder, false);
                    break;
            }
        }
        generateMethodCode(builder, false);
        builder.append("  \n}");


        builder.append("  }\n");
    }

    private void generateMethodCode(StringBuilder builder, boolean isActivity) {
        if (isActivity) {
            for (Map.Entry<int[], Element> entry : bindClick.entrySet()) {
                for (int id : entry.getKey()) {
                    builder.append("((android.app.Activity)source).findViewById(").append(id + ").setOnClickListener(new View.OnClickListener() {\n");
                    builder.append("@Override\n");
                    builder.append("public void onClick(View view) {\n");
                    builder.append("((" + typeElement.getQualifiedName() + ")host).").append(entry.getValue().getSimpleName()).append("(view);\n");
                    builder.append("}\n");
                    builder.append(" });\n");
                }
            }
        } else {
            for (Map.Entry<int[], Element> entry : bindClick.entrySet()) {
                for (int id : entry.getKey()) {
                    builder.append("((android.view.View)source).findViewById(").append(id + ").setOnClickListener(new View.OnClickListener() {\n");
                    builder.append("@Override\n");
                    builder.append("public void onClick(View view) {\n");
                    builder.append("((" + typeElement.getQualifiedName() + ")host).").append(entry.getValue().getSimpleName()).append("(view);\n");
                    builder.append("}\n");
                    builder.append(" });\n");
                }
            }
        }
    }

    private void generateVariableCodes(int[] ids, StringBuilder builder, boolean isActivity) {
        if (isActivity) {
            try {
                VariableElement element = (VariableElement) bindView.get(ids);
                String name = element.getSimpleName().toString();
                String type = element.asType().toString();
                builder.append("((" + typeElement.getQualifiedName() + ")host)." + name).append(" = ");
                builder.append("new " + type + "{\n");
                for (int id : ids) {
                    builder.append("(" + type.replace("[", "").replace("]", "") + ")(((android.app.Activity)source).findViewById( " + id + ")),\n");
                }
                builder.delete(builder.length() - 2, builder.length()).append("\n");
                builder.append("};\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                VariableElement element = (VariableElement) bindView.get(ids);

                String name = element.getSimpleName().toString();
                String type = element.asType().toString();
                builder.append("((" + typeElement.getQualifiedName() + ")host)." + name).append(" = ");
                builder.append("new " + type + "{\n");
                for (int id : ids) {
                    builder.append("(" + type.replace("[", "").replace("]", "") + ")(((android.view.View)source).findViewById( " + id + ")),\n");
                }
                builder.delete(builder.length() - 2, builder.length()).append("\n");
                builder.append("};\n");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void generateLayoutCode(StringBuilder builder, boolean isActivity) {

        if (bindLayout.size() > 0)
            builder.append("((" + typeElement.getQualifiedName() + ")host).setContentView(").append(bindLayout.get(0).id).append(");\n");

    }

    private void generateVariableCode(int[] ids, StringBuilder builder, boolean isActivity) {
        if (isActivity) {
            try {
                if (bindView.get(ids) == null) return;
                VariableElement element = (VariableElement) bindView.get(ids);
                String name = element.getSimpleName().toString();
                String type = element.asType().toString();
                builder.append("((" + typeElement.getQualifiedName() + ")host)." + name).append(" = ");
                builder.append("(" + type + ")(((android.app.Activity)source).findViewById( " + ids[0] + "));\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (bindView.get(ids) == null) return;
                VariableElement element = (VariableElement) bindView.get(ids);
                String name = element.getSimpleName().toString();
                String type = element.asType().toString();
                builder.append("((" + typeElement.getQualifiedName() + ")host)." + name).append(" = ");
                builder.append("(" + type + ")(((android.view.View)source).findViewById( " + ids[0] + "));\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}