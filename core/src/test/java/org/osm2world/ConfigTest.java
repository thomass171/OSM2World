package org.osm2world;

import org.apache.commons.configuration2.Configuration;
import org.junit.Test;
import org.osm2world.core.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigTest {
    
    @Test
    public void testModuleConfig() {
        Config.reset();
        Configuration configuration = Config.getCurrentConfiguration();
        boolean b = configuration.getBoolean("modules.RoadModule.enabled");
        assertTrue("",b);

        String[] modulenames = Config.getInstance().getModules();
        assertEquals("",19,modulenames.length);
    }
}
