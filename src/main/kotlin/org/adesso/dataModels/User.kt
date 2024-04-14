package org.adesso.dataModels

import jakarta.persistence.*
import java.util.*
import javax.validation.constraints.Email

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID,

    @Email
    val email: String,

    @ManyToMany(fetch = FetchType.EAGER)
    val roles: Set<Role>
)