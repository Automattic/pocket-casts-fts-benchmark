package io.mehow.benchmark

import android.content.Context
import androidx.room.Room
import io.mehow.fts.DatabaseNoNotes
import io.mehow.fts.DatabaseWithNotes
import io.mehow.fts.EpisodeDao
import java.io.Closeable

enum class DaoFactory(
    private val hasShowNotes: Boolean,
    private val episodeCount: Int,
) {
    NoNotes1000(
        hasShowNotes = false,
        episodeCount = 1_000,
    ),
    NoNotes10000(
        hasShowNotes = false,
        episodeCount = 10_000,
    ),
    NoNotes100000(
        hasShowNotes = false,
        episodeCount = 100_000,
    ),
    NoNotes1000000(
        hasShowNotes = false,
        episodeCount = 1_000_000,
    ),
    WithNotes1000(
        hasShowNotes = true,
        episodeCount = 1_000,
    ),
    WithNotes10000(
        hasShowNotes = true,
        episodeCount = 10_000,
    ),
    WithNotes100000(
        hasShowNotes = true,
        episodeCount = 100_000,
    ),
    WithNotes1000000(
        hasShowNotes = true,
        episodeCount = 1_000_000,
    ),
    ;

    private val dbName
        get() = buildString {
            append(if (hasShowNotes) "with_notes" else "no_notes")
            append("_$episodeCount.db")
        }

    fun create(context: Context): CloseableEpisodeDao {
        val (database, dao) = if (hasShowNotes) {
            val database = Room.databaseBuilder(context, DatabaseWithNotes::class.java, dbName)
                .createFromAsset(dbName)
                .build()
            database to database.episodeDao()
        } else {
            val database = Room.databaseBuilder(context, DatabaseNoNotes::class.java, dbName)
                .createFromAsset(dbName)
                .build()
            database to database.episodeDao()
        }
        return object : CloseableEpisodeDao {
            override fun searchNoFts(keywords: Collection<String>) = dao.searchNoFts(keywords)

            override fun searchFts(keywords: Collection<String>) = dao.searchFts(keywords)

            override fun close() = database.close()
        }
    }

    fun deleteDatabase(context: Context) {
        context.deleteDatabase(dbName)
    }

    override fun toString(): String {
        return "episodeCount=$episodeCount, hasShowNotes=$hasShowNotes"
    }
}

interface CloseableEpisodeDao : EpisodeDao, Closeable

enum class KeywordFactory(
    private val count: Int,
) {
    Keywords1(1),
    Keywords2(2),
    Keywords5(5),
    Keywords10(10),
    Keywords20(20),
    Keywords30(30),
    Keywords40(40),
    Keywords50(50),
    ;

    fun create() = keywords.take(count)

    override fun toString(): String {
        return "keywordCount=$count"
    }
}

val keywords = listOf(
    "How",
    "Russel",
    "21",
    "Vir",
    "Activities",
    "Hepler",
    "Cabeças",
    "Moscou",
    "Orkan",
    "say",
    "Booty",
    "Goofs",
    "convergence",
    "Autonomy",
    "YOLA",
    "Sowing",
    "russisches",
    "Rambão",
    "Ärztekonzert",
    "Cravings",
    "Özden",
    "Diaby",
    "Vos",
    "Snowdon",
    "Sohn",
    "WHISTLE",
    "STRATEGY",
    "África",
    "Brilhar",
    "KALER",
    "reward",
    "lending",
    "Trolled",
    "dus",
    "reading",
    "Kunststoff",
    "Huck",
    "trará",
    "Süchtig",
    "meltdown",
    "munitions",
    "Siska",
    "Always",
    "Threadguy",
    "Persuasion",
    "Wimps",
    "2º",
    "Close",
    "escalofriantes",
    "Snakker",
)
