package com.example.stylecraft.data.mapper

import com.example.stylecraft.data.local.entity.UserEntity
import com.example.stylecraft.domain.model.Aesthetic
import com.example.stylecraft.domain.model.BodyShape
import com.example.stylecraft.domain.model.ColorSeason
import com.example.stylecraft.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    bodyShape = BodyShape.valueOf(bodyShape),
    colorSeason = ColorSeason.valueOf(colorSeason),
    aesthetics = aesthetics.split(",").filter { it.isNotBlank() }.map { Aesthetic.valueOf(it.trim()) },
    createdAt = createdAt
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    bodyShape = bodyShape.name,
    colorSeason = colorSeason.name,
    aesthetics = aesthetics.joinToString(",") { it.name },
    createdAt = createdAt
)

