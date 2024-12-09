package br.edu.ifc.blumenau.analyzer;

public class Main {

    public static void main(String[] args) {
        String repo = "";
        String shouldrRefactor = "";
        String shouldWriteToFile = "";
        if (args.length > 0) {
            repo = args[0];
            shouldrRefactor = args[1];
            shouldWriteToFile = args[2];
        }

//        System.out.println("projeto: " + repo);

        Analyzer analyzer = new Analyzer(repo);
        analyzer.setShouldRefactor(true);
        if (shouldrRefactor.equals("true")) {
            analyzer.setShouldRefactor(true);
        }
        analyzer.setShouldWriteToFile(true);
        if (shouldWriteToFile.equals("true")) {
            analyzer.setShouldWriteToFile(true);
        }
        analyzer.run();
    }
}
