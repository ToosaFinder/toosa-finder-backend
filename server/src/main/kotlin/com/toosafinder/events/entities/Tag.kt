package com.toosafinder.events.entities

import com.toosafinder.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "tags")
class Tag(

    @Column(name = "name")
    val name: String

) : BaseEntity<Long>()

interface TagRepository: JpaRepository<Tag, Long>