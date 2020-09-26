package com.andrew121410.genzprofessor.config;

import com.andrew121410.genzprofessor.GenZProfessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.SneakyThrows;

import java.io.File;

public class ConfigManager {

    private GenZProfessor genZProfessor;
    private GenZProfessorJacksonConfig genZProfessorJacksonConfig;

    public ConfigManager(GenZProfessor genZProfessor) {
        this.genZProfessor = genZProfessor;
    }

    @SneakyThrows
    public GenZProfessorJacksonConfig loadConfig() {
        ObjectMapper objectMapper = createDefaultMapper();
        File configFile = new File(getConfigFolder(), "config.yml");
        if (configFile.exists()) {
            this.genZProfessorJacksonConfig = objectMapper.readValue(configFile, GenZProfessorJacksonConfig.class);
            return this.genZProfessorJacksonConfig;
        } else {
            this.genZProfessorJacksonConfig = null;
            objectMapper.writeValue(configFile, new GenZProfessorJacksonConfig());
            return null;
        }
    }

    public ObjectMapper createDefaultMapper() {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        return new ObjectMapper(yamlFactory);
    }

    public File getConfigFolder() {
        File configFolder = new File("config");
        if (!configFolder.exists()) {
            configFolder.mkdir();
        }
        return configFolder;
    }

    public GenZProfessorJacksonConfig getMainConfig() {
        return genZProfessorJacksonConfig;
    }
}
