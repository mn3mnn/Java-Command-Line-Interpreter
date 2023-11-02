import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

// 1st Class
class Parser {
    String commandName;
    String[] args;

    public boolean parse(String input) {
        String[] parts = input.split("\\s+", 2); // Split the input into command and arguments
        if (parts.length > 0) {
            commandName = parts[0];
            if (parts.length > 1) {
                args = parts[1].split("\\s+");
            } else {
                args = new String[0];
            }
            return true;
        }
        return false;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }

}



// 2nd Class
class Terminal {
    Parser parser;
    List<String> commandHistory;

    public Terminal() {
        this.parser = new Parser();
        this.commandHistory = new ArrayList<>();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.print(">");
            input = scanner.nextLine();
            commandHistory.add(input);

            if (input.equals("exit")) {
                break;
            }

            if (parser.parse(input)) {
                chooseCommandAction();
            } else {
                System.out.println("Invalid command. Please try again.");
            }

        }
    }

    public void chooseCommandAction() {
        String commandName = parser.getCommandName();
        String[] args = parser.getArgs();

        switch (commandName) {
            case "echo":
                if (args.length == 1) {
                    echo(args[0]);
                } else if (args.length > 1 && args[0].startsWith("\"") && args[args.length - 1].endsWith("\"")) {
                    echo(String.join(" ", args).replace("\"", "")); // Join the arguments into a single string and remove the quotes
                } else {
                    System.out.println("Usage: echo <message>");
                }
                break;

            case "pwd":
                if (args.length == 0) {
                    pwd();
                } else {
                    System.out.println("Usage: pwd");
                }
                break;

            case "cd":
                if (args.length == 1) {
                    cd(args[0]);
                }
                else if (args.length == 0){
                    cd(""); // home directory
                }
                else {
                    System.out.println("Usage: cd <directory>");
                }
                break;

            case "ls":
                if (args.length == 0) {
                    ls();
                } else if (args.length == 1 && args[0].equals("-r")) {
                    lsReverse();
                } else {
                    System.out.println("Usage: ls or ls -r");
                }
                break;

            case "mkdir":
                if (args.length >= 1) {
                    mkdir(args);
                } else {
                    System.out.println("Usage: mkdir <directory1> <directory2> ...");
                }
                break;

            case "rmdir":
                if (args.length == 1 && args[0].equals("*")) {
                    rmdirAll();
                } else if (args.length == 1) {
                    rmdir(args[0]);
                } else {
                    System.out.println("Usage: rmdir * or rmdir <directory>");
                }
                break;

            case "touch":
                if (args.length == 1) {
                    touch(args[0]);
                } else {
                    System.out.println("Usage: touch <file>");
                }
                break;

            case "cp":
                if (args.length == 2) {
                    cp(args[0], args[1]);
                } else {
                    System.out.println("Usage: cp <source> <destination>");
                }
                break;

            case "rm":
                if (args.length == 1) {
                    rm(args[0]);
                } else {
                    System.out.println("Usage: rm <file>");
                }
                break;

            case "cat":
                if (args.length == 1) {
                    cat(args[0]);
                } else if (args.length == 2) {
                    catConcatenate(args[0], args[1]);
                } else {
                    System.out.println("Usage: cat <file> or cat <file1> <file2>");
                }
                break;

            case "wc":
                if (args.length == 1) {
                    wc(args[0]);
                } else {
                    System.out.println("Usage: wc <file>");
                }
                break;

            case ">":
                if (args.length == 1) {
                    redirect(args[0], true);
                } else {
                    System.out.println("Usage: > <file>");
                }
                break;

            case ">>":
                if (args.length == 1) {
                    redirect(args[0], false);
                } else {
                    System.out.println("Usage: >> <file>");
                }
                break;

            case "history":
                if (args.length == 0) {
                    history();
                } else {
                    System.out.println("Usage: history");
                }
                break;

            case "exit":
                break;
            default:
                System.out.println("Unknown command: " + commandName);
        }
    }


    // The command methods

    public void echo(String message) {
        System.out.println(message);
    }

    public void pwd() {
        String currentDirectory = System.getProperty("user.dir");
        System.out.println(currentDirectory);
    }

    public void cd(String directory) {
        try {
            if (directory.equals("..")) { // parent directory
                String currentDirectory = System.getProperty("user.dir");
                String parentDirectory = Paths.get(currentDirectory).getParent().toString();
                System.setProperty("user.dir", parentDirectory);
            }
            else if (directory.equals("")) { // home directory
                System.setProperty("user.dir", System.getProperty("user.home"));
            }
            // absolute path
            else if (Files.exists(Paths.get(directory.replace("\"", "")))){
                System.setProperty("user.dir", directory.replace("\"", ""));
            }
            // relative path
            else if (Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + directory.replace("\"", "")))) {
                System.setProperty("user.dir", System.getProperty("user.dir") + "\\" + directory.replace("\"", ""));
            } else {
                System.out.println("Directory does not exist");
            }
        } catch (Exception e) {
            System.out.println("Error changing directory: " + e.getMessage());
        }
    }

    public void ls() {
        try {
            Files.list(Paths.get(System.getProperty("user.dir")))
                    .map(Path::getFileName)
                    .forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error listing directory: " + e.getMessage());
        }
    }

    public void lsReverse() {
        try {
            List<String> fileList = Files.list(Paths.get(System.getProperty("user.dir")))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
            Collections.reverse(fileList);
            fileList.forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error listing directory in reverse: " + e.getMessage());
        }
    }

    public void mkdir(String[] directories) {
        for (String dir : directories) {
            Path newDir = Paths.get(dir);
            try {
                Files.createDirectories(newDir);
                System.out.println("Directory created: " + newDir.toAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error creating directory: " + e.getMessage());
            }
        }
    }

    public void rmdirAll() {
        rmdirAll(System.getProperty("user.dir"));
    }

    private void rmdirAll(String directory) {
        File dir = new File(directory);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    rmdirAll(file.getPath());
                }
                file.delete();
            }
        }
        dir.delete();
    }

    public void rmdir(String directory) {
        String currentDirectory = System.getProperty("user.dir");
        String dirToRemove = currentDirectory + File.separator + directory;
        File dir = new File(dirToRemove);

        if (dir.exists() && dir.isDirectory()) {
            if (dir.list().length == 0) {
                dir.delete();
                System.out.println("Directory removed: " + dir.getAbsolutePath());
            } else {
                System.out.println("Directory is not empty: " + dir.getAbsolutePath());
            }
        } else {
            System.out.println("Directory not found: " + dir.getAbsolutePath());
        }
    }
    public void touch(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.createFile(path);
            System.out.println("File created: " + path.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    public void cp(String sourcePath, String destPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path destination = Paths.get(destPath);
            if (Files.isDirectory(source)) {
                Files.walk(source)
                        .forEach(sourceFile -> {
                            try {
                                Path destFile = destination.resolve(source.relativize(sourceFile));
                                if (Files.isDirectory(sourceFile)) {
                                    Files.createDirectories(destFile);
                                } else {
                                    Files.copy(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING);
                                }
                            } catch (IOException e) {
                                System.out.println("Error copying file/directory: " + e.getMessage());
                            }
                        });
            } else {
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Copied: " + sourcePath + " to " + destPath);
        } catch (IOException e) {
            System.out.println("Error copying file/directory: " + e.getMessage());
        }
    }

    public void rm(String fileName) {
        try {
            Path file = Paths.get(fileName);
            if (Files.isRegularFile(file)) {
                Files.delete(file);
                System.out.println("File removed: " + file.toAbsolutePath());
            } else {
                System.out.println("File not found: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("Error removing file: " + e.getMessage());
        }
    }

    public void cat(String fileName) {
        try {
            Path file = Paths.get(fileName);
            if (Files.isRegularFile(file)) {
                List<String> lines = Files.readAllLines(file);
                lines.forEach(System.out::println);
            } else {
                System.out.println("File not found: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public void catConcatenate(String file1, String file2) {
        try {
            Path path1 = Paths.get(file1);
            Path path2 = Paths.get(file2);
            if (Files.isRegularFile(path1) && Files.isRegularFile(path2)) {
                List<String> lines1 = Files.readAllLines(path1);
                List<String> lines2 = Files.readAllLines(path2);
                List<String> combinedLines = new ArrayList<>(lines1);
                combinedLines.addAll(lines2);
                combinedLines.forEach(System.out::println);
            } else {
                System.out.println("File not found: " + (Files.isRegularFile(path1) ? file2 : file1));
            }
        } catch (IOException e) {
            System.out.println("Error reading files: " + e.getMessage());
        }
    }

    public void wc(String fileName) {
        try {
            Path file = Paths.get(fileName);
            if (Files.isRegularFile(file)) {
                List<String> lines = Files.readAllLines(file);
                int lineCount = lines.size();
                int wordCount = lines.stream()
                        .map(line -> line.split("\\s+"))
                        .mapToInt(arr -> arr.length)
                        .sum();
                int charCount = lines.stream()
                        .mapToInt(String::length)
                        .sum();
                System.out.println(lineCount + " " + wordCount + " " + charCount + " " + fileName);
            } else {
                System.out.println("File not found: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public void redirect(String fileName, boolean truncate) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter content to write to the file: ");
        String content = scanner.nextLine();
        try {
            Path file = Paths.get(fileName);
            if (!truncate) {
                Files.write(file, (content + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                Files.write(file, (content + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            System.out.println("Content written to " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public void history() {
        for (int i = 0; i < commandHistory.size(); i++) {
            System.out.println((i + 1) + " " + commandHistory.get(i));
        }
    }

}

// Main Class
public class Main {
    public static void main(String[] args) {
        Terminal terminal = new Terminal();
        terminal.run();
    }
}

