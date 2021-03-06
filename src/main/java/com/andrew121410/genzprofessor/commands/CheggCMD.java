package com.andrew121410.genzprofessor.commands;

import com.andrew121410.genzprofessor.GenZProfessor;
import com.andrew121410.genzprofessor.manager.ICommand;
import com.andrew121410.genzprofessor.objects.CheggRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class CheggCMD implements ICommand {

    private GenZProfessor genZProfessor;

    public CheggCMD(GenZProfessor genZProfessor) {
        this.genZProfessor = genZProfessor;
        this.genZProfessor.getCommandManager().register(this, "c", new String[]{"chegg"});
    }

    @Override
    public boolean onMessage(MessageReceivedEvent event, String prefix, String[] args) {
        if (event.getGuild().getId().equals("760507288531763210")) {
            boolean hasDonatorRole = event.getMember().getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("Donator"));
            TextChannel freeChannel = event.getGuild().getTextChannelById("760512271741878392");
            TextChannel paidChannel = event.getGuild().getTextChannelById("764308806242402315");
            if (!hasDonatorRole) {
                if (!event.getTextChannel().getId().equals("760512271741878392")) {
                    event.getMessage().delete().queue();
                    event.getTextChannel().sendMessage("Wrong channel please use " + freeChannel.getAsMention() + " " + event.getAuthor().getAsMention()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    return true;
                }
            } else {
                if (!event.getTextChannel().getId().equals("764308806242402315")) {
                    event.getMessage().delete().queue();
                    event.getTextChannel().sendMessage("Wrong channel please use " + paidChannel.getAsMention() + " " + event.getAuthor().getAsMention()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                    return true;
                }
            }
        }

        if (args.length == 0) {
            event.getTextChannel().sendMessage("Usage: " + this.genZProfessor.getConfigManager().getMainConfig().getPrefix() + prefix + " <Link>").queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
            event.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
        } else if (args.length == 1) {
            String message = args[0];

            if (!message.startsWith("https://www.chegg.com/homework-help")) {
                event.getTextChannel().sendMessage("Sorry that doesn't look like a valid link").queue(message1 -> message1.delete().queueAfter(30, TimeUnit.SECONDS));
                event.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
                return true;
            }

            this.genZProfessor.getQueueManager().add(new CheggRequest(event.getGuild().getId(), event.getAuthor().getId(), message));
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setAuthor("Chegg Answers")
                    .setThumbnail("https://i.pinimg.com/originals/70/a5/52/70a552e8e955049c8587b2d7606cd6a6.gif")
                    .setTitle("Your Chegg request is being processed.")
                    .setDescription("Your request has been placed into the queue \r\n You are number " + this.genZProfessor.getQueueManager().getSize()
                            + "\r\n " + "\r\nOnce we are done processing your request we will send you a DM " + event.getMember().getAsMention());
            event.getTextChannel().sendMessage(embedBuilder.build()).queue(message1 -> message1.delete().queueAfter(30, TimeUnit.SECONDS));
            event.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
        }
        return false;
    }
}
