package com.andrew121410.genzprofessor;

import com.andrew121410.genzprofessor.commands.CheggCMD;
import com.andrew121410.genzprofessor.config.ConfigManager;
import com.andrew121410.genzprofessor.manager.CommandManager;
import com.andrew121410.genzprofessor.manager.QueueManager;
import com.andrew121410.genzprofessor.utils.SetListMap;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GenZProfessor {

    public static final String VERSION = "1.3";

    private static GenZProfessor instance;

    private JDA jda;

    private SetListMap setListMap;
    private ConfigManager configManager;
    private CommandManager commandManager;
    private QueueManager queueManager;

    public static void main(String[] args) {
        new GenZProfessor(args);
    }

    public GenZProfessor(String[] args) {
        instance = this;
        this.setListMap = new SetListMap();
        this.configManager = new ConfigManager(this);
        this.configManager.loadConfig();
        setupJDA();
        setupScanner();
    }

    @SneakyThrows
    public void setupJDA() {
        this.commandManager = new CommandManager(this);

        if (this.configManager.getMainConfig() == null) {
            System.out.println("Please add discord token to config.yml.");
            exit();
        }

        this.jda = JDABuilder.createDefault(this.configManager.getMainConfig().getToken())
                .setEventManager(new AnnotatedEventManager())
                .addEventListeners(commandManager)
//                .addEventListeners(new CEvents(this))
                .build()
                .awaitReady();

        this.queueManager = new QueueManager(this);
        this.queueManager.setup();

        setupCommands();
    }

    public void setupCommands() {
        new CheggCMD(this);
    }

    private void setupScanner() {
        try (InputStream in = System.in) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                switch (line.toLowerCase()) {
                    case "stop":
                    case "end":
                    case "exit":
                        exit();
                        break;
                    default:
                        System.out.println("Sorry don't understand: " + line);
                }
            }
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    public void exit() {
        this.queueManager.quit();
        System.out.println("Exited Successfully!");
        this.jda.shutdown();
        System.exit(0);
    }

    public static GenZProfessor getInstance() {
        return instance;
    }

    public JDA getJda() {
        return jda;
    }

    public SetListMap getSetListMap() {
        return setListMap;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }
}
