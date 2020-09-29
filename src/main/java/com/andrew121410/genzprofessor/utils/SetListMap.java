package com.andrew121410.genzprofessor.utils;

import com.andrew121410.genzprofessor.manager.ICommand;

import java.util.HashMap;
import java.util.Map;

public class SetListMap {

    private Map<String, ICommand> commandMap;

    public SetListMap() {
        this.commandMap = new HashMap<>();
    }

    public Map<String, ICommand> getCommandMap() {
        return commandMap;
    }
}
