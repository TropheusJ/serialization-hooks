# Serialization Hooks
#### Adds more hooks for Serialization of things.
Currently, only Ingredients are supported, however more is planned.

# Use
```groovy
maven { url = "https://mvn.devos.one/snapshots/" }
```
```groovy
modImplementation("io.github.tropheusj:serialization-hooks:VERSION")
```
find the latest version by [browsing the maven](https://mvn.devos.one/#/snapshots/io/github/tropheusj/serialization-hooks).

Once you have it in your environment, you'll want to register an `IngredientDeserializer`.
Simply create a class and implement the interface. Note that a deserializer only handles deserialization;
serialization should be handled by the Ingredient with `toJson` and `toNetwork`.
```java
Registry.register(IngredientSerializer.REGISTRY, id("example"), new ExampleIngredientSerializer());
```
