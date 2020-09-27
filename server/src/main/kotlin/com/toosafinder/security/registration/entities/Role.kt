package com.toosafinder.security.registration.entities

import com.toosafinder.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
@Table(name = "roles")
internal class Role(

        @Column(name = "name")
        val name: String

) : BaseEntity<Long>()

internal interface RoleRepository: JpaRepository<Role, Long>