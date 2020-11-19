package com.andrew121410.genzprofessor.manager;

import com.andrew121410.genzprofessor.GenZProfessor;
import com.andrew121410.genzprofessor.objects.CheggRequest;
import com.andrew121410.genzprofessor.objects.CheggRequestResult;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueManager {

    private Queue<CheggRequest> requestQueue;

    private GenZProfessor genZProfessor;
    private boolean running;

    private CheggRequestManager cheggRequestManager;
    private ScheduledExecutorService queueService;

    public QueueManager(GenZProfessor genZProfessor) {
        this.genZProfessor = genZProfessor;
        this.requestQueue = new LinkedList<>();
    }

    public void setup() {
        this.cheggRequestManager = new CheggRequestManager();
        this.cheggRequestManager.start();
        setupQueue();
        running = false;
    }

    public void quit() {
        this.requestQueue.clear();
        this.queueService.shutdown();
    }

    public void add(CheggRequest cheggRequest) {
        this.requestQueue.add(cheggRequest);
    }

    private void setupQueue() {
        Runnable runnable = () -> {
            if (!this.running && !this.requestQueue.isEmpty()) {
                this.running = true;
                CheggRequest cheggRequest = this.requestQueue.remove();
                if (cheggRequest == null) {
                    this.running = false;
                    return;
                }
                System.out.println("Processing request: " + cheggRequest.getUserId());
                this.cheggRequestManager.processLink(cheggRequest, (completeResults) -> {
                    if (completeResults.getResult().hasFailed() || completeResults.getFiles().isEmpty()) {
                        if (completeResults.getResult() == CheggRequestResult.Result.FAILED_TEXTBOOK_SOLUTION) {
                            GenZProfessor.getInstance().getJda().openPrivateChannelById(cheggRequest.getUserId()).queue(privateChannel -> {
                                EmbedBuilder embedBuilder = new EmbedBuilder()
                                        .setTitle("Chegg request failed")
                                        .setThumbnail("https://media.giphy.com/media/HNEmXQz7A0lDq/giphy.gif")
                                        .setDescription("Unfortunately, the link you provided was a **textbook solution**.\r\nWe don't support textbook solutions.")
                                        .setFooter("My bad...")
                                        .setColor(Color.RED);
                                privateChannel.sendMessage(embedBuilder.build()).queue();
                                privateChannel.close().queueAfter(20, TimeUnit.SECONDS);
                            });
                        } else if (completeResults.getResult() == CheggRequestResult.Result.FAILED_UNKNOWN_REASON || completeResults.getFiles().isEmpty()) {
                            GenZProfessor.getInstance().getJda().openPrivateChannelById(cheggRequest.getUserId()).queue(privateChannel -> {
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

                    GenZProfessor.getInstance().getJda().openPrivateChannelById(cheggRequest.getUserId()).queue(privateChannel -> {
                        privateChannel.sendMessage("**Here's the files**").queue();
                        completeResults.getFiles().forEach((file -> privateChannel.sendFile(file).queue()));

                        EmbedBuilder embedBuilder = new EmbedBuilder()
                                .setAuthor("Chegg Answers")
                                .setThumbnail("https://media.giphy.com/media/jRyZsj7JOpvtvt66r4/giphy.gif")
                                .setDescription("Your request has been completed!\r\nThe files are above me.")
                                .setFooter("GenZProfessor | V: " + GenZProfessor.VERSION + " | LastTimeUpdated: 11/18/2020")
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