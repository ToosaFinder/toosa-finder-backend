package com.toosafinder.security.entities

import com.toosafinder.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "roles")
class Role(

        @Column(name = "name")
        val name: String

) : BaseEntity<Long>() {

        enum class Name {
                USER,
                UNCONFIRMED
        }

}

internal interface RoleRepository: JpaRepository<Role, Long> {
        fun findByName(name: String): Role?
}