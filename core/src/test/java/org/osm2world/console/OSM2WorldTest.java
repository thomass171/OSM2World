package org.osm2world.console;

import org.junit.Assert;
import org.junit.Test;
import org.osm2world.core.ConversionFacade;
import org.osm2world.core.world.modules.PowerModule;
import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class OSM2WorldTest {

    @Test
    public void testInstance() throws ArgumentValidationException, URISyntaxException, IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("files" + File.separator + "validFile.osm");
        File testFile = new File(url.toURI());

        OSM2World osm2World = OSM2World.buildInstance(testFile, null);
        assertNotNull(osm2World.getData());
        ConversionFacade cf = osm2World.getConversionFacade();
        ConversionFacade.Results results = cf.createRepresentations();
        //assertFalse(CLIArgumentsGroup.isCompatible(cliArgs1, cliArgs3));

    }

    @Test
    public void testInstancePerConfig() throws ArgumentValidationException, URISyntaxException, IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("files" + File.separator + "validFile.osm");
        File testFile = new File(url.toURI());

        OSM2World osm2World = OSM2World.buildInstance(testFile, null);
        assertNotNull(osm2World.getData());
        ConversionFacade cf = osm2World.getConversionFacade();
        ConversionFacade.Results results = cf.createRepresentations();
        PowerModule powerModule = (PowerModule) cf.getModule("PowerModule");
        Assert.assertNotNull(powerModule);

    }
}
