package net.gbmb.xemph.cucumber;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.gbmb.xemph.Name;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;
import net.gbmb.xemph.values.ArrayValue;
import net.gbmb.xemph.values.SimpleValue;
import net.gbmb.xemph.xml.XmlReader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ValidationSteps {

    private Packet packet;

    private Exception parsingException;

    private String xmlToParse;


    @After
    public void after(Scenario scenario) {
        System.out.println();
        System.out.printf(xmlToParse);
        System.out.println();
        System.out.println(scenario.getName() + " Status - " + scenario.getStatus());
        System.out.println("------------------------------");
        System.out.println();
        System.out.println();
    }


    public void parsing_with_replace(String ... tokens) throws Throwable {
        // ensure we have pairs
        assertEquals(0,tokens.length % 2);
        // generate byte array
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD,"html");
        transformer.setOutputProperty(OutputKeys.INDENT,"no"); // put yes to have human readable xml
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(DocumentHandler.getDocument());
        transformer.transform(source, result);
        xmlToParse = result.getWriter().toString().replaceFirst("encoding=\"UTF-8\"","");
        // replacement
        int pos = 0;
        while (pos < tokens.length) {
            xmlToParse = xmlToParse.replace(tokens[pos],tokens[pos+1]);
            pos += 2;
        }
        // parse byte array
        try {
            XmlReader reader = new XmlReader();
            packet = reader.parse(new ByteArrayInputStream(xmlToParse.getBytes("utf-8")));
        } catch (Exception e) {
            parsingException = e;
        }
    }

    @When("^parsing with '([^']*)' to '([^']*)'$")
    public void parsing_with_one_replace(String token,String value) throws Throwable {
        parsing_with_replace(token,value);
    }

    @When("^parsing$")
    public void doing_parsing() throws Throwable {
        parsing_with_replace();
    }

    @Then("^it should contain nothing$")
    public void it_should_contain_nothing() throws Throwable {
        assertEquals(0,packet.getProperties().size());
    }

    @Then("^it should contain (\\d+) properties$")
    public void it_should_contain_property(int arg1) throws Throwable {
        assertEquals(arg1,packet.getProperties().size());
    }

    @Then("^it should contain a property named '([^']*)' in namespace '([^']*)' valued '([^']*)'$")
    public void it_should_contain_a_property_named_name_in_namespace_http_www_example_com_valued_value(String sname, String ns, String evalue) throws Throwable {
        Name name = new Name(ns,sname);
        assertTrue(packet.contains(name));
        Value value = packet.getValue(name);
        assertTrue(value instanceof SimpleValue);
        assertEquals(evalue,((SimpleValue)value).getContent());
    }

    @Then("^it should contain a property named '([^']*)' in namespace '([^']*)' and not valued$")
    public void it_should_contain_a_property_named_name_in_namespace_without_value(String sname, String ns) throws Throwable {
        Name name = new Name(ns,sname);
        assertTrue(packet.contains(name));
        Value value = packet.getValue(name);
        assertTrue(value instanceof SimpleValue);
        assertEquals("",((SimpleValue)value).getContent());
    }

    @Then("^it should fail with '([^']*)' exception$")
    public void it_should_fail__exception(String name) throws Throwable {
        assertNotNull(parsingException);
        assertEquals(name,parsingException.getClass().getSimpleName());
    }

    @Then("^it should contain an array named '([^']*)' in namespace '([^']*)' which is (UnorderedArray|OrderedArray|AlternativeArray)$")
    public void it_should_contain_a_property_named_in_namespace_with_type(String sname, String ns, String type) throws Throwable {
        Name name = new Name(ns,sname);
        assertTrue(packet.contains(name));
        Value value = packet.getValue(name);
        assertTrue(value instanceof ArrayValue);
        assertEquals(type,value.getClass().getSimpleName());
    }



}
