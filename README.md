## Synopsis

MindlessMongo is a performant library which allows users to easily map Java fields to MongoDB documents, with seamless integration into existing legacy databases or new projects. MindlessMongo can easily load objects from a database, and save only the modified fields back into the database.

## Code Example

Take this example Mongo document ([credits](https://docs.mongodb.com/manual/core/document/)):

``` 
{
    name: { first: "Alan", last: "Turing" },
    birth: new Date('Jun 23, 1912'),
    death: new Date('Jun 07, 1954'),
    contribs: [ "Turing machine", "Turing test", "Turingery" ],
    views : NumberLong(1250000)
}
```

Using the [traditional mongo java API](https://github.com/mongodb/mongo-java-driver), this object could be read into memory, modified, and saved using the following code:

```
public class Person {
    String firstName;
    String lastName;
    Date birth;
    Date death;
    ArrayList<String> contributions;
    int views;
    
    public Person(MongoClient client, String database, String collection, BasicDBObject query) {
      DB db = client.getDB(database);
      Collection c = db.getCollection(collection);
      BasicDBObject object = c.findOne(query);
      
      //Load objects into memory
      this.firstName = (String) ((DBObject) object.get("name")).get("first");
      this.lastName = (String) ((DBObject) object.get("name")).get("last");
      
      this.birth = (Date) object.get("birth");
      this.death = (Date) object.get("death");
      this.contributions = (ArrayList<String>) object.get("contribs");
      this.views = (int) object.get("int");
      
      //Add new contribution
      this.contributions.add("LU decomposition");
      c.update(query, new BasicDBObject("$set", new BasicDBObject("contributions", contributions)));
    }
}
```

Using MindlessMongo, this same class can be written like this:
```
public class Person extends MongoDocument {
    @StoredField("name.first")
    String firstName;
    @StoredField("name.last")
    String lastName;
    @StoredField("birth")
    Date birth;
    @StoredField("death")
    Date death;
    @StoredField("contributions")
    ArrayList<String> contributions;
    @StoredField("views")
    int views;
    
    public Person(MongoClient client, String database, String collection, BasicDBObject query) {
      super(client, database, collection, query);
      
      //Load objects into memory
      load();
      //Add new contribution
      this.contributions.add("LU decomposition");
      save();
    }
}
```

## Motivation

Existing, more complex MongoDB mapping libraries (like [Morphia](http://mongodb.github.io/morphia/)) rely on database schemas being designed around java classes. By offering a more performant and lightweight alternative, MindlessMongo gives existing projects access to mapping technologies without changing their existing database, and gives new projects a lightweight wrapper which offers unprecedented performance gains. 

## Credits

Since reflection is regularly very intensive, MindlessMongo relies on [ReflectASM](https://github.com/EsotericSoftware/reflectasm) for high performance reflection via code generation.
