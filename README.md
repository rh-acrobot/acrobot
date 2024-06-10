# Acrobot

Acrobot is a Slack chatbot, with the aim of keeping track of acronyms used within Red Hat.

## Usage

Acrobot is capable of multiple actions:

* Getting an acronym explanation: `acronym`
* Saving an acronym: `!acronym = explanation`
* Modifying an acronym explanation: `!acronym = explanation => new explanation`
* Removing an acronym explanation: `!acronym = explanation =>`

Whitespace between parts of commands does not matter. Thus, `!TEST=An explanation.` and `! TEST = An explanation.` are
equivalent, but `!TE ST = An explanation.` is distinct from both.

If you are interacting with acrobot in a room, do not forget to tag the bot, e.g. `@Acrobot !acronym = explanation`.
This is not necessary in a direct message with the bot.

You can only change or remove an explanation that you have added.

## Architecture

Acrobot is a simple application, using:

* Java (tested against OpenJDK 21)
* Hibernate
* MySQL
* The Slack Bolt API

Before running the application, you will need to [create a Slack application](https://api.slack.com/apps). The app
requires the following API features:

* Socket mode (to avoid needing to expose a URL for Slack to use)
* The following scopes:
    * `app_mentions:read`: to receive messages in which the bot is mentioned
    * `chat:write`: to respond to messages
    * `im:history`: to read DMs sent to the bot
* The following event subscriptions:
    * `app_mention`: to receive messages in which the bot is mentioned
    * `message.im`: to receive DMs sent to the bot

An example App Manifest is [provided](./doc/manifest.json).

For more details on setting up the bot, see
the [Google Developer's documentation](https://developers.google.com/hangouts/chat/how-tos/pub-sub).

## Configuration

Acrobot is configured using environment variables:

* `SLACK_APP_TOKEN`: the Slack application's "app-level token", which can be found in Settings > Basic Information >
  App-Level Tokens in the application configuration (on https://api.slack.com/apps/).
* `SLACK_BOT_TOKEN`: the Slack application's OAuth token, starting with `xapp-`, which can be found in Features >
  OAuth & Permissions > OAuth Tokens for Your Workspace.
* `ACROBOT_DB_URL`: the JDBC URL used for storing data (e.g. `jdbc:mysql://localhost:3306/acrobot`, for the database
  `acrobot` on the MySQL server hosted at `localhost` on port `3306`).
* `ACROBOT_DB_USER`: the username used to authenticate to the database.
* `ACROBOT_DB_PASS`: the username used to authenticate to the database.

## Testing

Acrobot contains unit tests with an in-memory H2 database. You should execute all tests before pushing new code. You
can build and test the program using `./gradlew build`.

All new features should have tests.

## Credits

Author: Janet Cobb

This project is based on the previous [Acrobot for Google Chat](https://github.com/m-czernek/acrobot), written by Marek
Czernek. The idea of Acrobot came from a number of very smart folks at Red Hat. The IRC implementation was done
by https://github.com/mfojtik. If you wish to run AcroBot on IRC, see
the [AcroBot Implementation](https://github.com/theacrobot/AcroBot).
