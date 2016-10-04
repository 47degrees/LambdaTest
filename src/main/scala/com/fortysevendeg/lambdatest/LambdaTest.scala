package com.fortysevendeg.lambdatest

/**
  * Each class (to be run by the run method but not SBT test)
  * that contains tests should extend this trait.
  */
trait LambdaTestRun {

  /**
    * The collection of all tests indside the class.
    *
    * @return the LambdaAct for all tests.
    */
  def act: LambdaAct
}

/**
  * Each class (to be run by either SBT test on the run method)
  * that contains tests should extend this trait.
  */
trait LambdaTest extends LambdaTestRun
