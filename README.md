# Xemph: XMP Parser

[![Build Status](https://travis-ci.org/gbm-bailleul/xemph.svg?branch=master)](https://travis-ci.org/gbm-bailleul/xemph)
[![Codecov](https://img.shields.io/codecov/c/github/gbm-bailleul/xemph.svg)](https://codecov.io/gh/gbm-bailleul/xemph/)
[![License](https://img.shields.io/github/license/gbm-bailleul/xemph.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Abstract

This project is just a new XMP parser. It has been inspired of the xmpbox module in Apache PDFBox project. 
On the contrary of Xmpbox, I did not try keep compatibility with the Jempbox.

## Example

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

## Format

Only XML parsing and writing is implemented

