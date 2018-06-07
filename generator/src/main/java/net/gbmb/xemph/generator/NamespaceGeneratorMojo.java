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
import net.sourceforge.jenesis4java.*;
import net.sourceforge.jenesis4java.jaloppy.JenesisJalopyEncoder;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * see http://jenesis4java.sourceforge.net/index.html
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
        System.setProperty("jenesis.encoder", JenesisJalopyEncoder.class.getName());
        VirtualMachine vm = VirtualMachine.getVirtualMachine();
        CompilationUnit unit = vm.newCompilationUnit(this.outputJavaDirectory.getAbsolutePath());
        unit.setNamespace(packageName);

        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Namespace ns = mapper.readValue(file,Namespace.class);

        PackageClass cls = unit.newClass(ns.getClassname());
        cls.addImport("net.gbmb.xemph.Namespace");
        cls.addImport("net.gbmb.xemph.Name");
        cls.addImport("net.gbmb.xemph.Value");
        cls.addImport("net.gbmb.xemph.Packet");
        cls.addImport("net.gbmb.xemph.values.*");
        cls.addImport("net.gbmb.xemph.namespaces.*");
        cls.setExtends("Namespace");
        cls.setAccess(Access.AccessType.PUBLIC);

        ClassField defaultPrefix = cls.newField(String.class,"DEFAULT_PREFIX");
        defaultPrefix.setAccess(Access.AccessType.PUBLIC);
        defaultPrefix.isStatic(true);
        defaultPrefix.isFinal(true);
        defaultPrefix.setExpression(vm.newString(ns.getDefaultPrefix()));

        ClassMethod getDefaultPrefix = cls.newMethod(vm.newType(String.class),"getDefaultPrefix");
        getDefaultPrefix.setAccess(Access.AccessType.PUBLIC);
        getDefaultPrefix.newReturn().setExpression(vm.newVar("DEFAULT_PREFIX"));
        getDefaultPrefix.addAnnotation(vm.newAnnotation(Override.class));

        ClassField namespaceURI = cls.newField(String.class,"NAMESPACE_URI");
        namespaceURI.setAccess(Access.AccessType.PUBLIC);
        namespaceURI.isStatic(true);
        namespaceURI.isFinal(true);
        namespaceURI.setExpression(vm.newString(ns.getUri()));

        ClassMethod getNamespaceURI = cls.newMethod(vm.newType(String.class),"getNamespaceURI");
        getNamespaceURI.setAccess(Access.AccessType.PUBLIC);
        getNamespaceURI.newReturn().setExpression(vm.newVar("NAMESPACE_URI"));
        getNamespaceURI.addAnnotation(vm.newAnnotation(Override.class));

        ClassMethod getPropertyType = cls.newMethod(vm.newType(Class.class), "getPropertyType");
        getPropertyType.addParameter(String.class, "propertyName");
        getPropertyType.setAccess(Access.AccessType.PUBLIC);
        getPropertyType.addAnnotation(vm.newAnnotation(Override.class));
        getPropertyType.newStmt(vm.newFree("if (propertyName==null) return null"));

        for (Property property:ns.getProperties()) {
            // create field
            ClassField field1 = cls.newField(vm.newType("Name"),getUpperPropertyName(property.getName()));
            field1.isStatic(true);
            field1.isFinal(true);
            field1.setAccess(Access.AccessType.PUBLIC);
            field1.setExpression(vm.newFree("new Name(NAMESPACE_URI,\""+property.getName()+"\")"));
            // type
            String ifStmt = String.format("else if (%s.getLocalName().equals(propertyName)) return %s",getUpperPropertyName(property.getName()),getTypeClass(property.getType())+".class");
            getPropertyType.newStmt(vm.newFree(ifStmt));
        }

        getPropertyType.newStmt(vm.newFree("else return null"));


        for (Property property:ns.getProperties()) {
            generateGetter(vm,cls,property);
            generateContains(vm,cls,property);
            generateSetter(vm,cls,property);
        }

        unit.encode();

    }

    private void generateGetter (VirtualMachine vm, PackageClass cls,Property property) throws MojoExecutionException {
        ClassMethod getMethod = cls.newMethod(
                vm.newType(getTypeClass(property.getType())),
                "get"+getPropertyNameForMethod(property.getName()));
        getMethod.setAccess(Access.AccessType.PUBLIC);
        getMethod.isStatic(true);
        getMethod.addParameter(vm.newType("Packet"), "packet");
        getMethod.newStmt(vm.newFree("return ("+getTypeClass(property.getType())+")packet.getValue("+getUpperPropertyName(property.getName())+")"));
    }

    private void generateContains (VirtualMachine vm, PackageClass cls,Property property) throws MojoExecutionException {
        ClassMethod getMethod = cls.newMethod(
                vm.newType("boolean"),
                "contains"+getPropertyNameForMethod(property.getName()));
        getMethod.setAccess(Access.AccessType.PUBLIC);
        getMethod.isStatic(true);
        getMethod.addParameter(vm.newType("Packet"), "packet");
        getMethod.newReturn().setExpression(vm.newFree("packet.contains("+getUpperPropertyName(property.getName())+")"));
    }

    private void generateSetter (VirtualMachine vm, PackageClass cls,Property property) throws MojoExecutionException {
        ClassMethod setMethod = cls.newMethod(
                "set"+getPropertyNameForMethod(property.getName()));
        setMethod.setAccess(Access.AccessType.PUBLIC);
        setMethod.isStatic(true);
        setMethod.addParameter(vm.newType("Packet"), "packet");
        String propertyClass = getTypeClass(property.getType());
        setMethod.addParameter(vm.newType(propertyClass), "value");
        setMethod.newStmt(vm.newFree("packet.addProperty("+getUpperPropertyName(property.getName())+",value)"));
        // generate string setter if property is simple value
        if (propertyClass.equals("SimpleValue")) {
            generateSetterFromString(vm,cls,property);
        }
        // generate string setter for array
        if (propertyClass.equals("UnorderedArray") || propertyClass.equals("OrderedArray")) {
            generateArraySetterWithOne(vm,cls,property);
        }
    }

    private void generateSetterFromString(VirtualMachine vm, PackageClass cls, Property property) throws MojoExecutionException {
        ClassMethod setMethod = cls.newMethod(
                "set"+getPropertyNameForMethod(property.getName()));
        setMethod.setAccess(Access.AccessType.PUBLIC);
        setMethod.isStatic(true);
        setMethod.addParameter(vm.newType("Packet"), "packet");
        setMethod.addParameter(vm.newType("String"), "value");
        setMethod.newStmt(vm.newFree("SimpleValue simple = SimpleValue.parse(value)"));
        setMethod.newStmt(vm.newFree("packet.addProperty("+getUpperPropertyName(property.getName())+",simple)"));
    }

    private void generateArraySetterWithOne(VirtualMachine vm, PackageClass cls, Property property) throws MojoExecutionException {
        ClassMethod setMethod = cls.newMethod(
                "set"+getPropertyNameForMethod(property.getName()));
        setMethod.setAccess(Access.AccessType.PUBLIC);
        setMethod.isStatic(true);
        setMethod.addParameter(vm.newType("Packet"), "packet");
        setMethod.addParameter(vm.newType("String"), "value");
        setMethod.newStmt(vm.newFree("packet.addProperty("+getUpperPropertyName(property.getName())+","+getTypeClass(property.getType())+".parse(value))"));
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
