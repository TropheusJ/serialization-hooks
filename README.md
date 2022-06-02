# Serialization Hooks
#### Adds more hooks for Serialization of things.
Currently, only Ingredients are supported, however more is planned.

# Use
### Setup
```groovy
maven { url = "https://mvn.devos.one/snapshots/" }
```
```groovy
modImplementation("io.github.tropheusj:serialization-hooks:VERSION")
```
find the latest version by [browsing the maven](https://mvn.devos.one/#/snapshots/io/github/tropheusj/serialization-hooks).

### Actually using it
Once you have it in your environment, you'll want to register an `IngredientDeserializer`.
Simply create a class and implement the interface. Note that a deserializer only handles deserialization;
serialization should be handled by the Ingredient with `toJson` and `toNetwork`.
```java
Registry.register(IngredientSerializer.REGISTRY, new ResourceLocation("example_mod", "example"), new ExampleIngredientDeserializer());
```
Your custom Ingredients should implement the `CustomIngredient` interface.
This is required for correct functionality. `BaseCustomIngredient` has been
provided for convenience.

Finally, to actually use your custom Ingredients, whenever an Ingredient is used
in Json, you can add a `type` entry to the object pointing to your deserializer.
```json
{
    "ingredient": {
        "type": "example_mod:example",
        "things": 27,
        "stuff": [
            {
                "perambulations": 12345
            }
        ],
        "aaaaaa": {
            // you get it.
        }
    }
}
```
