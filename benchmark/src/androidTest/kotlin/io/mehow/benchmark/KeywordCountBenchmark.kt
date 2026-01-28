package io.mehow.benchmark

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class KeywordCountBenchmark(
    factory: KeywordFactory,
) {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val daoFactory = DaoFactory.NoNotes1000000

    private val keywords = factory.create()

    @Before
    fun setup() {
        daoFactory.deleteDatabase(context)
    }

    @Test
    fun search_without_fts() {
        val dao = daoFactory.create(context)

        benchmarkRule.measureRepeated {
            val episodes = dao.searchNoFts(keywords)
            assertTrue(episodes.size < 1001)
        }

        dao.close()
    }

    @Test
    fun search_with_fts() {
        val dao = daoFactory.create(context)

        benchmarkRule.measureRepeated {
            val episodes = dao.searchFts(keywords)
            assertTrue(episodes.size < 1001)
        }

        dao.close()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun params() = KeywordFactory.entries.map { factory ->
            arrayOf(factory)
        }
    }
}

