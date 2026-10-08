
// AI assistance (Claude, Anthropic): extending RuntimeException lets us throw and
// catch our own error type without adding "throws" to every method.
public class MiniLangError extends RuntimeException{
    private String category;
    private int line;
    private String info;

    public MiniLangError(String category, int line, String info){
        this.category = category;
        this.line = line;
        this.info = info;
    }

    public String getErrorReport(){
        return category + " Error on line " + line + ": " + info;
    }
}
