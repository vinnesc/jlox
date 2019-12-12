package jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Lox {
    static boolean hadError = false;
    public static void error(int line, String message) {
        report(line, "", message);
    }

    public static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
    public static void run(String source) {
        Scanner scanner = new Scanner(source);

        List<Token> tokens = scanner.scan().stream().map(token -> {
            System.out.println(token);

            return token;
        }).collect(Collectors.toList());
    }

    public static void runFile(String file) throws IOException {
        byte[] contents = Files.readAllBytes(Paths.get(file));
        run(new String(contents, Charset.defaultCharset()));

        if (hadError) {
            System.exit(1);
        }
    }

    public static void runPrompt() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        for (;;) {
            System.out.print("> ");

            String line = bufferedReader.readLine();
            run(line);
            hadError = false;
        }

    }


    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("usage: jlox <script>");
            System.exit(1);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }
}
