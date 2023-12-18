/*
 * Copyright 2017 Guillaume Bailleul.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.gbmb.xemph.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.javapoet.*;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * @goal genNamespaces
 * @phase generate-sources
 * @description generate the java source code of namespaces
 */
public class NamespaceGeneratorMojo extends AbstractMojo {

    private Log log;

    /**
     * @parameter property="project"
     * @required
     */
    protected MavenProject project;

    /**
     * @parameter property="project.build.directory/generated-sources/namespaces"
     * @required
     */
    protected File outputJavaDirectory;

    /**
     * @parameter property="project.basedir/namespaces"
     * @required
     */
    protected File definitionDirectory;

    /**
     * @parameter
     * @required
     */
    protected String packageName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        loadConfiguration();
        // list namespace definitions
        Collection<File> nsDefinitions = FileUtils.listFiles(definitionDirectory,new String [] {"yaml"},false);
        try {
            for (File file: nsDefinitions) {
                generate(file);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed generation",e);
        }
    }

    private void loadConfiguration () throws MojoExecutionException {
        log = getLog ();
        // generated source dir
        if (!outputJavaDirectory.exists()) {
            if (!outputJavaDirectory.mkdirs()) {
                throw new MojoExecutionException("Cannot create generated source directory "+outputJavaDirectory.getName());
            } else {
                log.info("Created target directory "+outputJavaDirectory.getPath());
            }
        }
        if (this.project != null) {
            this.project.addCompileSourceRoot(this.outputJavaDirectory.getAbsolutePath());
        }
        // namespaces definition
        if (!definitionDirectory.exists()) {
            log.error("Namespaces definition directory does not exist: "+definitionDirectory.getPath());
            throw new MojoExecutionException("Namespace definition not found");
        }

    }


