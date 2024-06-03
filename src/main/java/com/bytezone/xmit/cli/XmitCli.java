package com.bytezone.xmit.cli;

import com.bytezone.xmit.Utility;
import com.bytezone.xmit.api.XmitMember;
import com.bytezone.xmit.api.XmitPartitionedDataset;
import com.bytezone.xmit.api.XmitReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.*;

public class XmitCli {

  public static void argss(String[] args) throws IOException {
    File f = new File("C:\\Users\\loren\\Desktop\\XMIT\\RES.SAVE.D230208.ADCD.Z23B.PARMLIB.XMIT");
    Utility.setCodePage("CP037");
    XmitReader reader = XmitReader.fromFile(f);
    reader.getPartitionedDatasets().stream()
        .flatMap(ds -> ds.getMembers().stream())
        .map(XmitMember::getContent)
        .forEach(System.out::println);
  }

  public static void listDataSet(XmitReader reader) {
    System.out.println("Partitioned dataset list:");
    int count = 0;
    for (var ds : reader.getPartitionedDatasets()) {
      System.out.printf("%4d: %s\n", count++, ds.getName());
    }
    count = 0;

    System.out.println("Physical sequential dataset list:");
    for (var ds : reader.getPhysicalSequentialDatasets()) {
      System.out.printf("%4d: %s\n", count++, ds.getName());
    }
  }

  public static void listMembers(XmitReader reader, int iDataset) {
    XmitPartitionedDataset dataset = reader.getPartitionedDatasets().get(iDataset);
    System.out.println("Members of dataset " + dataset.getName() + ":");
    int count = 0;
    for (var memeber : dataset.getMembers()) {
      System.out.printf("%4d: %s\n", count++, memeber.getName());
    }
  }

  public static int exportMember(
      XmitReader reader, File outputDir, int iDataset, List<Integer> iMember) {
    var dataset = reader.getPartitionedDatasets().get(iDataset);
    iMember.stream().map(i -> dataset.getMembers().get(i)).forEach(m -> storeFile(outputDir, m.getName(), m.getContent()));
    return iMember.size();
  }

  public static int exportAllMember(XmitReader reader, File outputDir, List<Integer> iDataset) {
    var members = iDataset.stream().flatMap(i -> reader.getPartitionedDatasets().get(i).getMembers().stream()).toList();
    for(var m : members){
      storeFile(outputDir, m.getName(), m.getContent());
    }
    return members.size();
  }

  public static int exportAll(XmitReader reader, File outputDir) {
    var members = reader.getPartitionedDatasets().stream().flatMap(d -> d.getMembers().stream()).toList();
    for(var m : members){
      storeFile(outputDir, m.getName(), m.getContent());
    }
    return members.size();
  }

  public static void main(String[] args) {
    Options options = new Options();
    int nFiles = 0;
    // Define the dataset option
    Option directory =
        Option.builder("dir")
            .longOpt("directory")
            .hasArg(false)
            .desc("Extract the whole directory")
            .required(false)
            .build();
    options.addOption(directory);

    // Define the dataset option
    Option dataset =
        Option.builder("d")
            .longOpt("dataset")
            .hasArg(true)
            .desc("Dataset argument")
            .required(false)
            .build();
    options.addOption(dataset);

    // Define the members option
    Option members =
        Option.builder("m")
            .longOpt("members")
            .hasArg(true)
            .desc("Members argument")
            .required(false)
            .build();
    options.addOption(members);

    // Define the output option
    Option output =
        Option.builder("o")
            .longOpt("output")
            .hasArg(true)
            .desc("Output folder")
            .required(false)
            .build();
    options.addOption(output);

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();

    try {
      // Manually parse the folder argument
      if (args.length < 1) {
        throw new ParseException("Usage: Xmit source <options>");
      }
      String source = args[0];

      // Create a new array for the remaining options
      String[] optionArgs = new String[args.length - 1];
      System.arraycopy(args, 1, optionArgs, 0, args.length - 1);

      CommandLine cmd = parser.parse(options, optionArgs);

      File outputDir = new File(".");
      if (cmd.hasOption(output)) {
        outputDir = new File(cmd.getOptionValue(output));
        if (!outputDir.exists()) throw new ParseException("Output folder ("+outputDir+") does not exist");
        else if (!outputDir.isDirectory())
          throw new ParseException("Output folder is not a directory");
      }

      File file = new File(source);
      if (cmd.hasOption(directory)) nFiles = exportDirectory(file, outputDir);
      else if (file.isDirectory()) throw new ParseException("source is a directory, use -dir");
      else if (!file.getName().toLowerCase().endsWith(".xmit"))
        throw new IllegalArgumentException(file + " is not a .xmit file");
      else {
        XmitReader reader = XmitReader.fromFile(file);

        // Validate dataset and members input

        if (!cmd.hasOption(dataset)) {
          if (cmd.hasOption(members))
            throw new ParseException(
                "Option 'members' is not allowed without a single dataset specified ");
          nFiles = exportAll(reader, outputDir);
        } else if (cmd.getOptionValue(dataset).equals("list")) {
          if (cmd.hasOption(members))
            throw new ParseException("Option 'members' is not allowed with 'dataset list'");
          listDataSet(reader);
        } else {
          if (!cmd.getOptionValue(dataset).matches("[\\d ]+"))
            throw new ParseException(
                "Error parsing dataset list. Found: " + cmd.getOptionValue(dataset));
          List<Integer> datasetValues =
              Arrays.stream(cmd.getOptionValue(dataset).split(" ")).map(Integer::parseInt).toList();

          if (datasetValues.size() == 1) {
            var iDataset = datasetValues.get(0);
            if (!cmd.hasOption(members)) nFiles = exportAll(reader, outputDir);
            else if (cmd.getOptionValue(members).equals("list")) {
              listMembers(reader, iDataset);
            } else {
              if (!cmd.getOptionValue(members).matches("[\\d ]+"))
                throw new ParseException(
                    "Error parsing dataset list. Found: " + cmd.getOptionValue(dataset));
              List<Integer> membersValue =
                  Arrays.stream(cmd.getOptionValue(members).split(" "))
                      .map(Integer::parseInt)
                      .toList();
              nFiles = exportMember(reader, outputDir, iDataset, membersValue);
            }
          } else {
            if (cmd.hasOption("members"))
              throw new ParseException("Option 'members' is not allowed with multiple datasets'");
            nFiles = exportAllMember(reader, outputDir, datasetValues);
          }
        }
      }
      System.out.println(nFiles +" files exported in "+outputDir);

    } catch (ParseException e) {
      System.err.println(e.getMessage());
      formatter.printHelp("Xmit source", options);
      System.exit(1);
    }
  }

  private static int exportDirectory(File dir, File outDir) {
    int nFiles = 0;
    File[] filesInDir = dir.listFiles();
    if(filesInDir == null) throw new IllegalStateException();

    List<File> fileList = List.of(filesInDir);
    for(var file : fileList){
      if(!file.getName().toLowerCase().endsWith(".xmit")) continue;
      XmitReader reader = XmitReader.fromFile(file);
      nFiles += exportAll(reader, outDir);
    }
    return nFiles;
  }

  private static void storeFile(File outDir, String name, String content) {
    try {
      File outFile = new File(outDir, name + ".txt");
      PrintWriter writer = new PrintWriter(new FileWriter(outFile));
      writer.write(content);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException("Failed to save file", e);
    }
  }
}
