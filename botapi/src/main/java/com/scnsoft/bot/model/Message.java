package com.scnsoft.bot.model;

/**
 * The message that the custome bot is working with
 */
public record Message(
    String data,
    String attachments
) {
}
