package com.redhat.acrobot

import com.redhat.acrobot.CommandFormat.ACRONYM_EXPLANATION_SEPARATOR
import com.redhat.acrobot.CommandFormat.COMMAND_PREFIX
import com.redhat.acrobot.CommandFormat.EXPLANATION_REPLACEMENT_SEPARATOR
import com.redhat.acrobot.entities.Explanation

object Messages {
    const val INCORRECT_FORMAT_FOR_SAVING_ACRONYM: String =
        "Please enter the acronym in format of `${COMMAND_PREFIX}[acronym] $ACRONYM_EXPLANATION_SEPARATOR [explanation]`" +
                " to save an explanation, or only `acronym` to get an explanation. Alternatively, send `help` for more information."

    const val EXPLANATION_SAVED: String = "I have saved your explanation. Thank you!"
    const val EXPLANATION_UPDATED: String = "I have updated the acronym. Thank you!"
    const val EXPLANATION_REMOVED: String = "Explanation removed. Thank you!"

    const val EXPLANATION_TOO_LONG: String =
        "Sorry, that explanation is too long. Please limit explanations to ${Explanation.MAX_EXPLANATION_LENGTH} characters."

    const val EXPLANATION_NOT_FOUND: String =
        "I did not find any matching explanation for the given acronym. Please double-check the acronym and your spelling."

    const val ACRONYM_NOT_FOUND: String =
        "I was unable to find that acronym. Write `help` for information on how to add acronyms."

    const val INSUFFICIENT_PRIVILEGES: String = "You cannot update acronyms that you did not save. Aborting!"

    const val MULTIPLE_UPDATE_SEPARATORS =
        "Your message contains multiple `$EXPLANATION_REPLACEMENT_SEPARATOR`s, so I don't know how to parse it. Please do not include `$EXPLANATION_REPLACEMENT_SEPARATOR` in your acronym or explanation, or you can write `help` for more informtation."

    const val AUTHOR_NO_EXPLANATIONS = "You have not added any explanations."

    const val AUTHOR_CONFIRM_DELETE_EXPLANATIONS =
        "Use `@Acrobot ${COMMAND_PREFIX}my_explanations delete confirm` to confirm this action. If you do this, **all explanations you have added will be permanently removed**."

    const val AUTHOR_EXPLANATIONS_DELETED = "All of your explanations have been deleted."

    const val HELP_TEXT: String = """You are interacting with Acrobot. 
Actions:
*Get an acronym explanation:* `@Acrobot acronym` 
*Insert an acronym:* `@Acrobot ${COMMAND_PREFIX}acronym${ACRONYM_EXPLANATION_SEPARATOR}explanation` 
*Change old explanation to a new one:* `@Acrobot ${COMMAND_PREFIX}acronym${ACRONYM_EXPLANATION_SEPARATOR}old explanation $EXPLANATION_REPLACEMENT_SEPARATOR new explanation` 
*Delete old explanation:* `@Acrobot ${COMMAND_PREFIX}acronym ${ACRONYM_EXPLANATION_SEPARATOR} old explanation ${EXPLANATION_REPLACEMENT_SEPARATOR}`
*List your explanations:* `@Acrobot ${COMMAND_PREFIX}my_explanations`
*Delete all of your explanations:* `@Acrobot ${COMMAND_PREFIX}my_explanations delete`
You can add an explanation to an already existing acronym the same way as inserting. 
All of the actions work in a direct message without tagging `@Acrobot`. Whitespace doesn't matter,and acronyms are matched without regard to case. 

Acrobot is implemented by Janet Cobb. You can find documentation and file issues or suggest improvements at https://github.com/randomnetcat/acrobot

Your use of Acrobot is governed by the Red Hat Employee Privacy Statement, available at https://source.redhat.com/departments/legal/global_legal_compliance/compliance_folder/employee_personal_information_privacy_statement_pdfpdf"""
}
