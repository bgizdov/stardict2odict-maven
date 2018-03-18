package org.odict.stardict2odict;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Timer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.time.StopWatch;

/**
 * This is not a runnable app, refer package org.yage.dict.star please!
 */
public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options();

        options.addOption("c", "config", true, "YAML regex configuration file");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        List<String> argList = cmd.getArgList();
        int numberOfArguments = argList.size();

        if (numberOfArguments < 1) {
            System.out.println("Must provide at least one argument in order to run. Try --help for help.");
        } else if (numberOfArguments == 1) {
            String path = argList.get(0);

            if (!new File(path).exists()) {
                System.out.println("Could not find file: " + path);
                System.exit(1);
            }

            System.out.println(EmojiParser.parseToUnicode(":book:Processing dictionary file..."));
            StarDict dict = new StarDictParser().parse(path);
            EntryExample example = new ODictXMLConverter(dict).getExample();
            System.out.println(
                    String.format("Found example entry for word \"%s\": %s",
                            example.getWord(),
                            example.getExplanation()
                    ));
        } else if (numberOfArguments >= 2) {
            StopWatch sw = new StopWatch();
            sw.start();

            if (!cmd.hasOption("config")) {
                System.out.println("A YAML config file is required for conversion");
                System.exit(1);
            }

            String configPath = cmd.getOptionValue("config");

            if (!new File(configPath).exists()) {
                System.out.println("Config file does not exist at path: " + configPath);
                System.exit(1);
            }

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            YAMLConfig config = mapper.readValue(new File(configPath), YAMLConfig.class);
            String originPath = argList.get(0);

            if (!new File(originPath).exists()) {
                System.out.println("Origin file does not exist at path: " + originPath);
                System.exit(1);
            }

            System.out.println(EmojiParser.parseToUnicode(":book:Processing dictionary file..."));
            StarDict dict = new StarDictParser().parse(originPath);

            System.out.println(EmojiParser.parseToUnicode(":page_with_curl:Converting to ODXML (this may take awhile)..."));
            String xml = new ODictXMLConverter(dict).convert(config);

            System.out.println(EmojiParser.parseToUnicode(":floppy_disk:Writing to file..."));
            String destPath = argList.get(1);

            BufferedWriter writer = new BufferedWriter(new FileWriter(destPath));
            writer.write(xml);
            writer.close();

            sw.stop();

            System.out.println(EmojiParser.parseToUnicode(String.format("\n:sparkles:Completed in %d seconds ", sw.getNanoTime() / 1000000000)));
        }
    }
}
