package com.tungnk123.orpheus.utils

object AppConstants {
    /**
     * Maximum number of entries in the metadata cache.
     * Configured based on average metadata size and typical user session patterns.
     * TODO: Consider making this configurable based on device memory constraints
     */
    const val CACHE_SIZE_CONFIG = 100

}