# mybatis-mapper-generator
Mapper generator for MyBatis.

**How to use:** change ```YOUR_CLASS_HERE``` on ```MyBatisMapperGenerator``` with the class you want to generate mapper to. Execute as Java Application. The mapper will be printed to the console.

Features:

* Generates MyBatis mapping of given Java class fields with the columns in database.
* Generates resultMap tag with id, result, association and collection tags, filling important mapping attributes of those tags.
* Identifies attribute and column properties of id tags.
* Identifies attribute and column properties of result tags.
* Identifies attribute, column and javaType properties of association tags.
* Identifies attribute, column and javaType properties of collection tags.
* Generates subselect tag wich will be used to fill associations. Links it with the select attribute of association tags.
* Generates subselect tag wich will be used to fill collections. Links it with the select attribute of collection tags.


Limitations:

* Only works with annotation based mapping.
* Only works when annotation is done on fields, not on methods.
* Does not detect fields of parent class.
* Only works with javax.persistence annotations (although it is very straightforward to add support to other annotations).
* Does not identify ofType attribute of collections.

To do:

* Identify ofType attribute of collections.
* Recursively map user-defined classes, used inside the given class.
