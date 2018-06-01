package org.osm2world.core;

import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Singleton for holding the current configuration and make it available globally.
 * <p>
 * Created on 30.05.18.
 */
public class Config {
    Logger logger = Logger.getLogger(Config.class.getName());
    private CompositeConfiguration compositeConfiguration;
    private Configuration defaultconfig = null;
    private static Config instance = null;
    String defaultconfigfile = "config/configuration-default.properties";

    private Config(File configfile) {
        Reader reader = null;
        String source = "";
        if (configfile != null) {
            try {
                reader = new FileReader(configfile);
                source=configfile.getName();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (reader == null) {
            InputStream inputstream = Thread.currentThread().getContextClassLoader().getResourceAsStream(defaultconfigfile);
            reader = new InputStreamReader(inputstream);
            source=defaultconfigfile;
        }
        defaultconfig = loadConfig(reader,source);
        merge(null);
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config(null);
        }
        return instance;
    }

    public static Configuration getCurrentConfiguration() {
        return getInstance().compositeConfiguration;
    }

    public static void init(File configfile) {
        instance = new Config(configfile);
    }

    /**
     * keeps only default config and merges new userconfig 
     * @param userconfig
     */
    public static void reinit(Configuration userconfig) {
        getInstance().merge(userconfig);
    }

    /**
     * @param userconfig set of parameters that controls various aspects
     *                   of the modules' behavior; null to use defaults only
     */
    private void merge(Configuration userconfig) {
        compositeConfiguration = new CompositeConfiguration();
        if (userconfig != null) {
            compositeConfiguration.addConfiguration(userconfig);
        }
        compositeConfiguration.addConfiguration(defaultconfig);
    }

    private Configuration loadConfig(Reader inputstream, String source) {
        Configuration config = new BaseConfiguration();

        try {
            PropertiesConfiguration fileConfig = new PropertiesConfiguration();
            fileConfig.read((inputstream));
            //TODO really needed? fileConfig.setListDelimiter(';');
            config = fileConfig;
        } catch (Exception e) {
            logger.error("could not read config from "+source+", ignoring it: "+e.getMessage());
        }
        return config;
    }
}
