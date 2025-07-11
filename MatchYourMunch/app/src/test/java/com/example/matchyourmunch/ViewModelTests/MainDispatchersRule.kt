package com.example.matchyourmunch.ViewModelTests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * ersetzt den MainDispatcher durch einen neuen TestDispatcher
 * @param dispatcher der zu verwendene TestDispatcher
 */
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    /**
     * wird vor jedem Test aufgerufen
     * setzt den MainDispatcher auf den übergebenen TestDispatcher
     * @param description die Beschreibung des Tests
     */
    override fun starting(description: Description?) {
        Dispatchers.setMain(dispatcher)
    }

    /**
     * wird nach jedem Test aufgerufen
     * setzt den MainDispatcher zurück auf den Standardwert
     * @param description die Beschreibung des Tests
     */
    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}
