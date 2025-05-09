package server;

public class OuputCobolMessage {

	private String _message = null;
	
	public OuputCobolMessage(Object obj) {
		this._message  = new String( (byte[]) obj);
	}

	public String getOutMessage() {
		return this._message;
	}

}
