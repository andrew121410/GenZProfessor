package com.andrew121410.genzprofessor.manager;

import com.andrew121410.genzprofessor.GenZProfessor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueManager {

    private Queue<Request> requestQueue;

    private boolean running;
    private GenZProfessor genZProfessor;

    private CheggRequest cheggRequest;
    private ScheduledExecutorService queueService;

    public QueueManager(GenZProfessor genZProfessor) {
        this.genZProfessor = genZProfessor;
        this.requestQueue = new LinkedList<>();
    }

    public void setup() {
        this.cheggRequest = new CheggRequest();
        this.cheggRequest.start();
        setupQueue();
        running = false;
    }

    public void quit() {
        this.requestQueue.clear();
        this.queueService.shutdown();
    }

    public void add(Guild guild, User user, String link) {
        this.requestQueue.add(new Request(guild.getId(), user.getId(), link));
    }

    private void setupQueue() {
        Runnable runnable = () -> {
            if (!this.running && !this.requestQueue.isEmpty()) {
                this.running = true;
                Request request = this.requestQueue.remove();
                if (request == null) {
                    this.running = false;
                    return;
                }
                System.out.println("Processing request: " + request.getUserId());
                this.cheggRequest.processLink(request.getLink(), (completeResults) -> {
                    if (completeResults.getResult().hasFailed() || completeResults.getFiles().isEmpty()) {
                        if (completeResults.getResult() == Result.FAILED_TEXTBOOK_SOLUTION) {
                            GenZProfessor.getInstance().getJda().openPrivateChannelById(request.getUserId()).queue(privateChannel -> {
                                EmbedBuilder embedBuilder = new EmbedBuilder()
                                        .setTitle("Chegg request failed")
                                        .setThumbnail("https://media.giphy.com/media/HNEmXQz7A0lDq/giphy.gif")
                                        .setDescription("Unfortunately, the link you provided was a **textbook solution**.\r\nWe don't support textbook solutions.")
                                        .setFooter("My bad...")
                                        .setColor(Color.RED);
                                privateChannel.sendMessage(embedBuilder.build()).queue();
                                privateChannel.close().queueAfter(20, TimeUnit.SECONDS);
                            });
                        } else if (completeResults.getResult() == Result.FAILED_UNKNOWN_REASON || completeResults.getFiles().isEmpty()) {
                            GenZProfessor.getInstance().getJda().openPrivateChannelById(request.getUserId()).queue(privateChannel -> {
                                EmbedBuilder embedBuilder = new EmbedBuilder()
                                        .setTitle("Chegg request failed")
                                        .setThumbnail("https://media.giphy.com/media/HNEmXQz7A0lDq/giphy.gif")
                                        .setDescription("Unfortunately, something randomly went wrong.")
                                        .setFooter("My bad...")
                                        .setColor(Color.RED);
                                privateChannel.sendMessage(embedBuilder.build()).queue();
                                privateChannel.close().queueAfter(20, TimeUnit.SECONDS);
                            });
                        }
                        this.running = false;
                        return;
                    }

                    GenZProfessor.getInstance().getJda().openPrivateChannelById(request.getUserId()).queue(privateChannel -> {
                        privateChannel.sendMessage("**Here's the files**").queue();
                        completeResults.getFiles().forEach((file -> privateChannel.sendFile(file).queue()));

                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                .setAuthor("Chegg Answers")
                                .setThumbnail("https://media.giphy.com/media/cPIHGR2FxpFWBZ2CPD/giphy.gif")
                                .setDescription("Your request has been completed!\r\nThe files are above me.")
                                .setFooter("GenZProfessor | V: 1.0 | LastTimeUpdated: 10/1/2020")
                                .setColor(Color.getHSBColor(0, 88, 181));
                        privateChannel.sendMessage(embedBuilder.build()).queue();

                        privateChannel.close().queueAfter(20, TimeUnit.SECONDS);
                    });
                    this.running = false;
                });
                this.running = false;
            }
        };
        this.queueService = Executors.newSingleThreadScheduledExecutor();
        this.queueService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
    }

    public int getSize() {
        return this.requestQueue.size();
    }
}

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
class Request {
    private String guildId;
    private String userId;
    private String link;
}
