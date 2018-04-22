# Kotlin application. 

Specification:
* Vertx.io web
* Vertx.io micro services (across vertex eventBus)
* NoSQL database (**Neo4j**) 
* TDD first steps

Are used by application:

* `8080` port for web application
* connection with `7687` neo4j server

### Neo4J

Neo4j useful commands:

`MATCH (n) detach delete n`             # delete all

`MATCH (n) return n`                    # show graph

`MATCH (n:g1) return n`                 # show nodes of specific graph
 
```
MATCH (n:g1) delete n:g1 set n:g2       # link nodes with other graph
```

```
MATCH (f:g1) where f.body=$specialValue
CREATE (t:g1) set t.body=$otherValue
CREATE (f)-[:Linked {min: $min, max: $max, delta: $delta, dispersion: $d}]->(t)       
```
