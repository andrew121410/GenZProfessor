package com.andrew121410.genzprofessor.manager;

import com.andrew121410.genzprofessor.GenZProfessor;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.Arrays;
import java.util.Map;

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

        if (this.commandMap.containsKey(command) || this.shortcutMap.containsKey(command)) {
            String prefix = command;
            if (this.shortcutMap.containsKey(command)) prefix = this.shortcutMap.get(command);
            ICommand iCommand = this.commandMap.get(prefix);
            iCommand.onMessage(event, command, modifiedArray);
        }
    }

    public void register(ICommand iCommandManager, String command, String[] shortcuts) {
        this.commandMap.putIfAbsent(command.toLowerCase(), iCommandManager);
        if (shortcuts != null) {
            for (String shortcut : shortcuts) {
                this.shortcutMap.putIfAbsent(shortcut, command.toLowerCase());
            }
        }
        System.out.println("CommandManager Registered: " + iCommandManager.getClass() + " ? CMD: " + command);
    }
}
