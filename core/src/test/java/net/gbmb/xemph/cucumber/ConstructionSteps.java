package net.gbmb.xemph.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import net.gbmb.xemph.Namespaces;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ConstructionSteps {

    private Element root;

    private Element description;

    private Element current;

    @Before
    public void before(Scenario scenario) {
        System.out.println("------------------------------");
        System.out.println("Starting - " + scenario.getName());
        System.out.println("------------------------------");
    }


    @Given("^an empty xmlrdf without description$")
    public void an_empty_xmlrdf_without_description() throws Throwable {
        // create the getDocument
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        DocumentHandler.setDocument(docBuilder.newDocument());
        DocumentHandler.getDocument().setXmlStandalone(true);
        // add the rdf:RDF
        root = DocumentHandler.getDocument().createElementNS(Namespaces.RDF,"rdf:RDF");
        DocumentHandler.getDocument().appendChild(root);
        current = root;

    }


    @Given("^an empty xmlrdf with target '([^']*)'$")
    public void an_empty_xmlrdf(String target) throws Throwable {
        an_empty_xmlrdf_without_description();
        // add rdf:Description
        description = DocumentHandler.getDocument().createElementNS(Namespaces.RDF, "rdf:Description");
        description.setAttributeNS(Namespaces.RDF,"rdf:about",target);
        root.appendChild(description);
        // current is the description
        current = description;
    }

    // TODO reecrire
    @Given("^adding property '([^:]*):([^']*)' in namespace '([^']*)' with simple value '([^']*)'$")
    public void adding_property_name_in_namespace_ns_with_simple_value_value(String arg10, String arg11, String arg2, String arg3) throws Throwable {
        root.setAttribute("xmlns:"+arg10,arg2);
        Element element = DocumentHandler.getDocument().createElementNS(arg2,arg10+":"+arg11);
        if (arg3!=null && arg3.length()>0) element.setTextContent(arg3);
        description.appendChild(element);
        current = element;
    }


    @Given("^set value '([^']*)' to attribute '([^:]*):([^']*)'$")
    public void set_value_to_attribute(String value, String prefix, String name) throws Throwable {
        current.setAttribute(prefix+":"+name+"A",value);
    }


    @Given("^remove attribute '([^']*)'$")
    public void remove_attribute(String name) throws Throwable {
        current.removeAttribute(name);
    }

    @Given("^adding element '([^']*)'$")
    public void adding_element_rdf_Bag(String name) throws Throwable {
        Element ne = DocumentHandler.getDocument().createElement(name);
        current.appendChild(ne);
        current = ne;
    }

    @Given("^adding element '([^']*)' with value '([^']*)'$")
    public void adding_element_rdf_li_with_value_aaa(String name, String value) throws Throwable {
        Element ne = DocumentHandler.getDocument().createElement(name);
        ne.setTextContent(value);
        current.appendChild(ne);
        current = ne;
    }

    @Given("^adding child element '([^']*)' with value '([^']*)'$")
    public void adding_child_element_rdf_li_with_value_aaa(String name, String value) throws Throwable {
        Element ne = DocumentHandler.getDocument().createElement(name);
        ne.setTextContent(value);
        current.appendChild(ne);
    }

    @Given("^adding child element '([^:]*):([^']*)' with value '([^']*)' and namespace '([^']*)'$")
    public void adding_child_element_rdf_li_with_value_aaa(String prefix, String name, String value, String ns) throws Throwable {
        root.setAttribute("xmlns:"+prefix,ns);
        Element ne = DocumentHandler.getDocument().createElementNS(ns,prefix+":"+name);
        ne.setTextContent(value);
        current.appendChild(ne);
    }

    @Given("^adding attribute '([^:]*):([^']*)' with value '([^']*)'$")
    public void adding_attribute_with_value(String prefix, String sname, String value) throws Throwable {
        current.setAttribute(prefix+":"+sname,value);
    }


    @Given("^back parent$")
    public void back_parent() throws Throwable {
        current = (Element)current.getParentNode();
    }

}
