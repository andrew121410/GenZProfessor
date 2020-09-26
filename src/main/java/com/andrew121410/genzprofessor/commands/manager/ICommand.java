package com.andrew121410.genzprofessor.commands.manager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
    boolean onMessage(MessageReceivedEvent event, String[] args);
}
