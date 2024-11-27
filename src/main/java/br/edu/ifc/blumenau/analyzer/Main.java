package br.edu.ifc.blumenau.analyzer;

public class Main {

    public static void main(String[] args) {
        String repo = "";
        if (args.length > 0) {
            repo = args[0];
        }

//        System.out.println("projeto: " + repo);

        Analyzer analyzer = new Analyzer(repo);
        analyzer.setShouldRefactor(true);
        analyzer.setShouldWriteToFile(false);
        analyzer.run();
    }
}
