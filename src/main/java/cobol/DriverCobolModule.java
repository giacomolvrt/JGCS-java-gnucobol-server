package cobol;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;


public interface DriverCobolModule extends Library {
	
	final String COBOL_MODULE_NAME = "TEC00001";
	
	DriverCobolModule INSTANCE = (DriverCobolModule) Native.loadLibrary("TEC00001", DriverCobolModule.class);

	int TEC00001(Pointer aValue);
}
