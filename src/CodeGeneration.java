import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

public class CodeGeneration {
    private ArrayList<String> assembly;
    private ArrayList<String> forward = new ArrayList<>();
    Charset charset;
    public CodeGeneration(){
        assembly = new ArrayList<>();
        charset = Charset.forName("UTF-8");
    }
    public void Program(String name){
        assembly.add(name + "\t" + "START" + "\t" + "0");
        assembly.add("\t\t" + "EXTREF" + "\t" + "XREAD, XWRITE");
        assembly.add("\t\t" + "STL" + "\t\t" + "RETADR");
        assembly.add("\t\t" + "J" + "\t\t" + "EXADDR");
        assembly.add("RETADR" + "\t" + "RESW" + "\t" + "1");
    }

    public void VAR(ArrayList<String> vars){
        for (int i = 0; i < vars.size(); i++) {
            assembly.add(vars.get(i) + "\t\t" + "RESW" + "\t" + "1");
        }
    }

    public void Read(ArrayList<String> vars){
        assembly.add("\t\t" + "+JSUB" + "\t" + "XREAD");
        assembly.add("\t\t" + "WORD" + "\t" + vars.size());
        for (int i = 0; i < vars.size() ; i++) {
            assembly.add("\t\t" + "WORD" + "\t" + vars.get(i));
        }
    }

    public void Write(ArrayList<String> vars){
        assembly.add("\t\t" + "+JSUB" + "\t" + "XWRITE");
        assembly.add("\t\t" + "WORD" + "\t" + vars.size());
        for (int i = 0; i < vars.size() ; i++) {
            assembly.add("\t\t" + "WORD" + "\t" + vars.get(i));
        }
    }

    public void Assign(ArrayList<String> postfix, String assigned){
        String value1, value2, Ti;
        String regA = null;
        int counter = 1;
        for (int i = 0; i < postfix.size() ; i++) {
            if (postfix.get(i).equals("13")) {
                postfix.remove(i--);
                value1 = postfix.remove(i--);
                value2 = postfix.remove(i);
                if (value1.equals(regA)) {
                    assembly.add("\t\t" + "ADD" + "\t\t" + value2);
                    regA = value2 + "+" + value1;
                } else if (value2.equals(regA)) {
                    assembly.add("\t\t" + "ADD" + "\t\t" + value1);
                    regA = value2 + "+" + value1;
                } else {
                    if (regA == null) {
                        assembly.add("\t\t" + "LDA" + "\t\t" + value1);
                        regA = value2 + "+" + value1;
                    } else {
                        Ti = "T" + counter++;
                        postfix.add(postfix.indexOf(regA), Ti);
                        postfix.remove(regA);
                        assembly.add("\t\t" + "STA" + "\t\t" + Ti);
                        forward.add(Ti);
                        assembly.add("\t\t" + "LDA" + "\t\t" + value1);
                        regA = Ti;
                    }
                    assembly.add("\t\t" + "ADD" + "\t\t" + value2);
                }
                postfix.add(i,regA);
            } else if (postfix.get(i).equals("18")) {
                postfix.remove(i--);
                value1 = postfix.remove(i--);
                value2 = postfix.remove(i);
                if (value1.equals(regA)) {
                    assembly.add("\t\t" + "MUL" + "\t\t" + value2);
                    regA = value2 + "+" + value1;
                } else if (value2.equals(regA)) {
                    assembly.add("\t\t" + "MUL" + "\t\t" + value1);
                    regA = value2 + "+" + value1;
                } else {
                    if (regA == null) {
                        assembly.add("\t\t" + "LDA" + "\t\t" + value1);
                        regA = value2 + "*" + value1;
                    } else {
                        Ti = "T" + counter++;
                        postfix.add(postfix.indexOf(regA), Ti);
                        postfix.remove(regA);
                        assembly.add("\t\t" + "STA" + "\t\t" + Ti);
                        forward.add(Ti);
                        assembly.add("\t\t" + "LDA" + "\t\t" + value1);
                        regA = Ti;
                    }
                    assembly.add("\t\t" + "MUL" + "\t\t" + value2);
                }
                postfix.add(i,regA);
            }
        }
        assembly.add("\t\t" + "STA" + "\t\t" + assigned);
    }

    public void End(){
        assembly.add("\t\t" + "LDL" + "\t\t" + "RETADR");
        assembly.add("\t\t" + "RSUB");
        for (int i = 0; i < forward.size(); i++) {
            assembly.add(forward.get(i) + "\t\t" + "LDL" + "\t\t" + "RETADR");
        }
        assembly.add("\t\t" + "END");
        Path path2=Paths.get("./src/assembly.txt");
        try {
            Files.write(path2,assembly,charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private int prec(String ch)
    {
        if(ch.equals("13")){
            return 1;
        }else if(ch.equals("18")){
            return 2;
        }else{
            return -1;
        }
    }

    // to postfix expression.
    public ArrayList<String> infixToPostfix(ArrayList<String> exp) {
        // initializing empty String for result
        ArrayList<String> result = new ArrayList<>();

        // initializing empty stack
        Stack<String> stack = new Stack<>();

        for (int i = 0; i < exp.size(); ++i) {
            String s = exp.get(i);

            // If the scanned character is an operand, add it to output.
            if(Pattern.matches("[a-zA-Z]+",s)){
                //System.out.println(s);
                result.add(s);
            }
            // If the scanned character is an '(', push it to the stack.
            else if (s.equals("15")) {
                stack.push(s);
            }
            //  If the scanned character is an ')', pop and output from the stack
            // until an '(' is encountered.
            else if (s.equals("16")) {
                while (!stack.isEmpty() && !stack.peek().equals("15")){
                    result.add(stack.pop());
                }
                stack.pop();
            }// an operator is encountered
            else{
                while (!stack.isEmpty() && prec(s) <= prec(stack.peek()))
                    result.add(stack.pop());
                stack.push(s);
            }
        }
        // pop all the operators from the stack
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        return result;
    }


}

