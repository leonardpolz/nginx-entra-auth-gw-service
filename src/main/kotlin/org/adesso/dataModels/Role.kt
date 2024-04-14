package org.adesso.dataModels

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*


@Entity
@Table(name = "roles")
data class Role (
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true)
    val name: String,
)