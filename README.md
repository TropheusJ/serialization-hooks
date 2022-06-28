# Serialization Hooks
#### Adds more hooks for Serialization of things.
Currently, Ingredients and Values are supported.

# Use
### Setup
```groovy
maven { url = "https://mvn.devos.one/snapshots/" }
```
```groovy
modImplementation(include("io.github.tropheusj:serialization-hooks:0.3.<build>"))
```
find the latest build from [GitHub Actions](https://github.com/TropheusJ/serialization-hooks/actions).

### Actually using it
#### Ingredients
Once you have it in your environment, you'll want to register an `IngredientDeserializer`.
Simply create a class and implement the interface. Note that a deserializer only handles deserialization;
serialization should be handled by the Ingredient with `toJson` and `toNetwork`. Note that `toNetwork` must
always first write the `Identifier`/`ResourceLocation` of the corresponding deserializer. See the testmod's
TestIngredient for a complete implementation.
```java
Registry.register(IngredientDeserializer.REGISTRY, new ResourceLocation("example_mod", "example"), new ExampleIngredientDeserializer());
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
A JSON array can be used to specify that multiple ingredients can be valid. You can mix and match types,
and even have recursive arrays.
```json
{
  "ingredient": [
      {
          "type": "example_mod:example_2",
          "data": "goes here"
      },
      {
          "item": "minecraft:stone"
      },
      [
          {
              "type": "example_mod:example_3",
              "data": "goes here too"
          },
          {
              "type": "example_mod:example_4",
              "you_get": "the point"
          }
      ]
  ]
}
```
#### Values
Values are similar to Ingredients. You want a `ValueDeserializer` instead:
```java
Registry.register(ValueDeserializer.REGISTRY, new ResourceLocation("example_mod", "example"), new ExampleValueDeserializer());
```
And you want to implement CustomValue onto your Values.

To use custom values, specify a `value_deserializer` in a Value Json:
```json
{
  "type": "minecraft:blasting",
  "group": "iron_ingot",
  "ingredient": {
    "value_deserializer": "example_mod:example",
    "thingies": "indeed"
  },
  "result": "minecraft:dark_oak_planks",
  "experience": 0.7,
  "cookingtime": 100
}
```

Note that custom values are not as plug-and-play as ingredients; by
default, ingredients only compare values by checking if their stacks
hold the same item. For further checks, you need a custom Ingredient.
You can either have fully custom test logic, or you can implement
`CustomIngredient`'s `testCustom` as a shortcut.
