package org.adesso.repositories

import org.adesso.dataModels.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface IUserRepository : JpaRepository<User, UUID>
