package com.wavecat.today

object Constant {
    const val OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions"
    const val TEST_ENDPOINT = "https://today-gamma.vercel.app/api/today"

    const val GPT_3_5_TURBO = "gpt-3.5-turbo"

    const val BASE_PROMPT = "Now - %DATETIME%\n" +
            "Create a hint based on user notifications:\n" +
            "%NOTIFICATIONS%"
}