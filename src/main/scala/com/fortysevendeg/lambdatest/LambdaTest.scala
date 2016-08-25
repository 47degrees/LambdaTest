package com.fortysevendeg.lambdatest

/**
  * Each class that contains tests should extend this trait.
  */
trait LambdaTest {

  /**
    * The collection of all tests indside the class.
    *
    * @return the LambdaAct for all tests.
    */
  def act: LambdaAct
}
