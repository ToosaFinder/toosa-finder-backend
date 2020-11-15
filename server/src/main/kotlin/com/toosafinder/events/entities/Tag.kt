package com.toosafinder.events.entities

import com.toosafinder.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "tags")
class Tag(

    @Column(name = "name")
    val name: String

) : BaseEntity<Long>()

interface TagRepository: JpaRepository<Tag, Long> {

    @Query("select t.id, t.name" +
            " from events_tags et right outer join tags t on et.tag_id=t.id" +
            " group by id" +
            " order by count(id) desc limit ?1", nativeQuery = true)
    fun findTopByPopularityByOrderDesc(amount: Int): List<Tag>
}