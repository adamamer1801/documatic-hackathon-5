package dev.danky.cmm;

import dev.danky.cmm.Exceptions.IncorrectFiletypeException;
import dev.danky.cmm.lib.OnlineCompiler;
import dev.danky.cmm.lib.ext.FilenameUtils;
import dev.danky.cmm.lib.ext.IOUtils;
import picocli.CommandLine;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

@CommandLine.Command(name = "cmm")
public class CMM implements Callable<Integer> {

    private final String[] SUB_COMMANDS = { "toCMM", "fromCMM", "compile" };
    private final List<String> CPP_FILETYPES = new ArrayList<>(Arrays.asList("cpp", "cc"));
    private final List<String> CMM_FILETYPES = new ArrayList<>(List.of("cmm"));
    private final List<String> WINDOWS_COMPILERS = new ArrayList<>(List.of("mingw-w64"));
    private final List<String> UNIX_COMPILERS = new ArrayList<>(Arrays.asList("make", "g++")); // Linux/MacOS

    @CommandLine.Command(name = "ToCMM")
    public Integer CMMConvert(
            @CommandLine.Parameters(index = "0", description = "C++ file to convert to C--") File file,
            @CommandLine.Option(names = { "-o", "--output" }, description = "Where to output the compiled binaries", defaultValue = "./out.cmm") File output
    ) throws IOException, IncorrectFiletypeException {
        if (!file.exists()) throw new FileNotFoundException("Input file path specified does not exist or could not be found.");

        String path = file.getAbsolutePath();
        String ext = FilenameUtils.getExtension(path);

        String outPath = output.getAbsolutePath();
        String outExt = FilenameUtils.getExtension(outPath);

        if (!CPP_FILETYPES.contains(ext)) throw new IncorrectFiletypeException("Incorrect filetype for C++ to C-- conversion\ngot '" + ext + "', expected " + CPP_FILETYPES);
        if (!CMM_FILETYPES.contains(outExt)) throw new IncorrectFiletypeException("Incorrect (output) filetype for C++ to C-- conversion\ngot '" + outExt + "', expected " + CMM_FILETYPES);

        byte[] buffer = Files.readAllBytes(file.toPath());
        StringBuilder code = new StringBuilder(new String(buffer)).reverse();

        FileWriter fw = new FileWriter(output);
        fw.write(new String(code));
        fw.close();
        System.out.println(output);
        return 1;
    }

    @CommandLine.Command(name = "FromCMM")
      public Integer CPPConvert(
            @CommandLine.Parameters(index = "0", description = "C++ file to convert to C--") File file,
            @CommandLine.Option(names = { "-o", "--output" }, description = "Where to output the compiled binaries", defaultValue = "./out.cmm") File output
    ) throws IOException, IncorrectFiletypeException {
        if (!file.exists()) throw new FileNotFoundException("Input file path specified does not exist or could not be found.");

        String path = file.getAbsolutePath();
        String ext = FilenameUtils.getExtension(path);
        String outPath = output.getAbsolutePath();
        String outExt = FilenameUtils.getExtension(outPath);
        if (!CMM_FILETYPES.contains(ext)) throw new IncorrectFiletypeException("Incorrect filetype for C++ to C-- conversion\ngot '" + ext + "', expected " + CPP_FILETYPES);
        if (!CPP_FILETYPES.contains(outExt)) throw new IncorrectFiletypeException("Incorrect (output) filetype for C++ to C-- conversion\ngot '" + outExt + "', expected " + CMM_FILETYPES);
        FileWriter fw = new FileWriter(output);
        fw.write(new String(new StringBuilder(new String(Files.readAllBytes(file.toPath()))).reverse()));
        fw.close();
        return 1;
    }

