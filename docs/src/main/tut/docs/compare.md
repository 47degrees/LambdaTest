---
 layout: docs
 title: Compare with ScalaTest and Specs2
---

## Comparison with ScalaTest and Specs2

| Feature                | LambdaTest      | ScalaTest    | Specs2       |
| ---------------------- | --------------- | ------------ | ------------ |
| lines of code          |  ~1K            | ~ 400K       |  ~50K        |
| written in             | Scala           | Scala/Java   | Scala/Java   |
| functional/immutable   | yes             | no           | no           | 
| easy to customize      | yes             | no           | no           |
| easy to generate tests | yes             | no           | no           |
| wrap code              | wrappers        | before/after | before/after |
| supports ScalaCheck    | yes             | yes          | yes          |
| tagged/ignored tests   | yes             | yes          | yes          |
| timed tests            | yes Note 1      | yes          | no           |
| performance tests      | yes Note 1      | no           | no           |
| major dependencies     | only ScalaCheck | lots         | lots         |
| actor message tests    | yes Note 2      | no           | no           |
| log message tests      | yes Note 2      | no           | no           |


LambdaTest has a simple clean fully functional/immutable API that makes it
easy to define new kinds of assertions and compound testing forms (such as wrappers). 
This simple API also provides the option of having code that generates 
an entire test suite rather than having to write each test manually.

Notes
* Note 1. Via [LambdaTestTiming](https://github.com/47deg/LambdaTestTiming) extension.
* Note 2. Via [LambdaTestAsync](https://github.com/47deg/LambdaTestAsync) extension.

Wrappers offer many advantages over use of before and after.

* Single function (wrapper) rather than 2 (before and after).
* Does not require mutable state.
* Can catch exceptions.
* Can hide state needed both before and after.
* Can be applied at any level.

All these features are discussed in this documentation with links to demo code that illustrate the
features.