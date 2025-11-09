package com.wavecat.today

object Constant {
    const val TAG = "Today"

    const val GROQ_ENDPOINT = "https://api.groq.com/openai/v1/chat/completions"
    const val OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions"
    const val TEST_ENDPOINT = "https://today-gamma.vercel.app/api/tomorrow"

    const val DEFAULT_MODEL = "gpt-4.1-mini"
    const val DEFAULT_PREFERENCES = "default"

    const val DEFAULT_GROQ_MODEL = "llama-3.3-70b-versatile"
    const val DEFAULT_GROQ_KEY = "gs" + "k_KTxe18eGycE32KbhuZRTWGdyb3FYHSNMrXC5nA58MUBl7uRAgBg1"

    const val BASE_PROMPT =
        "Analyze the user's notifications and write a wish to the user. Write briefly, do not use line breaks. Current time - %DATETIME%.\n" +
                "Notifications, if any:\n" +
                "%NOTIFICATIONS%\n"

    const val NOTIFICATIONS_VAR = "%NOTIFICATIONS%"
    const val SCREEN_CONTENT_VAR = "%SCREENCONTENT%"
    const val DATE_AND_TIME_VAR = "%DATETIME%"
    const val BATTERY_LEVEL_VAR = "%BATTERYLEVEL%"

    val variables = listOf(
        NOTIFICATIONS_VAR,
        SCREEN_CONTENT_VAR,
        BATTERY_LEVEL_VAR,
        DATE_AND_TIME_VAR
    )
}