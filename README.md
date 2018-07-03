# Xemph: XMP Parser

[![Build Status](https://travis-ci.org/gbm-bailleul/xemph.svg?branch=master)](https://travis-ci.org/gbm-bailleul/xemph)
[![Codecov](https://img.shields.io/codecov/c/github/gbm-bailleul/xemph.svg)](https://codecov.io/gh/gbm-bailleul/xemph/)
[![License](https://img.shields.io/github/license/gbm-bailleul/xemph.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Abstract

This project is just a new XMP parser. It has been inspired of the xmpbox module in Apache PDFBox project. 
On the contrary of Xmpbox, I did not try keep compatibility with the Jempbox.

Most of the namespaces described in the XMP specification are implemented. Namespaces helpers are generated from a 
configuration file. It is possible to create new namespaces with a simple configuration file and using the xemph generator plugin for maven..

## Examples

Here are some examples based on PDFBox

### Extracting XMP object from PDF

How to extract xmp packet from PDF file

```java
// open PDF file
File input = new File ("files/XMPSpecificationPart1.pdf");
RandomAccessFile raf = new RandomAccessFile(input,"r");
PDFParser parser = new PDFParser(raf);
parser.parse();
PDDocument document = parser.getPDDocument();

// retrieve serialized xmp from document catalog
InputStream inputstream = document.getDocumentCatalog().getMetadata().exportXMPMetadata();

// deserialize
XmpReader xmpReader = new XmpReader();
Packet packet = xmpReader.parse(inputstream);

// display
for (Map.Entry<Name,Value> entry: packet.getProperties().entrySet()) {
    System.out.println(entry.getKey()+" : "+entry.getValue().getClass()+" / "+entry.getValue());
}
System.out.println(Xmp.getMetadataDate(packet).asDate().getTime());
System.out.println(XmpMM.getInstanceID(packet).asUUID());

OrderedArray<Value> creators = DublinCore.getCreator(packet);
for (Value sv : creators.getItems()) {
    System.out.println(">> "+sv.getClass().getSimpleName()+" : "+sv.toString());
}
System.out.println("Contains DC:title = "+DublinCore.containsTitle(packet));
```
### Creating a PDF with XMP packet

This example is extracted from xemph-example (PDFCreator.java)

```java
// create the PDF Document
PDDocument document = new PDDocument();
PDPage blankPage = new PDPage();
document.addPage( blankPage );

// create the xmp packet
Packet packet = new Packet();
DublinCore.setCreator(packet,"Guillaume Bailleul");
// There is no helper for this one
packet.addProperty(DublinCore.DATE, new Date());
DublinCore.setPublisher(packet,UnorderedArray.parse("publisher1","publisher2","publisher3","publisher4"));

// serialize the xmp packet
XmpWriter writer = new XmpWriter();
byte [] xmpContent = writer.write(packet);

// add the serialized xmp in the document catalog
PDMetadata metadata = new PDMetadata(document);
metadata.importXMPMetadata(xmpContent);
document.getDocumentCatalog().setMetadata(metadata);
document.save("example.pdf");
```

### Creating a specific namespace

Current namespaces are defined in yaml files (see core/namespaces). 

It is possible to generate new namespaces with a simple yaml file and with a maven plugin. Here is an example
of namespace; default prefix will be **ec**, its url will be **http://example.com/my_ns/1.0** and it will have
5 properties (yes this is an extract of Dublin Core).

```yaml
default-prefix: ec
uri: http://example.com/my_ns/1.0
classname: ExampleNamespace
properties:
  - name: contributor
    type: Unordered
    element: Text
  - name: coverage
    type: Text
  - name: creator
    type: Ordered
    element: ProperName
  - name: date
    type: Ordered
    element: Date
  - name: description
    type: LangAlternative
```

In your project pom, add this maven plugin:

```xml
<build>
        <plugins>
        ...
            <plugin>
                <groupId>xemph</groupId>
                <artifactId>generator</artifactId>
                <version>${project.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>genNamespaces</goal>
                        </goals>
                        <configuration>
                            <packageName>com.example.namespaces</packageName>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
         ...
        </plugins>
    </build>
```

The classes will be generated in *target/generated-sources/namespaces*

## Format

Only XML parsing and writing is implemented

