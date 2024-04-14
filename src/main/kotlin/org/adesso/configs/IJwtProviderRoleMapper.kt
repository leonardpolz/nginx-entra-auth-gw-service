package org.adesso.configs

import org.adesso.dataModels.Role
import org.adesso.shared.JwtProviderRole
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring")
interface IJwtProviderRoleMapper {
    companion object {
        val instance: IJwtProviderRoleMapper = Mappers.getMapper(IJwtProviderRoleMapper::class.java)
    }
    fun toJwtProviderRoles(roles: Set<Role>): Set<JwtProviderRole>
}