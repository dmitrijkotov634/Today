package com.wavecat.today.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.wavecat.today.DataRepository
import com.wavecat.today.MainActivity
import com.wavecat.today.R

object TodayWidget : GlanceAppWidget() {

    val suggestion = stringPreferencesKey("suggestion")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val suggestion = currentState(suggestion) ?: DataRepository(context).suggestion
            MainContent(suggestion)
        }
    }

    @Composable
    private fun MainContent(suggestion: String) {
        Row(modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())) {
            Image(
                provider = ImageProvider(R.drawable.baseline_auto_awesome_24),
                modifier = GlanceModifier.size(20.dp),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
                contentDescription = "Suggestion"
            )

            Spacer(modifier = GlanceModifier.width(6.dp))

            Text(
                text = suggestion,
                style = TextStyle().copy(color = GlanceTheme.colors.onBackground)
            )
        }
    }
}
