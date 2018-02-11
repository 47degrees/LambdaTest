---
 layout: docs
 title: Actions
---

## Actions
 
 Actions are created using functions contained in 
 
    src/main/scala/com/fortysevendeg/lambdatest/package.scala
    
Additional actions can be easily defined.  
    
### Simple Actions

Simple actions do not contain other actions.

* **`assert`**. Tests a boolean predicate. See the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Example.scala) demo.
* **`assertEq`**. Tests that two values are equal. See the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Example.scala) demo.
* **`assertEx`**. Test that an exception is raised. See the [Except](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Except.scala) demo
* **`assertSC`**. Used to test a ScalaCheck property. See the [ScalaCheck](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/ScalaCheck.scala) demo.
* **`exec`**. Used to insert Scala code. See the [Mutable](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Mutable.scala) demo.
* **`assertPerf`**. Runs an expression multiple times and reports mean, max, and stdDev. Can assert a maximum mean and max. See the [Timing](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Timing.scala) demo.

### Compound Actions

Compound actions contain other actions within themselves.

* **`label`**. Introduced a labeled block of code. See the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Example.scala) demo.
* **`test`**. Defines a named test. See the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/.scala) demo.
* **`changeOptions`**. Used to change options within its body. See the [FailOnly](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/coverage/FailOnly.scala) test.
* **`timer`**. Reports the execution time of its body. See the [Timing](https://github.com/47deg/LambdaTestTiming/blob/master/src/test/scala/demo/Timing.scala) demo.
* **`assertTiming`**. Reports the execution time of its body. Fails if a max time is exceeded. See the [Timing](https://github.com/47deg/LambdaTestTiming/blob/master/src/test/scala/demo/Timing.scala) demo.
* **`nest`**. Used to nest declarations in immutable tests. See the [Immutable](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Immutable.scala) demo.

### Assertion Nesting Rule

* Assertions must be either directly or indirectly inside a test.

See [Bad](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Bad.scala) demo 
  for what not to do.
  
### Including Scala Code

Suppose we have a test that contains several assertions.
We can add arbitrary code as follows.

* **Before all assertions**. Put the code in the body of the test action. See the [Mutable](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Mutable.scala) demo.
* **Between assertions (mutable)**. Add exec actions between assertions. See the [Mutable](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Mutable.scala) demo.
* **Between assertions (immutable)**. Use nesting. See the [Immutable](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Immutable.scala) demo.
* **After all assertions**. The can be done using a user defined compound action called a wrapper. See the [Wrap](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Wrap.scala) demo. Wrappers are used instead of the before and after features of other testing frameworks. Wrappers avoid the need to use mutable state and can easily capture exceptions

  

