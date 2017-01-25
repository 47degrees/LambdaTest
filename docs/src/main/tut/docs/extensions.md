---
 layout: docs
 title: Extensions
---

## Extensions

### Extensibility 

LambdaTest is designed to be easily extensible.

* **Test generation**. The clean simple design of LambdaTest makes generation of tests easy. See the [Generate](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Generate.scala) demo for some simple examples.
* **New actions**. New actions are easily defined. See the code in [package.scala](https://github.com/47deg/LambdaTest/blob/master/src/main/scala/com/fortysevendeg/lambdatest/package.scala) for examples. Actions typically use the methods of `LambdaState` and the eval method of `LambdaAct`. These methods should not be called directly in user test code. Wrappers are one kind of user defined action (see the [Wrap](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Wrap.scala) demo).
* **Alternate reporters**. Reporters collect and display the results of tests. Reporters are included for both StdOut (for tests run directly) and SBT (for tests run using the SBT test command). Custom reporters can created by extending the `LambdaReporter` trait.


### Current Extensions

* The [LambdaTestTiming](https://github.com/47deg/LambdaTestTiming) extension provides actions testing execution times.
* The [LambdaTestAsync](https://github.com/47deg/LambdaTestAsync) extension provides actions for testing Akka actors and logs.

