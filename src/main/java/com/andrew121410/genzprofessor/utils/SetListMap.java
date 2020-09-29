package com.andrew121410.genzprofessor.utils;

import com.andrew121410.genzprofessor.manager.ICommand;

import java.util.HashMap;
import java.util.Map;

public class SetListMap {

    private Map<String, ICommand> commandMap;
    private Map<String, String> shortcutMap;

    public SetListMap() {
        this.commandMap = new HashMap<>();
        this.shortcutMap = new HashMap<>();
    }

    public Map<String, ICommand> getCommandMap() {
        return commandMap;
    }

    public Map<String, String> getShortcutMap() {
        return shortcutMap;
    }
}
