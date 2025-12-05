package com.example.myapplication.util

/**
 * Runtime-safe accessor for the generated BuildConfig constants.
 * Uses reflection so code doesn't have a compile-time dependency on the generated BuildConfig class
 * (which may not be available in some IDE/code-analysis contexts). At runtime, if the generated
 * BuildConfig exists it will return the build-time value; otherwise it falls back to an empty string.
 */
object BuildConfigProvider {
    val NEWS_API_KEY: String
        get() = try {
            val clazz = Class.forName("com.example.myapplication.BuildConfig")
            val field = clazz.getDeclaredField("NEWS_API_KEY")
            field.get(null) as String
        } catch (_: Exception) {
            // Fall back to empty string if BuildConfig isn't available at compile time
            ""
        }
}
