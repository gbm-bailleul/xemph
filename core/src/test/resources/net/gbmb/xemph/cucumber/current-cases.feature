Feature: Current existing xmp constructions

     #  TODO xmp-1-2-unexpected-entity.xml


#  TODO xmp-1-inline.xml

#  TODO xmp-2-array-inline.xml

#  13/10/2017  07:10             1 472 xmp-3-array-description.xml
#  13/10/2017  07:10             1 265 xmp-5-simple-langalt.xml
#  13/10/2017  07:10             2 772 xmp-6-array-in-property.xml


  Scenario: Empty RDF without description
    Given an empty xmlrdf without description
    When parsing
    Then it should contain nothing

  Scenario: Empty RDF
    Given an empty xmlrdf with target 'mytarget'
    When parsing
    Then it should contain nothing

  Scenario: Empty RDF with empty target
    Given an empty xmlrdf with target ''
    When parsing
    Then it should contain nothing

  Scenario: Empty RDF without target attribute
    Given an empty xmlrdf with target ''
    And remove attribute 'rdf:about'
    When parsing
    Then it should contain nothing

  Scenario: One property
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'exa:name1' in namespace 'http://www.example.com' with simple value 'value1'
    When parsing
    Then it should contain 1 properties

  Scenario: One empty property
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'exa:BaseURL' in namespace 'http://ns.adobe.com/xap/1.0/' with simple value ''
    When parsing
    Then it should contain 1 properties
    And it should contain a property named 'BaseURL' in namespace 'http://ns.adobe.com/xap/1.0/' and not valued

  Scenario: One other property
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'exa:name1' in namespace 'http://www.example.com' with simple value 'value 1'
    And adding property 'exa:name2' in namespace 'http://www.example.com' with simple value 'value 2'
    When parsing
    Then it should contain 2 properties
    And it should contain a property named 'name1' in namespace 'http://www.example.com' valued 'value 1'
    And it should contain a property named 'name2' in namespace 'http://www.example.com' valued 'value 2'

  Scenario: Simple array
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'exa:name1' in namespace 'http://www.example.com' with simple value ''
    And adding element 'rdf:Bag'
    And adding child element 'rdf:li' with value 'aaa'
    And adding child element 'rdf:li' with value 'bbb'
    And back parent
    And adding property 'exa:name2' in namespace 'http://www.example.com' with simple value ''
    And adding element 'rdf:Seq'
    And adding child element 'rdf:li' with value 'ccc'
    And adding child element 'rdf:li' with value 'ddd'
    When parsing
    Then it should contain 2 properties
    And it should contain an array named 'name1' in namespace 'http://www.example.com' which is UnorderedArray
    And it should contain an array named 'name2' in namespace 'http://www.example.com' which is OrderedArray

  Scenario: Simple array with not existing list namespace
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'exa:name1' in namespace 'http://www.example.com' with simple value ''
    And adding element 'rdf:Bag'
    And adding child element 'rdf:li' with value 'aaa'
    And adding child element 'rdf:li' with value 'bbb'
    And back parent
    When parsing with 'rdf:Bag' to 'rdff:Bag'
    Then it should fail with 'XMLStreamException' exception

  Scenario: Simple valid structure
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'xmpTPg:MaxPageSize' in namespace 'http://ns.adobe.com/xap/1.0/t/pg/' with simple value ''
    And adding element 'rdf:Description'
    And adding child element 'stDim:h' with value '11.0' and namespace 'http://ns.adobe.com/xap/1.0/sType/Dimensions#'
    And adding child element 'stDim:w' with value '8.5' and namespace 'http://ns.adobe.com/xap/1.0/sType/Dimensions#'
    And adding child element 'stDim:unit' with value 'inch' and namespace 'http://ns.adobe.com/xap/1.0/sType/Dimensions#'
    When parsing
    Then it should contain 1 properties
    # TODO test structure has 3 elements (and values)


  Scenario: Valid array of valid structure
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'xmpMM:Pantry' in namespace 'http://ns.adobe.com/xap/1.0/mm/' with simple value ''
    And adding element 'rdf:Bag'
    And adding element 'rdf:li'
    And adding element 'rdf:Description'
    And adding child element 'stDim:h' with value '11.0' and namespace 'http://ns.adobe.com/xap/1.0/sType/Dimensions#'
    And adding child element 'stDim:w' with value '8.5' and namespace 'http://ns.adobe.com/xap/1.0/sType/Dimensions#'
    And adding child element 'stDim:unit' with value 'inch' and namespace 'http://ns.adobe.com/xap/1.0/sType/Dimensions#'
    And back parent
    And back parent
    And adding element 'rdf:li'
    And adding element 'rdf:Description'
    And adding child element 'stDim:h' with value '11.0' and namespace 'http://ns.adobe.com/xap/1.0/sType/Dimensions#'
    And adding child element 'stDim:w' with value '8.5' and namespace 'http://ns.adobe.com/xap/1.0/sType/Dimensions#'
    And adding child element 'stDim:unit' with value 'inch' and namespace 'http://ns.adobe.com/xap/1.0/sType/Dimensions#'
    When parsing
    Then it should contain 1 properties
    And it should contain an array named 'Pantry' in namespace 'http://ns.adobe.com/xap/1.0/mm/' which is UnorderedArray
    # TODO test structure has 3 elements (and values)

  Scenario: Simple Description
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'dc:source' in namespace 'http://purl.org/dc/elements/1.1/' with simple value ''
    And adding element 'rdf:Description'
    And adding child element 'xe:qualifier' with value 'artificial example' and namespace 'http://ns.adobe.com/xmp-example/'
    And adding child element 'rdf:value' with value 'Adobe XMP Specification'
    When parsing
    Then it should contain 1 properties

  Scenario: Array of Description
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'dc:subject' in namespace 'http://purl.org/dc/elements/1.1/' with simple value ''
    And adding element 'rdf:Bag'
    And adding element 'rdf:li' with value 'XMP'
    And back parent
    And adding element 'rdf:li'
    And adding element 'rdf:Description'
    And adding child element 'rdf:value' with value 'Metadata'
    And adding child element 'xe:qualifier' with value 'artificial example' and namespace 'http://ns.adobe.com/xmp-example/'
    When parsing
    Then it should contain 1 properties
    And it should contain an array named 'subject' in namespace 'http://purl.org/dc/elements/1.1/' which is UnorderedArray

  Scenario: Simple lang alternative
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'dc:Example' in namespace 'http://purl.org/dc/elements/1.1/' with simple value ''
    And adding element 'rdf:Alt'
    And adding element 'rdf:li' with value 'titre francais'
    And adding attribute 'xml:lang' with value 'fr-fr'
    And back parent
    And adding element 'rdf:li' with value 'english title'
    And adding attribute 'xml:lang' with value 'en-us'
    And back parent
    When parsing
    Then it should contain 1 properties
    And it should contain an array named 'Example' in namespace 'http://purl.org/dc/elements/1.1/' which is AlternativeArray


