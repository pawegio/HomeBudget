package com.pawegio.homebudget

import io.kotlintest.IsolationMode
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.specs.AbstractFreeSpec
import io.kotlintest.specs.FreeSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

@Suppress("UNCHECKED_CAST")
abstract class FlowSpec constructor(body: FlowSpec.() -> Unit) :
    FreeSpec(body as AbstractFreeSpec.() -> Unit), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Unconfined + job

    private val job = Job()

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        InstantTaskUtils.beforeTest()
    }

    override fun afterSpec(spec: Spec) {
        InstantTaskUtils.afterTest()
        super.afterSpec(spec)
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        job.cancel()
        super.afterTest(testCase, result)
    }

    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
