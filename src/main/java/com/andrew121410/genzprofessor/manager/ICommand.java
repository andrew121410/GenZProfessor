package com.andrew121410.genzprofessor.manager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
    boolean onMessage(MessageReceivedEvent event, String prefix, String[] args);
}
