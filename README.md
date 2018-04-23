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

```
MATCH (f:g0), (t:g0) where f.body = "SQL" AND t.body = "SQL"
                CREATE (f)-[:Linked {min: 0.5}]->(t)
```

```
MATCH (n:g0)-[:Linked {min: nmin, max: nmax, delta: ndelta, dispersia: nd}]->(nr) 
MATCH (g:g4)-[:Linked {min: gmin, max: gmax, delta: gdelta, dispersia: gd}]->(gr:g4)
MERGE (n:g0 {body: g.body})-[:Linked {}]->(gr:g0)
return n, nr, g, gr

MATCH (g:g1) 
MERGE (v:g0 {body: g.body}) 
set v.ts = g.ts 
return v

MATCH (prev:g1)-[r:Linked]->(prevr:g1)
MATCH (g:g0 {body: prev.body})
MATCH (gr:g0 {body: prevr.body})
MERGE (g)-[w:Linked]->(gr)
set w = {min:r.min, min: r.max, delta: r.delta, dispersion: r.dispersion}
RETURN g, w, gr

```