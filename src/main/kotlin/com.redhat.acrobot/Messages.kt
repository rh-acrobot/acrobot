package com.redhat.acrobot

import com.redhat.acrobot.CommandFormat.ACRONYM_SEPARATOR
import com.redhat.acrobot.CommandFormat.CHANGE_PREFIX
import com.redhat.acrobot.CommandFormat.UPDATE_EXPLANATION_SEPARATOR

object Messages {
    const val INCORRECT_FORMAT_FOR_SAVING_ACRONYM: String =
        "Please enter the acronym in format of `${CHANGE_PREFIX}[acronym] $ACRONYM_SEPARATOR [explanation]`" +
                " to save an explanation, or only `acronym` to get an explanation. Alternatively, send `help` for more information."

    const val EXPLANATION_SAVED: String = "I have saved your explanation. Thank you!"
    const val EXPLANATION_UPDATED: String = "I have updated the acronym. Thank you!"
    const val EXPLANATION_REMOVED: String = "Explanation removed. Thank you!"

    const val EXPLANATION_NOT_FOUND: String =
        "I did not find any matching explanation for the given acronym. Please double-check the acronym and your spelling."

    const val ACRONYM_NOT_FOUND: String =
        "I was unable to find that acronym. Write `help` for information on how to add acronyms."

    const val INSUFFICIENT_PRIVILEGES: String = "You cannot update acronyms that you did not save. Aborting!"

    const val MULTIPLE_UPDATE_SEPARATORS =
        "Your message contains multiple `$UPDATE_EXPLANATION_SEPARATOR`s, so I don't know how to parse it. Please do not include `$UPDATE_EXPLANATION_SEPARATOR` in your acronym or explanation, or you can write `help` for more informtation."

    const val HELP_TEXT: String = "You are interacting with Acrobot. \n\n" +
            "Actions:\n" +
            "*Get an acronym explanation:* `@Acrobot acronym` \n" +
            "*Insert an acronym:* `@Acrobot !acronym=explanation` \n" +
            "*Change old explanation to a new one:* `@Acrobot !acronym=old explanation => new explanation` \n" +
            "*Delete old explanation:* `@Acrobot !acronym = old explanation =>` \n" +
            "You can add an explanation to an already existing acronym the same way as inserting. \n" +
            "All of the actions work in a direct message without tagging `@Acrobot`. Whitespaces shouldn't matter," +
            "and you can input acronym in both lower- and uppercase; it will be matched regardless of the capitalisation. \n\n" +
            "Acrobot is implemented by Marek Czernek. You can find documentation and file issues or suggest improvements at " +
            "https://github.com/m-czernek/acrobot"
}
