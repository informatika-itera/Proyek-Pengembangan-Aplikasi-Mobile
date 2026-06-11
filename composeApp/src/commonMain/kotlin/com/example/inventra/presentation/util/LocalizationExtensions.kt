package com.example.inventra.presentation.util

import com.example.inventra.core.localization.Strings
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.model.ItemCondition

fun ItemCategory.getDisplayName(strings: Strings): String = when (this) {
    ItemCategory.ALL -> strings.catAll
    ItemCategory.MEDICAL -> strings.catMedical
    ItemCategory.FOOD -> strings.catFood
    ItemCategory.FLAG -> strings.catFlag
    ItemCategory.ELECTRONICS -> strings.catElectronics
    ItemCategory.OTHER -> strings.catOther
}

fun ItemCondition.getDisplayName(strings: Strings): String = when (this) {
    ItemCondition.NEW -> strings.condNew
    ItemCondition.GOOD -> strings.condGood
    ItemCondition.FAIR -> strings.condFair
    ItemCondition.POOR -> strings.condPoor
}
