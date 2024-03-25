package com.wavecat.today.worker

import com.knuddels.jtokkit.api.Encoding
import com.wavecat.today.worker.models.Message

fun trimMessages(
    encoding: Encoding,
    inputMessages: List<Message>,
    tokenLimit: Int
): List<Message> = buildList {
    var numTokens = 0

    for (message in inputMessages.reversed()) {
        numTokens += 6
        numTokens += encoding.encode(message.role).size
        numTokens += encoding.encode(message.content).size

        if (numTokens > tokenLimit) {
            if (isEmpty())
                add(
                    message.copy(
                        content = encoding.decode(
                            encoding.encode(message.content)
                                .take(tokenLimit)
                        )
                    )
                )

            break
        }

        add(message)
    }
}.reversed()