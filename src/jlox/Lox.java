package jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Lox {
    public static void run(String source) {
        Scanner scanner = new Scanner(source);

        List<String> tokens = scanner.tokens().map(token -> {
            System.out.println(token);

            return token;
        }).collect(Collectors.toList());
    }

    public static void runFile(String file) throws IOException {
        byte[] contents = Files.readAllBytes(Paths.get(file));
        run(new String(contents, Charset.defaultCharset()));
    }

    public static void runPrompt() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        for (;;) {
            System.out.println("> ");

            String line = bufferedReader.readLine();
            run(line);
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
