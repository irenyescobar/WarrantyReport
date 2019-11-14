package com.ireny.randon.frasle.warrantyreport.entites

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["reportId", "technicalAdviceId"],
    foreignKeys = [
        ForeignKey(
            entity = Report::class,
            parentColumns = ["id"],
            childColumns = ["reportId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TechnicalAdvice::class,
            parentColumns = ["id"],
            childColumns = ["technicalAdviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["reportId"]),
        Index(value = ["technicalAdviceId"])
    ]
)
class AssignedTechnicalAdvice(
    var reportId: Long = 0,
    var technicalAdviceId: Int = 0
)
