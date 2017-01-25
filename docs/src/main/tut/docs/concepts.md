---
 layout: docs
 title: Concepts
---

## Concepts

LambdaTest has fully functional/immutable testing state and has a rich set of features.

### Major Concepts and Classes

* **Reporter**. Used to report test results.
* **State**. All state needed to run tests is stored in objects of type `LambdaState`. State objects are immutable. 
* **Action**. Actions transform an old state to a new state and are objects of type `LambdaAct`. Each `LambdaAct` object contains an ordered list of transforms. Each transform maps an old state to a new state. A `LambdaAct` with exactly one transform is called a single action, with no transforms is call an empty action and with two or more transforms is called a multiple action. Actions can be combined using the infix `+` operator.

Actions are created using functions that create objects of type `LambdaAct`. These functions are also informally refered to as actions.

Multiple actions can also be specified as a list of actions. This is done via an implicit conversion from `Seq[LambdaAct]` to `LambdaAct`. See the [Generate](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Generate.scala) 
demo for an example that used this conversion.

### Options

Description of and defaults for options in [src/main/resources/reference.conf](https://github.com/47deg/LambdaTest/blob/master/src/main/resources/reference.conf).
The `changeOptions`action is used to change options within its body. See the [FailOnly](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/coverage/FailOnly.scala) test.


### Immutablility and Side Effects

The testing system itself contains no mutable data. It does however have one kind of side effect: tests results are output. It would have been more pure to move the output entirely after the core functionality, but it is nice to see test output incrementally as tests are run so a little purity is sacrificed.

Although LambdaTest is ideal for testing pure functional code, it can also be used to test code with mutable state and side effects. See the [Mutable](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Mutable.scala) demo.

### Exceptions

Expected exceptions can be checked using the assertEx action.

Unexpected exceptions are caught and treated as test failures. Where possible an unexpected exception will not stop later tests from being run.

### Tags

The label and test actions can have sets of tags that can be used to selectively execute only some tests.
There are options includeTags and excludeTags that can be set to select the subset of tests to be run.

By default, including the tag "ignore" on a label or test causes that action to be excluded.

See the [Tag](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Tag.scala) demo.

### Parallel Execution

The run function and the label and test actions have an optional `parallel` parameter.

By default those actions directly inside are run sequentially in order.

If parallel is true, actions directly inside are run in parallel. Note that the output still occurs in the specified order.

See the [Parallel](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Parallel.scala) demo.






