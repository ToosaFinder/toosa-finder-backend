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

    @Query("select t" +
            " from Event e right outer join e.tags t" +
            " group by t" +
            " order by count(t) desc")
    fun findAllByOrderByPopularityDesc(): List<Tag>
}