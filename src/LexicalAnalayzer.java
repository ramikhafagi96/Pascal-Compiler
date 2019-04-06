import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalayzer {

    public static void main(String[] args) {
        HashMap<String, Integer> tokenTable = new HashMap<>();
        fillTable(tokenTable);
        FileReader reader = new FileReader();
        ArrayList<String> program = new ArrayList<>();
        program = (ArrayList<String>) reader.readFileInList("./src/sumProgram.txt");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens = Lexical(program,tokenTable);
        Token token = new Token("null",11);
        //tokens.add(25,token); for the previous method of tokenization
        Parser parser = new Parser();
        boolean flag  = parser.Parse(tokens);
        if(flag==false) {
            System.err.println("ERROR");
        }else{
            System.out.println("SUCCESS");
        }
    }

    public static void fillTable(HashMap<String,Integer> table){
        table.put("PROGRAM ",1);
        table.put("VAR",2);
        table.put("BEGIN ",3);
        table.put("END",4);
        table.put("END.",5);
        table.put("FOR",6);
        table.put("READ",7);
        table.put("WRITE",8);
        table.put("TO",9);
        table.put("DO",10);
        table.put(";",11);
        table.put(":=",12);
        table.put("+",13);
        table.put(",",14);
        table.put("(",15);
        table.put(")",16);
        table.put("id",17);
        table.put("*",18);
    }

    public static ArrayList<Token> Lexical(ArrayList<String> program, HashMap<String,Integer> table){
        ArrayList<Token> tokens = new ArrayList<Token>();
        String regex = "(PROGRAM |VAR |BEGIN |END\\.|END |FOR |READ |WRITE |TO |DO |;|:=|\\+|,|\\(|\\)|\\*|[a-zA-Z]+)";
        Pattern pattern = Pattern.compile(regex);
        Token token;
        int tokenType;
        for(int i=0 ; i<program.size() ; i++){
            String string = program.get(i);
            Matcher m = pattern.matcher(string);
            while(m.find()){
                tokenType = regexFunc(m.group(),table);
                if(tokenType==17){
                    token = new Token(m.group(),tokenType);
                    tokens.add(token);
                }
                else if(tokenType>-1){
                    token = new Token("null",tokenType);
                    tokens.add(token);
                }
                else{
                    System.err.print("Doesn't Satisfy The Scheme");
                    System.exit(0);
                }
            }
        }
        return tokens;
    }

    public static int regexFunc(String string,HashMap<String,Integer> table){
        String idRegex = "[a-zA-Z]+";
        Pattern idPattern  = Pattern.compile(idRegex);
        Matcher m = idPattern.matcher(string);
        if(table.containsKey(string))
            return table.get(string);
        else if(m.find())
            return table.get("id");
        else
            return -1;

    }

}
