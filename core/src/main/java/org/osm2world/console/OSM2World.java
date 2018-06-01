package org.osm2world.console;

import static org.osm2world.console.CLIArgumentsUtil.getProgramMode;
import static org.osm2world.core.GlobalValues.VERSION_STRING;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.UIManager;

import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.osm2world.console.CLIArgumentsUtil.ProgramMode;
import org.osm2world.core.Config;
import org.osm2world.core.ConversionFacade;
import org.osm2world.core.GlobalValues;


import org.osm2world.core.osm.creation.OSMDataReader;
import org.osm2world.core.osm.creation.OSMFileReader;
import org.osm2world.core.osm.data.OSMData;
import org.osm2world.core.target.Target;
import org.osm2world.core.target.TargetUtil;
import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;

/**
 * main class of the OSM2World console application
 */
public class OSM2World {

    public static void main(String[] unparsedArgs) {
        
		/* assume --gui if no parameters are given */

        if (unparsedArgs.length == 0) {

            System.out.println("No parameters, running graphical interface.\n"
                    + "If you want to use the command line, use the --help"
                    + " parameter for a list of available parameters.");

            unparsedArgs = new String[]{"--gui"};

        }
        
		/* parse command line arguments */

        CLIArguments args = null;

        try {
            args = parseArguments(unparsedArgs);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
		/* parse lines from parameter file (if one exists) */

        List<CLIArguments> argumentsList = Collections.singletonList(args);

        if (args.isParameterFile()) {

            argumentsList = new ArrayList<CLIArguments>();

            try {

                List<String[]> unparsedArgsLines = CLIArgumentsUtil
                        .getUnparsedParameterGroups(args.getParameterFile());

                for (String[] unparsedArgsLine : unparsedArgsLines) {

                    try {
                        argumentsList.add(parseArguments(unparsedArgsLine));
                    } catch (Exception e) {
                        System.err.println("Could not parse parameters from file:");
                        System.err.println(Arrays.toString(unparsedArgsLine));
                        System.err.println("Ignoring it. Reason:");
                        System.err.println(e.getMessage());
                    }

                }

            } catch (IOException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }

        }
        
		/* collect parameter groups into compatible groups
		 * (groups of parameter groups that use the same input and config files) */

        List<CLIArgumentsGroup> argumentsGroups = new ArrayList<CLIArgumentsGroup>();

        for (CLIArguments arguments : argumentsList) {

            boolean added = false;

            for (CLIArgumentsGroup compatibleGroup : argumentsGroups) {
                if (compatibleGroup.isCompatible(arguments)) {
                    // add to existing compatible group
                    compatibleGroup.addCLIArguments(arguments);
                    added = true;
                    break;
                }
            }

            if (!added) {
                // start a new compatible group
                argumentsGroups.add(new CLIArgumentsGroup(arguments));
            }

        }
		
		/* execute conversions */

        if (argumentsGroups.isEmpty()) {
            System.err.println("warning: empty parameter file, doing nothing");
        }

        for (CLIArgumentsGroup argumentsGroup : argumentsGroups) {

            if (argumentsList.size() > 1) {
                System.out.print("executing conversion for these parameter lines: ");
                for (CLIArguments p : argumentsGroup.getCLIArgumentsList()) {
                    System.out.print(argumentsList.indexOf(p) + " ");
                }
                System.out.print("\n");
            }

            executeArgumentsGroup(argumentsGroup);

        }

    }

    private static CLIArguments parseArguments(String[] unparsedArgs)
            throws ArgumentValidationException, Exception {

        CLIArguments args = CliFactory.parseArguments(CLIArguments.class, unparsedArgs);

        if (!CLIArgumentsUtil.isValid(args)) {
            throw new Exception(CLIArgumentsUtil.getErrorString(args));
        }
        return args;

    }

   

    private static void executeArgumentsGroup(CLIArgumentsGroup argumentsGroup) {
		
		/* load configuration file */

        Configuration config = new BaseConfiguration();

        CLIArguments representativeArgs = argumentsGroup.getRepresentative();

        if (representativeArgs.isConfig()) {
                Config.init(representativeArgs.getConfig());
                config = Config.getCurrentConfiguration();
        }
		
		/* run selected mode */

        ProgramMode programMode = getProgramMode(representativeArgs);

        switch (programMode) {

            case HELP:
                System.out.println(
                        CliFactory.createCli(CLIArguments.class).getHelpMessage()
                                + "\n\nFor more information, see " + GlobalValues.WIKI_URI);
                break;

            case VERSION:
                System.out.println("OSM2World " + VERSION_STRING);
                break;

            case GUI:
                throw new RuntimeException("No gui available");
			/*try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				System.out.println("Error setting native look and feel: " + e);
			}
			File input = representativeArgs.isInput() ?
					representativeArgs.getInput() : null;
			new ViewerFrame(config, configFile, input).setVisible(true);
			break;*/

            case CONVERT:
                try {
                    Output.output(config, argumentsGroup);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    OSMData osmdata = null;
    ConversionFacade cf;
    Configuration compositeConfiguration;

    OSM2World(File inputfile, Configuration userconfig) throws IOException {
          // better to use CombinedConfiguration?
        OSMDataReader dataReader = new OSMFileReader(inputfile);
        osmdata = dataReader.getData();
        cf = new ConversionFacade(osmdata);

    }

    /**
     * Entry for external modules
     */
    public static OSM2World buildInstance(File inputfile, Configuration userconfig) throws IOException {
        OSM2World osm2World = new OSM2World(inputfile, userconfig);
        return osm2World;
    }

    public ConversionFacade getConversionFacade() {
        return cf;
    }

    public OSMData getData() {
        return osmdata;
    }

}
