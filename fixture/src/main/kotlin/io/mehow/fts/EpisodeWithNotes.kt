package io.mehow.fts

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomDatabase
import androidx.room.RoomRawQuery

@Entity(tableName = "episode_metadata")
data class EpisodeWithNotesMetadata(
    @PrimaryKey val uuid: String,
    val title: String,
    val notes: String,
)

@Entity(tableName = "episode_metadata_fts")
@Fts4(contentEntity = EpisodeWithNotesMetadata::class)
data class EpisodeWithNotesMetadataFts(
    val title: String,
    val notes: String,
)

@Dao
abstract class EpisodeWithNotesDao : EpisodeDao {
    final override fun searchNoFts(keywords: Collection<String>): List<Episode> {
        val query = buildString {
            append("SELECT episode.*")
            append(" FROM episode")
            append(" JOIN episode_metadata ON episode_metadata.uuid = episode.uuid")
            append(" WHERE")
            keywords.forEachIndexed { index, keyword ->
                if (index != 0) {
                    append(" OR")
                }
                append(" episode_metadata.title LIKE '%' || '$keyword' || '%'")
                append(" OR episode_metadata.notes LIKE '%' || '$keyword' || '%'")
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
        JOIN episode_metadata ON episode_metadata.uuid = episode.uuid
        JOIN episode_metadata_fts ON episode_metadata_fts.rowid = episode_metadata.rowid
        WHERE episode_metadata_fts MATCH :searchTerm
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
    entities = [Episode::class, EpisodeWithNotesMetadata::class, EpisodeWithNotesMetadataFts::class],
)
abstract class DatabaseWithNotes : RoomDatabase() {
    abstract fun episodeDao(): EpisodeWithNotesDao
}
