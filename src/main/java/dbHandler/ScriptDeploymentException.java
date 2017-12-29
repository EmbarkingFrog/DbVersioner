package dbHandler;

public class ScriptDeploymentException extends Exception {
    public ScriptDeploymentException(Exception e){
        super(e);
    }

    public ScriptDeploymentException(String message){
        super(message);
    }

    public ScriptDeploymentException(String message, Exception e){
        super(message, e);
    }
}
