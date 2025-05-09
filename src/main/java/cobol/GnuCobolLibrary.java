package cobol;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public interface GnuCobolLibrary extends Library {
	GnuCobolLibrary INSTANCE = (GnuCobolLibrary) Native.loadLibrary("cob", GnuCobolLibrary.class);

	void cob_init(int argc, Pointer argv);
}

