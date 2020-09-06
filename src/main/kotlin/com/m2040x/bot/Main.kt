package com.m2040x.bot

import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.authorId
import com.jessecorbett.diskord.util.sendMessage
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import java.io.File


@UnstableDefault
suspend fun main() {
    val bonkFile = File("data/bonk.json")

    if (!bonkFile.exists()) {
        bonkFile.createNewFile()
        bonkFile.writeText("[]")
    }
    bot(config.token) {
        commands("!40X ") {
            command("help") {
                reply("No help for you")
            }
            command("ping") {
                reply("pong")
            }
        }
        reactionAdded { messageReaction ->
            val guild = messageReaction.guildId ?: return@reactionAdded
            if (messageReaction.emoji.id == config.nsfwEmojiId) {
                val channelClient =
                    clientStore.channels[messageReaction.channelId]
                val reactors =
                    channelClient.getMessageReactions(
                        messageReaction.messageId,
                        messageReaction.emoji
                    ).filter { user -> !user.isBot }
                if (reactors.size < 5) return@reactionAdded
                val admins = reactors.filter { user ->
                    val userObj = clientStore.guilds[guild].getMember(user.id)
                    config.adminRole in userObj.roleIds
                }

                if (admins.size < 3) return@reactionAdded
                val members = reactors.filter { it !in admins }
                val message =
                    channelClient.getMessage(messageReaction.messageId)
                if (message.attachments.isEmpty() && message.embeds.isEmpty()) return@reactionAdded
                channelClient.deleteMessage(messageReaction.messageId)
                val memberString = if(members.isEmpty()) "" else "${members.mention()} and "
                channelClient.sendMessage(
                    memberString + "admins ${admins.mention()} voted to delete message from <@${message.author.id}>."
                )
                val bonk = DeletionLog(
                    message.authorId,
                    message.sentAt,
                    reactors.map { user -> user.id })
                val bonks = Json.parse(
                    DeletionLog.serializer().list,
                    bonkFile.readText()
                )
                bonkFile.writeText(
                    Json.stringify(
                        DeletionLog.serializer().list,
                        bonks + bonk
                    )
                )
            }
        }
    }
}
