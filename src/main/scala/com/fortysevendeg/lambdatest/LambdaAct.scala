package com.fortysevendeg.lambdatest

/**
  * A sequence of tests.
  *
  * @param acts a sequence of the state changes that occur when running each test.
  */
case class LambdaAct(acts: Seq[LambdaState => LambdaState]) {
  /**
    * Combines two LambdaAct's.
    *
    * @param act
    * @return the new LambdaAct.
    */
  def +(act: LambdaAct): LambdaAct = {
    LambdaAct(this.acts ++ act.acts)
  }

  /**
    * Executes the tests inside. This method should not be called inside the actual tests
    * (those that extend LambdaTest).
    * Instead, it can be called directly inside new test actions (that have type LambdaAct).
    *
    * @param t the initial state before running the test.
    * @param parallel  if false, tests are run in order; if true, tests are run in parallel.
    *                  In either case the output for the test will apprear in order.
    * @return  the final state after running all tests.
    */
  def eval(t: LambdaState, parallel: Boolean = false): LambdaState = {
    t.eval(this.acts, parallel)
  }

}
