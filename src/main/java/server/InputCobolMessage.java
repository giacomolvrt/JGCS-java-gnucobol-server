package server;

public class InputCobolMessage {
	
	private String _message = null;

	public InputCobolMessage(Object obj) {
		
		this._message = (String) obj; 
	}
	
	public String getMessage() {
		return this._message;
	}

}
