package Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.util.Base64InputStream;
import android.util.Base64OutputStream;

public class ObjectStringConverter {

	public static String objectToString(Serializable object) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			new ObjectOutputStream(out).writeObject(object);
			byte[] data = out.toByteArray();
			out.close();
			
			out = new ByteArrayOutputStream();
			Base64OutputStream b64 = new Base64OutputStream(out, 0);
			b64.write(data);
			b64.close();
			out.close();
			
			return new String(out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object stringToObject(String encodedObject) {
		try {
			return new ObjectInputStream(new Base64InputStream(new ByteArrayInputStream(encodedObject.getBytes()), 0)).readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