    private void generate (File file) throws IOException, MojoExecutionException {
        log.warn("Generating: "+file.getPath());
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Namespace ns = mapper.readValue(file,Namespace.class);

        TypeSpec.Builder clsBuilder = TypeSpec.classBuilder(ns.getClassname());
        clsBuilder.addModifiers(Modifier.PUBLIC);
        clsBuilder.superclass(ClassName.get("net.gbmb.xemph","Namespace"));
        clsBuilder.addField(
                FieldSpec.builder(String.class, "DEFAULT_PREFIX",
                                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("\"" + ns.getDefaultPrefix() + "\"").build());
        clsBuilder.addMethod(MethodSpec.methodBuilder("getDefaultPrefix")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addAnnotation(Override.class)
                .addStatement("return DEFAULT_PREFIX").build());
        clsBuilder.addField(
                FieldSpec.builder(String.class, "NAMESPACE_URI",
                                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("\"" + ns.getUri() + "\"").build());
        clsBuilder.addMethod(MethodSpec.methodBuilder("getNamespaceURI")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addAnnotation(Override.class)
                .addStatement("return NAMESPACE_URI").build());
        MethodSpec.Builder getPropertyTypeBuilder = MethodSpec.methodBuilder("getPropertyType")
                .addModifiers(Modifier.PUBLIC)
                .returns(Class.class)
                .addAnnotation(Override.class)
                .addParameter(String.class,"propertyName")
                .addStatement("if (propertyName==null) return null");
        for (Property property:ns.getProperties()) {
            // create field
            clsBuilder.addField(
                    FieldSpec.builder(ClassName.get("net.gbmb.xemph","Name"), getUpperPropertyName(property.getName()),
                                    Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("new Name(NAMESPACE_URI,\""+property.getName()+"\")").build());
            // type
            String ifStmt = String.format("else if (%s.getLocalName().equals(propertyName)) return $T.class",getUpperPropertyName(property.getName()));
            getPropertyTypeBuilder.addStatement(ifStmt,ClassName.get("net.gbmb.xemph.values",getTypeClass(property.getType())));
        }

        getPropertyTypeBuilder.addStatement("else return null");
        clsBuilder.addMethod(getPropertyTypeBuilder.build());

        for (Property property:ns.getProperties()) {
            generateGetter(clsBuilder,property);
            generateContains(clsBuilder,property);
            generateSetter(clsBuilder,property);
        }

        TypeSpec cls = clsBuilder.build();
        JavaFile jf = JavaFile.builder(packageName,cls)
                .indent("    ")
                .build();
        jf.writeToFile(new File(this.outputJavaDirectory.getAbsolutePath()));

    }

    private void generateGetter (TypeSpec.Builder cls,Property property) throws MojoExecutionException {
        MethodSpec.Builder getter = MethodSpec.methodBuilder("get"+getPropertyNameForMethod(property.getName()))
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(ClassName.get("net.gbmb.xemph.values",getTypeClass(property.getType())))
                .addParameter(ClassName.get("net.gbmb.xemph","Packet"),"packet")
                .addStatement("return ("+getTypeClass(property.getType())+")packet.getValue("+getUpperPropertyName(property.getName())+")");
        cls.addMethod(getter.build());
    }

    private void generateContains (TypeSpec.Builder cls,Property property) throws MojoExecutionException {
        MethodSpec.Builder contains = MethodSpec.methodBuilder("contains"+getPropertyNameForMethod(property.getName()))
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .returns(boolean.class)
                .addParameter(ClassName.get("net.gbmb.xemph","Packet"),"packet")
                .addStatement("return packet.contains("+getUpperPropertyName(property.getName())+")");
        cls.addMethod(contains.build());
    }

    private void generateSetter (TypeSpec.Builder cls,Property property) throws MojoExecutionException {
        String propertyClass = getTypeClass(property.getType());
        MethodSpec.Builder setter = MethodSpec.methodBuilder("set"+getPropertyNameForMethod(property.getName()))
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .addParameter(ClassName.get("net.gbmb.xemph","Packet"),"packet")
                .addParameter(ClassName.get("net.gbmb.xemph.values",propertyClass),"value")
                .addStatement("packet.addProperty("+getUpperPropertyName(property.getName())+",value)");
        cls.addMethod(setter.build());
        if (propertyClass.equals("SimpleValue")) {
            generateSetterFromString(cls,property);
        }
        // generate string setter for array
        if (propertyClass.equals("UnorderedArray") || propertyClass.equals("OrderedArray")) {
            generateArraySetterWithOne(cls,property);
        }
    }

    private void generateSetterFromString(TypeSpec.Builder cls, Property property) throws MojoExecutionException {
        MethodSpec.Builder setter = MethodSpec.methodBuilder("set"+getPropertyNameForMethod(property.getName()))
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .addParameter(ClassName.get("net.gbmb.xemph","Packet"),"packet")
                .addParameter(String.class,"value")
                .addStatement("SimpleValue simple = SimpleValue.parse(value)")
                .addStatement("packet.addProperty("+getUpperPropertyName(property.getName())+",simple)");
        cls.addMethod(setter.build());
    }

    private void generateArraySetterWithOne(TypeSpec.Builder cls, Property property) throws MojoExecutionException {
        MethodSpec.Builder setter = MethodSpec.methodBuilder("set"+getPropertyNameForMethod(property.getName()))
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                .addParameter(ClassName.get("net.gbmb.xemph","Packet"),"packet")
                .addParameter(String.class,"value")
                .addStatement("packet.addProperty("+getUpperPropertyName(property.getName())+","+getTypeClass(property.getType())+".parse(value))");
        cls.addMethod(setter.build());
    }


    private String getTypeClass (String typeName) throws MojoExecutionException {
        switch (typeName) {
            case "Text":
            case "String":
            case "OpenChoice":
            case "ClosedChoice":
            case "MIME":
            case "AgentName":
            case "Real":
            case "Integer":
            case "URI":
            case "URL":
            case "Boolean":
            case "RenditionClass":
            case "Date":
            case "Part":
            case "GUID":
            case "ProperName":
                return "SimpleValue";
            case "Unordered":
                return "UnorderedArray";
            case "Ordered":
                return "OrderedArray";
            case "LangAlternative":
                return "AlternativeArray";
            case "Dimensions":
            case "ResourceRef":
            case "ResourceEvent":
                return "Structure";
            default:
                throw new MojoExecutionException("Unknown type: " + typeName);
        }
    }

    private String getUpperPropertyName (String propertyName) {
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(propertyName.charAt(0)));
        for (int i=1; i < propertyName.length(); i++) {
            if (Character.isUpperCase(propertyName.charAt(i))) {
                sb.append('_');
            }
            sb.append(Character.toUpperCase(propertyName.charAt(i)));
        }
        String tmp = sb.toString();
        if (tmp.contains("P_D_F"))
            tmp = tmp.replace("P_D_F", "PDF");
        if (tmp.endsWith("_I_D"))
            tmp =tmp.substring(0,tmp.length()-3)+"ID";
        return tmp;
    }

    private String getPropertyNameForMethod (String propertyName) {
        StringBuilder sb = new StringBuilder(propertyName.length());
        sb.append(propertyName.substring(0,1).toUpperCase());
        sb.append(propertyName.substring(1));
        return sb.toString();
    }

}
