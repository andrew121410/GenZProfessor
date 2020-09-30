package com.andrew121410.genzprofessor.manager;

import com.andrew121410.genzprofessor.GenZProfessor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandManager {

    private Map<String, ICommand> commandMap;
    private Map<String, String> shortcutMap;

    private GenZProfessor genZProfessor;
    private String prefix;

    public CommandManager(GenZProfessor genZProfessor) {
        this.genZProfessor = genZProfessor;
        this.commandMap = this.genZProfessor.getSetListMap().getCommandMap();
        this.shortcutMap = this.genZProfessor.getSetListMap().getShortcutMap();
        this.prefix = this.genZProfessor.getConfigManager().getMainConfig().getPrefix();
    }

    @SubscribeEvent
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType() == ChannelType.PRIVATE) return;

        if (!event.getMessage().getContentRaw().startsWith(prefix)) {
            return;
        }

        String arg = event.getMessage().getContentRaw();
        String[] oldArgs = arg.split(" ");
        oldArgs[0] = oldArgs[0].replace(prefix, "");
        String command = oldArgs[0].toLowerCase();
        String[] modifiedArray = Arrays.copyOfRange(oldArgs, 1, oldArgs.length);

        if (this.commandMap.containsKey(command)) {
            ICommand iCommand = this.commandMap.get(command);
            iCommand.onMessage(event, modifiedArray);
        }
    }

    public void register(ICommand iCommandManager, String command, String[] shortcuts) {
        this.commandMap.putIfAbsent(command.toLowerCase(), iCommandManager);
        if (shortcuts != null) {
            for (String shortcut : shortcuts) {
                this.shortcutMap.putIfAbsent(command.toLowerCase(), shortcut);
            }
        }
        System.out.println("CommandManager Registered: " + iCommandManager.getClass() + " ? CMD: " + command);
    }

    public static boolean hasPermission(Member member, TextChannel textChannel, Permission permission) {
        if (!member.hasPermission(permission)) {
            textChannel.sendMessage("You don't have permission to do this.").queue(a -> a.delete().queueAfter(10, TimeUnit.SECONDS));
            return false;
        }
        return true;
    }
}