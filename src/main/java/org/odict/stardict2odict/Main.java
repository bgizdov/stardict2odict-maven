package org.odict.stardict2odict;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.time.StopWatch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * This is not a runnable app, refer package org.yage.dict.star please!
 */
public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options();

        options.addOption("c", "config", true, "YAML regex configuration file");
        options.addOption("n", "name", true, "Inline name for the dictionary");

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
            List<EntryExample> example = new ODictXMLConverter(dict).getExample();

            System.out.println("Found example entries:\n");

            example.forEach(ex -> System.out.println(String.format("%s\n%s\n\n", ex.getWord(), ex.getExplanation())));
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

            String name = null;
            if (cmd.hasOption("name")) {
                name = cmd.getOptionValue("name");
            }

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            YAMLConfig config = mapper.readValue(new File(configPath), YAMLConfig.class);
            if (Objects.nonNull(name)) {
                config.setName(name);
            }
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

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destPath), StandardCharsets.UTF_8));
            writer.write(xml);
            writer.close();

            sw.stop();

            System.out.println(EmojiParser.parseToUnicode(String.format("\n:sparkles:Completed in %d seconds ", sw.getNanoTime() / 1000000000)));
        }
    }
}
