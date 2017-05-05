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
//        getPropertyType.isStatic(true);
        getPropertyType.newStmt(vm.newFree("if (propertyName==null) return null"));

        for (Property property:ns.getProperties()) {
            // create field
            ClassField field1 = cls.newField(vm.newType("Name"),getUpperPropertyName(property.getName()));
            field1.isStatic(true);
            field1.isFinal(true);
            field1.setAccess(Access.AccessType.PUBLIC);
            field1.setExpression(vm.newFree("new Name(NAMESPACE_URI,\""+property.getName()+"\")"));
            // type
            String ifStmt = String.format("else if (%s.equals(propertyName)) return %s",getUpperPropertyName(property.getName()),getTypeClass(property.getType()));
            getPropertyType.newStmt(vm.newFree(ifStmt));
        }

        getPropertyType.newStmt(vm.newFree("else return null"));


        unit.encode();

    }

    private String getTypeClass (String typeName) throws MojoExecutionException {
        if ("Text".equals(typeName)) return "SimpleValue.class";
        else if ("OpenChoice".equals(typeName)) return "SimpleValue.class";
        else if ("ClosedChoice".equals(typeName)) return "SimpleValue.class";
        else if ("MIME".equals(typeName)) return "SimpleValue.class";
        else if ("AgentName".equals(typeName)) return "SimpleValue.class";
        else if ("Real".equals(typeName)) return "SimpleValue.class";
        else if ("Integer".equals(typeName)) return "SimpleValue.class";
        else if ("Date".equals(typeName)) return "DateValue.class";
        else if ("Unordered".equals(typeName)) return "UnorderedArray.class";
        else if ("Ordered".equals(typeName)) return "OrderedArray.class";
        else if ("LangAlternative".equals(typeName)) return "AlternativeArray.class";
        else if ("Dimensions".equals(typeName)) return "Dimensions.class";
        else throw new MojoExecutionException("Unknown type: "+typeName);
    }

    private String getUpperPropertyName (String propertyName) {
        return propertyName.toUpperCase(); // TODO improve (-)
    }

}
