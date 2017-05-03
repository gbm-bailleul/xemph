package net.gbmb.xemph.generator;

import net.sourceforge.jenesis4java.*;
import net.sourceforge.jenesis4java.jaloppy.JenesisJalopyEncoder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

/**
 * see http://jenesis4java.sourceforge.net/index.html
 * @goal genNamespaces
 * @phase generate-sources
 * @description generate the java source code of namespaces
 */
public class NamespaceGeneratorMojo extends AbstractMojo {

    private Log log;

    /**
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;

    /**
     * @parameter expression=
     *            "${project.build.directory}/generated-sources/namespaces"
     * @required
     */
    protected File outputJavaDirectory;

    /**
     * @parameter expression=
     *            "${project.basedir}/namespaces"
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
        try {
            generate();
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


    private void generate () throws IOException {
        System.setProperty("jenesis.encoder", JenesisJalopyEncoder.class.getName());
        VirtualMachine vm = VirtualMachine.getVirtualMachine();
        CompilationUnit unit = vm.newCompilationUnit(this.outputJavaDirectory.getAbsolutePath());
        unit.setNamespace(packageName);

        PackageClass cls = unit.newClass("DublinCore");
        cls.addImport("net.gbmb.xemph.Namespace");
        cls.addImport("net.gbmb.xemph.Name");
        cls.setExtends("Namespace");
        cls.setAccess(Access.AccessType.PUBLIC);

        ClassField defaultPrefix = cls.newField(String.class,"DEFAULT_PREFIX");
        defaultPrefix.setAccess(Access.AccessType.PUBLIC);
        defaultPrefix.isStatic(true);
        defaultPrefix.isFinal(true);
        defaultPrefix.setExpression(vm.newString("dc"));

        ClassMethod getDefaultPrefix = cls.newMethod(vm.newType(String.class),"getDefaultPrefix");
        getDefaultPrefix.setAccess(Access.AccessType.PUBLIC);
        getDefaultPrefix.newReturn().setExpression(vm.newVar("DEFAULT_PREFIX"));
        getDefaultPrefix.addAnnotation(vm.newAnnotation(Override.class));

        ClassField namespaceURI = cls.newField(String.class,"NAMESPACE_URI");
        namespaceURI.setAccess(Access.AccessType.PUBLIC);
        namespaceURI.isStatic(true);
        namespaceURI.isFinal(true);
        namespaceURI.setExpression(vm.newString("http://purl.org/dc/elements/1.1/"));

        ClassMethod getNamespaceURI = cls.newMethod(vm.newType(String.class),"getNamespaceURI");
        getNamespaceURI.setAccess(Access.AccessType.PUBLIC);
        getNamespaceURI.newReturn().setExpression(vm.newVar("NAMESPACE_URI"));
        getNamespaceURI.addAnnotation(vm.newAnnotation(Override.class));

        ClassField field1 = cls.newField(vm.newType("Name"),"TITLE");
        field1.isStatic(true);
        field1.isFinal(true);
        field1.setAccess(Access.AccessType.PUBLIC);
        field1.setExpression(vm.newFree("new Name(NAMESPACE_URI,\"title\")"));


        unit.encode();

    }


}
