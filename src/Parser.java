import java.util.ArrayList;

public class Parser {
    private int index = 0;
    CodeGeneration generator = new CodeGeneration();
    ArrayList<String> vars = new ArrayList<>();
    ArrayList<String> infix = new ArrayList<>();
    ArrayList<String> postfix = new ArrayList<>();
    String assigned;
    public boolean Parse(ArrayList<Token> tokens){
        boolean found = false;
        if(tokens.get(index).tokenType==1){
            index++;
            if(tokens.get(index).tokenType==17){
                generator.Program(tokens.get(index).tokenSpecifier);
                index++;
                if(tokens.get(index).tokenType==2){
                    index++;
                    if(IdList(tokens)){
                        generator.VAR(vars);
                        if(tokens.get(index).tokenType==3){
                            index++;
                            if(StatementList(tokens)){
                                if(tokens.get(index).tokenType==5){
                                    found = true;
                                    generator.End();
                                }
                            }
                        }
                    }
                }
            }
        }
        return found;
    }
    //to check for the IdList grammar
    private boolean IdList(ArrayList<Token> tokens){
        boolean found = false;
        if(tokens.get(index).tokenType == 17){
            found = true;
            vars.add(tokens.get(index).tokenSpecifier);
            index++;
            while(tokens.get(index).tokenType==14 && found){
                index++;
                if(tokens.get(index).tokenType==17) {
                    vars.add(tokens.get(index).tokenSpecifier);
                    index++;
                }
                else
                    found = false;
            }
        }
        return found;
    }

    private boolean StatementList(ArrayList<Token> tokens){
        boolean found = false;
        if(Statement(tokens)){
            found=true;
        }
        while(tokens.get(index).tokenType==11 && found){
            index++;
            if(!Statement(tokens)){
                found = false;
            }
        }
        return found;
    }

    private boolean Statement(ArrayList<Token> tokens){
        boolean found = false;
        switch (tokens.get(index).tokenType){
            case 7:
                index++;
                found = RW(tokens);
                if(found){
                    generator.Read(vars);
                }
                break;
            case 8:
                index++;
                found = RW(tokens);
                if(found){
                    generator.Write(vars);

                }
                break;
            default:
                found = Assign(tokens);
                if(found){
                    postfix = generator.infixToPostfix(infix);
                    generator.Assign(postfix,assigned);
                    System.out.println("\n");
                    infix.clear();
                }

        }
        return found;
    }

    private boolean RW(ArrayList<Token> tokens){
        boolean found = false;
        vars.clear();
        if(tokens.get(index).tokenType==15){
            index++;
            if(IdList(tokens)){
                if(tokens.get(index).tokenType==16){
                    found=true;
                    index++;
                }
            }
        }
        return found;
    }

    private boolean Assign(ArrayList<Token> tokens){
        boolean found = false;
        if(tokens.get(index).tokenType==17){
            assigned = tokens.get(index).tokenSpecifier;
            index++;
            if(tokens.get(index).tokenType==12){
                index++;
                if(Exp(tokens)){
                    found = true;
                }
            }
        }
        return found;
    }

    private boolean Exp(ArrayList<Token> tokens){
        boolean found = false;
        if(Factor(tokens)){
            if(tokens.get(index).tokenType==13||tokens.get(index).tokenType==18){
                infix.add(Integer.toString(tokens.get(index).tokenType));
                index++;
                if(Factor(tokens)){
                    found = true;
                }
            }
        }
        return found;
    }

    private boolean Factor(ArrayList<Token> tokens){
        boolean found = false;
        if(tokens.get(index).tokenType==17){
            infix.add(tokens.get(index).tokenSpecifier);
            index++;
            found = true;
        }
        else if(tokens.get(index).tokenType==15){
            infix.add(Integer.toString(tokens.get(index).tokenType));
            index++;
            if(Exp(tokens))
                if(tokens.get(index).tokenType==16){
                    infix.add(Integer.toString(tokens.get(index).tokenType));
                    index++;
                    found= true;
                }
        }
        return found;
    }
}