import requests
import json

def generator():
    s = 1523739600000
    for i in range(0, 10000):
        s += 86400000
        yield s

gen = generator()

data = [
        {"tid": 1, "body": "SELECT * FROM t", "timestamp": next(gen), "params": [] },
        {"tid": 1, "body": "query {repository(owner: \"user\") {name. description} }", "timestamp": next(gen), "params": [] },
        {"tid": 1, "body": "MATCH (f:?) where f.body=?", "timestamp": next(gen), "params": [] },
        {"tid": 2, "body": "MATCH (f:?) where f.body=?", "timestamp": next(gen), "params": [] },
        {"tid": 2, "body": "SELECT * FROM t", "timestamp": next(gen), "params": [] },
        {"tid": 2, "body": "db.collection.find({id: ??}.toArray()", "timestamp": next(gen), "params": [] },
        {"tid": 3, "body": "SELECT id FROM t where id > ?", "timestamp": next(gen), "params": [] },
        {"tid": 3, "body": "query {repository(owner: \"user\") {name. description} }", "timestamp": next(gen), "params": [] },
        {"tid": 3, "body": "db.collection.find({id: ??})", "timestamp": next(gen), "params": [] },
        {"tid": 3, "body": "db.collection.find({id: ??}, {_id: 0, description: 1})", "timestamp": next(gen), "params": [] },
        {"tid": 4, "body": "db.collection.find({id: ??}.toArray()", "timestamp": next(gen), "params": [] },
        {"tid": 4, "body": "db.collection.find({id: ??}.limit(?)", "timestamp": next(gen), "params": [] },
        {"tid": 5, "body": "MATCH (f:?) where f.body=?", "timestamp": next(gen), "params": [] },
]

for record in data:
    info = json.dumps(record)
    r = requests.post("http://127.0.0.1:8080/", data=info)
