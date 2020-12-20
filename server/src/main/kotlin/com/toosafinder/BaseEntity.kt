package com.toosafinder

import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import javax.persistence.*

@MappedSuperclass
open class BaseEntity<T : Serializable> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: T? = null

    override fun equals(other: Any?): Boolean {
        other ?: return false

        if (this === other) return true

        if (javaClass != ProxyUtils.getUserClass(other)) return false

        other as BaseEntity<*>

        return this.id != null && this.id == other.id
    }

    override fun hashCode() = 0xFF

    override fun toString(): String {
        return "${javaClass.simpleName}(id=$id)"
    }

}