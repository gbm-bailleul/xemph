Feature: invalid xml blocks

  Scenario: Empty RDF with unexpected element in place of Description
    Given an empty xmlrdf with target 'mytarget'
    When parsing with 'rdf:Description' to 'rdf:Unexpected'
    Then it should fail with 'XMLStreamException' exception

  Scenario: Empty RDF with not closing Description element
    Given an empty xmlrdf with target 'mytarget'
    When parsing with '</rdf:Description>' to ''
    Then it should fail with 'XMLStreamException' exception

  Scenario: Empty RDF with invalid rdf namespace
    Given an empty xmlrdf with target 'mytarget'
    When parsing with 'xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"' to 'xmlns:rdf="http://www.example.com"'
    Then it should fail with 'XMLStreamException' exception

  Scenario: Simple array with not existing list
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'exa:name1' in namespace 'http://www.example.com' with simple value ''
    And adding element 'rdf:Bag'
    And adding child element 'rdf:li' with value 'aaa'
    And adding child element 'rdf:li' with value 'bbb'
    And back parent
    When parsing with 'rdf:Bag' to 'rdf:NoExist'
    Then it should fail with 'XMLStreamException' exception

  Scenario: Unexpected entity
    Given an empty xmlrdf with target 'mytarget'
    And adding property 'exa:name1' in namespace 'http://www.example.com' with simple value 'VALUE_TO_REPLACE'
    When parsing with 'VALUE_TO_REPLACE' to '<?hello?>'
    Then it should fail with 'XMLStreamException' exception

