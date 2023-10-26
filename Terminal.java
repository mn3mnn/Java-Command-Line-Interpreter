
class Parser {
    String commandName;
    String[] args;
    //This method will divide the input into commandName and args
//where "input" is the string command entered by the user
    public boolean parse(String input){return true;}
    public String getCommandName(){ return ""; }
    public String[] getArgs(){return new String[0];}
}

public class Terminal {
    Parser parser;
    //Implement each command in a method, for example:
    public String pwd(){return "";}
    public void cd(String[] args){return;}
// ...

    //This method will choose the suitable command method to be called
    public void chooseCommandAction(){
        System.out.println("Choose command action");
        return;}
    public static void main(String[] args){
        Terminal terminal = new Terminal();
        terminal.chooseCommandAction();
    }
}
