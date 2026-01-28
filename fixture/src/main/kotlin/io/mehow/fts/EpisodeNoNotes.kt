package io.mehow.fts

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.room.RoomRawQuery

@Entity(tableName = "episode_fts")
@Fts4(contentEntity = Episode::class)
data class EpisodeNoNotesFts(
    val title: String,
)

@Dao
abstract class EpisodeNoNotesDao : EpisodeDao {
    final override fun searchNoFts(keywords: Collection<String>): List<Episode> {
        val query = buildString {
            append("SELECT episode.*")
            append(" FROM episode")
            append(" WHERE")
            keywords.forEachIndexed { index, keyword ->
                if (index != 0) {
                    append(" OR")
                }
                append(" episode.title LIKE '%' || '$keyword' || '%'")
            }
            append(" LIMIT 1000")
        }
        return getEpisodesUnsafe(RoomRawQuery(query))
    }

    final override fun searchFts(keywords: Collection<String>): List<Episode> {
        val searchTerm = keywords.joinToString(separator = " OR ") { "\"$it\"" }
        return searchFts(searchTerm)
    }

    @Query(
        """
        SELECT episode.*
        FROM episode
        JOIN episode_fts ON episode_fts.rowid = episode.rowid
        WHERE episode_fts MATCH :searchTerm
        LIMIT 1000
    """
    )
    internal abstract fun searchFts(searchTerm: String): List<Episode>

    @RawQuery
    internal abstract fun getEpisodesUnsafe(query: RoomRawQuery): List<Episode>
}

@Database(
    version = 1,
    exportSchema = false,
    entities = [Episode::class, EpisodeNoNotesFts::class],
)
abstract class DatabaseNoNotes : RoomDatabase() {
    abstract fun episodeDao(): EpisodeNoNotesDao
}
