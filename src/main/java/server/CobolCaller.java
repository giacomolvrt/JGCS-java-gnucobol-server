package server;

import java.util.ResourceBundle;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import cobol.DriverCobolModule;
import cobol.GnuCobolLibrary;

public class CobolCaller {

	private String properties_file = "cobolpath";
	private final int JAVA_MEMORY_PARAM_LENGHT = 32000;
	private OuputCobolMessage ocm = null; 
	
	public CobolCaller(InputCobolMessage icm, String par1, String par2, String par3, boolean b) {
		
		if (b) {
			System.out.println("CobolCaller parameters: "+par1+" "+par2+" "+par3);
		}
		
		ResourceBundle prop = ResourceBundle.getBundle(properties_file);
		
		System.setProperty("jna.library.path", prop.getString("jnalibrarypath_cobol_runtime"));
		
		try {
			GnuCobolLibrary.INSTANCE.cob_init(0, null);
		} catch (UnsatisfiedLinkError e) {
			System.out.println("GnuCobolLibrary Exception" + e);
		}
		
		System.setProperty("jna.library.path", prop.getString("jnalibrarypath_cobol_modules"));
		
		try {
			Pointer pointer;
			pointer = new Memory(JAVA_MEMORY_PARAM_LENGHT); //modificare allo spazio voluto
			//byte space = 32;                         //carattere 32 equivale a SPACE
			//pointer.setMemory(0, 32000, space);
			byte HIGH_VALUE = (byte) 0xFF;             //per fare HIGH-VALUE il valore esadecimale è 0XFF
			pointer.setMemory(0, JAVA_MEMORY_PARAM_LENGHT, HIGH_VALUE);
			
			byte[] data = Native.toByteArray(icm.getMessage());
			
			pointer.write(0, data, 0, data.length - 1);

			int rc = DriverCobolModule.INSTANCE.TEC00001(pointer);
			
			byte[] updateData = pointer.getByteArray(0, JAVA_MEMORY_PARAM_LENGHT);
			ocm = new OuputCobolMessage(updateData);
			
			
			if (b) {
				System.out.print("Return code:");
				System.out.println(rc);
			}

		} catch (UnsatisfiedLinkError e) {
			System.out.println("CobolCaller Exception" + e);
		}

	}

	public OuputCobolMessage getOutbound() {
		
		return ocm;
	}

}
