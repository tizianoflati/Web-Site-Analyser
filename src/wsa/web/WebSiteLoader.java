package wsa.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Set;

public class WebSiteLoader {
	
	public void load(Set<URI> loaded, Set<URI> toLoad, Set<URI> errs, File dir, URI name) {
		File file = new File(dir, name.toString() + ".wsa" );
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader( new FileReader(file));
			String dom = bufferedReader.readLine();
			
			// TODO
			
			String exceptionName = bufferedReader.readLine();
			String exceptionMessage = bufferedReader.readLine();
			
			try {
				
				Constructor<?> constructor = Class.forName(exceptionName).getConstructor(String.class);
				Exception e = (Exception)(constructor.newInstance(exceptionMessage));
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
