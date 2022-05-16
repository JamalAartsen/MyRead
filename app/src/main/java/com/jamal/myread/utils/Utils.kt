package com.jamal.myread

import android.content.Context
import android.content.res.Resources
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "voice_Settings")
val Context.dataStoreOnBoarding: DataStore<Preferences> by preferencesDataStore(name = "on_boarding")

/**
 * Sets the sizes of the textviews and image of the onboarding screen to an amount if the screen
 * height is smaller than 430
 *
 * @author Jamal Aartsen
 */
fun setSizesOnBoarding(resources: Resources): Sizes? {
    val heightDp = resources.displayMetrics.run { heightPixels / density }

    if (heightDp < 430) {
        val title = resources.getDimension(R.dimen.text_size_on_boarding_titel)
        val description =
            resources.getDimension(R.dimen.text_size_title_onboarding_smaller_than_430)
        val image = resources.getDimensionPixelSize(R.dimen.image_witdh_smaller_than_430)

        return Sizes(title, description, image)
    }

    return null
}

data class Sizes(
    val titleSize: Float,
    val descriptionSize: Float,
    val imageSize: Int
)