package com.example.stylecraft.data.mapper

import com.example.stylecraft.data.local.entity.ClothingItemEntity
import com.example.stylecraft.domain.model.ClothingCategory
import com.example.stylecraft.domain.model.ClothingColor
import com.example.stylecraft.domain.model.ClothingItem
import com.example.stylecraft.domain.model.Fit
import com.example.stylecraft.domain.model.Pattern

fun ClothingItemEntity.toDomain(): ClothingItem = ClothingItem(
    id = id,
    name = name,
    category = ClothingCategory.valueOf(category),
    primaryColor = ClothingColor.valueOf(primaryColor),
    secondaryColor = secondaryColor?.let { ClothingColor.valueOf(it) },
    pattern = Pattern.valueOf(pattern),
    fit = Fit.valueOf(fit),
    formality = formality,
    imageUri = imageUri,
    createdAt = createdAt
)

fun ClothingItem.toEntity(): ClothingItemEntity = ClothingItemEntity(
    id = id,
    name = name,
    category = category.name,
    primaryColor = primaryColor.name,
    secondaryColor = secondaryColor?.name,
    pattern = pattern.name,
    fit = fit.name,
    formality = formality,
    imageUri = imageUri,
    createdAt = createdAt
)

