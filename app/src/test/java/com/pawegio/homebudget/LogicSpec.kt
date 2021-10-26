package com.pawegio.homebudget

import com.pawegio.homebudget.util.InstantTaskUtils
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

@Suppress("UNCHECKED_CAST")
abstract class LogicSpec constructor(body: LogicSpec.() -> Unit) : FreeSpec(body as FreeSpec.() -> Unit) {

    private val logicCoroutineContext: CoroutineContext
        get() = Dispatchers.Unconfined + logicJob

    private val logicJob = Job()

    val logicScope = CoroutineScope(logicCoroutineContext)

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        InstantTaskUtils.beforeTest()
    }

    override fun afterSpec(spec: Spec) {
        InstantTaskUtils.afterTest()
        super.afterSpec(spec)
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        logicJob.cancel()
        super.afterTest(testCase, result)
    }

    override fun isolationMode() = IsolationMode.InstancePerLeaf
}
