package com.andrew121410.genzprofessor.commands;

import com.andrew121410.genzprofessor.GenZProfessor;
import com.andrew121410.genzprofessor.commands.manager.ICommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CheggCMD implements ICommand {

    private GenZProfessor genZProfessor;

    public CheggCMD(GenZProfessor genZProfessor) {
        this.genZProfessor = genZProfessor;
        this.genZProfessor.getCommandManager().register(this, "c");
    }

    @Override
    public boolean onMessage(MessageReceivedEvent event, String[] args) {

        if (args.length == 0) {
            event.getTextChannel().sendMessage("I don't understand?").queue();
        } else if (args.length == 1) {
            this.genZProfessor.getQueueManager().add(event.getAuthor(), args[0]);
        }
        return false;
    }
}
