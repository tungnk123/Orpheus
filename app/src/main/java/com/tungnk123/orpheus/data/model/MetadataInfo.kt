package com.tungnk123.orpheus.data.model

data class MetadataInfo(
    val bitrate: Long? = null,
    val samplingRate: Long? = null,
    val channels: Int? = null,
    val discNumber: Int? = null,
    val discTotal: Int? = null,
    val encoder: String? = null,
    val coverFile: String? = null
)