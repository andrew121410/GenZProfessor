package com.andrew121410.genzprofessor.queue;

import com.andrew121410.genzprofessor.GenZProfessor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.User;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QueueManager {

    private Queue<Request> requestQueue;

    private GenZProfessor genZProfessor;

    private FirefoxManager firefoxManager;
    private ScheduledExecutorService queueService;

    public QueueManager(GenZProfessor genZProfessor) {
        this.genZProfessor = genZProfessor;
        this.requestQueue = new LinkedList<>();
    }

    public void setup() {
        this.firefoxManager = new FirefoxManager(this.genZProfessor);
        this.firefoxManager.start();
        this.firefoxManager.setup();
        setupQueue();
    }

    public void quit() {
        this.requestQueue.clear();
        this.queueService.shutdown();
        this.firefoxManager.quit();
    }

    public void add(User user, String link) {
        this.requestQueue.add(new Request(user.getId(), link));
    }

    private void setupQueue() {
        Runnable runnable = () -> {
            if (!this.firefoxManager.isRunning() && !this.requestQueue.isEmpty()) {
                Request request = this.requestQueue.poll();
                System.out.println("Processing request in queue");
                this.firefoxManager.processLink(request.getLink(), (a) -> {
                    User user = this.genZProfessor.getJda().getUserById(request.getUserId());
                    if (user == null) return;
                    user.openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage("Hello here's the files.").queue();
                        a.forEach((file -> privateChannel.sendFile(file).queue()));
                        privateChannel.close().queueAfter(20, TimeUnit.SECONDS);
                    });
                });
            }
        };
        this.queueService = Executors.newSingleThreadScheduledExecutor();
        this.queueService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
    }
}

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
class Request {
    private String userId;
    private String link;
}