    @CommandLine.Command(name = "compile")
    public Integer Compile(
            @CommandLine.Parameters(index = "0", description = "C++ file to convert to C--") File file,
            @CommandLine.Option(names = { "-o", "--output" }, description = "Where to output the compiled binaries", defaultValue = "./out") File output,
            @CommandLine.Option(names = { "-on", "--online" }, description = "Whether or not to compile online using the online compiler at https://compile.cmm.danky.dev/") boolean doOnlineCompiling,
            @CommandLine.Option(names = { "-c", "--compiler" }, description = "C++ compiler to use") String compiler
    ) throws Exception {
        if (!file.exists()) throw new FileNotFoundException("Input file path specified does not exist or could not be found.");

        String os = System.getProperty("os.name").split(" ")[0];
        byte[] buffer = Files.readAllBytes(file.toPath());
        AtomicBoolean foundWorkingCompiler = new AtomicBoolean(false);

        File tmp = new File(file.getAbsolutePath().replace(".cmm", ".cpp"));tmp.deleteOnExit();
        FileWriter fw = new FileWriter(tmp);fw.write(new String(new StringBuilder(new String(buffer)).reverse()));fw.close();

        if (compiler != null) {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                        compiler,
                        file.getAbsolutePath().replace(".cmm", ""),
                        "-o",
                        output.getAbsolutePath()
                );

                Process p = pb.start();
                new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader esr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                List<String> c = new ArrayList<>();
                while (true) {
                    String line = esr.readLine();
                    if (line == null) break;
                    c.add(line);
                }

                if (!c.isEmpty()) {
                    System.out.println("Error compiling:\n" + String.join("\n", c));
                    System.exit(1);
                }

                System.out.print("Found, compiling done.\n");
                foundWorkingCompiler.set(true);
                System.exit(0);
            } catch(IOException e) {
                System.out.println("Your file does not exist or your specified compiler is not a valid terminal command");
                Scanner scanner = new Scanner(System.in);
                System.out.print("Continue to regular compiler checking? (y/n): ");
                String inp = scanner.next();

                if ("y".equalsIgnoreCase(inp)) {
                    doOnlineCompiling = true;
                } else {
                    System.exit(0);
                }
            }
        }

        if (doOnlineCompiling) {
            System.out.println("You have selected to do online compiling, Some things to note: \n> this will not work if your program has includes that are unique to your project.\n> This only generates binaries for 64 bit Windows.");
            byte[] response;
            response = new OnlineCompiler().compileOnline(new String(buffer));
            FileOutputStream fos = new FileOutputStream(output);
            IOUtils.write(response, fos);
            System.exit(0);
        } else {
            switch (os.toLowerCase()) {
                case "windows" -> {
                    System.out.println("Detected platform " + os + "; Checking valid " + os + " compilers: " + WINDOWS_COMPILERS);
                    WINDOWS_COMPILERS.forEach(s -> {
                        if (foundWorkingCompiler.get()) return;
                        System.out.print("Checking compiler " + s + ": ");
                        ProcessBuilder pb = new ProcessBuilder(
                                s,
                                file.getAbsolutePath().replace(".cmm", ""),
                                "-o",
                                output.getAbsolutePath() + ".exe"
                        );

                        try {
                            Process p = pb.start();
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
                            BufferedReader esr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                            List<String> c = new ArrayList<>();
                            while (true) {
                                String line = esr.readLine();
                                if (line == null) break;
                                c.add(line);
                            }

                            if (!c.isEmpty()) {
                                System.out.println("Error compiling:\n" + String.join("\n", c));
                                System.exit(1);
                            }

                            System.out.print("Found, compiling done.\n");
                            foundWorkingCompiler.set(true);
                            System.exit(0);
                        } catch (IOException e) {
                            System.out.print("Not found\n");
                        }
                    });
                }
                case "linux", "macos" -> {
                    System.out.println("Detected platform " + os + "; Checking valid " + os + " compilers: " + UNIX_COMPILERS);
                    UNIX_COMPILERS.forEach(s -> {
                        if (foundWorkingCompiler.get()) return;
                        System.out.print("Checking compiler " + s + ": ");
                        ProcessBuilder pb = new ProcessBuilder(
                                s,
                                file.getAbsolutePath().replace(".cmm", ""),
                                "-o",
                                output.getAbsolutePath()
                        );


                        try {
                            Process p = pb.start();
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
                            BufferedReader esr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                            List<String> c = new ArrayList<>();
                            while (true) {
                                String line = esr.readLine();
                                if (line == null) break;
                                c.add(line);
                            }

                            if (!c.isEmpty()) {
                                System.out.println("Error compiling:\n" + String.join("\n", c));
                                System.exit(1);
                            }

                            System.out.print("Found, compiling done.\n");
                            foundWorkingCompiler.set(true);
                            System.exit(0);
                        } catch (IOException e) {
                            System.out.print("Not found\n");
                        }
                    });
                }
                default -> {
                    System.out.println("Unsupported platform");
                    System.exit(1);
                }
            }

            if (!foundWorkingCompiler.get()) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("No compiler found for platform " + os + ", compile online? (Compiles 64 bit Windows binaries) (y/n): ");
                String inp = scanner.next();

                byte[] response;

                if ("y".equalsIgnoreCase(inp)) {
                    System.out.println("You have selected to do online compiling, Some things to note: \n> this will not work if your program has includes that are unique to your project.\n> This only generates binaries for 64 bit Windows.");
                    response = new OnlineCompiler().compileOnline(new String(buffer));
                    FileOutputStream fos = new FileOutputStream(output);
                    IOUtils.write(response, fos);
                    System.exit(0);
                } else {
                    System.exit(0);
                }

            }
        }
        return 1;
    }

    @Override
    public Integer call() {
        System.out.println("\nAvailable sub-commands: " + String.join(", ", this.SUB_COMMANDS) + "\n");
        return null;
    }

    public static void main(String[] args) {
        int c = new CommandLine(new CMM()).execute(args);
        System.exit(c);
    }
}