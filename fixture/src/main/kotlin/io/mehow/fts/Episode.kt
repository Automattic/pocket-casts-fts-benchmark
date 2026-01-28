package io.mehow.fts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode")
data class Episode(
    @PrimaryKey val uuid: String,
    val title: String,
)

interface EpisodeDao {
    fun searchNoFts(keywords: Collection<String>): List<Episode>

    fun searchFts(keywords: Collection<String>): List<Episode>
}
