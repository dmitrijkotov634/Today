package com.wavecat.today.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.wavecat.today.R

object TodayWidget : GlanceAppWidget() {

    val suggestion = stringPreferencesKey("suggestion")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val suggestion = currentState(suggestion) ?: ""
            MainContent(suggestion)
        }
    }

    @Composable
    private fun MainContent(suggestion: String) {
        Box(modifier = GlanceModifier.fillMaxHeight()) {
            Row(
                modifier = GlanceModifier
                    //  .clickable(actionStartActivity<MainActivity>())
                    .background(GlanceTheme.colors.primaryContainer)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.baseline_auto_awesome_24),
                    modifier = GlanceModifier.size(20.dp),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimaryContainer),
                    contentDescription = "Suggestion"
                )

                Spacer(modifier = GlanceModifier.width(8.dp))

                Text(
                    text = suggestion,
                    style = TextStyle().copy(color = GlanceTheme.colors.onPrimaryContainer)
                )
            }
        }
    }
}
