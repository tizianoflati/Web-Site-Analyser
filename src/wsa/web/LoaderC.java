package wsa.web;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import wsa.web.html.ParsedC;

public class LoaderC implements Loader {

	public static Random rand = new Random(System.currentTimeMillis());
	
	@Override
	public LoadResult load(URL url) {

		try {
			long random = 1000 * (1+rand.nextInt(5));
			System.out.println("WAITING " + random/1000 + "s BEFORE DOWNLOADING");
			Thread.sleep(random);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		ParsedC parsed = null;
		
		Exception exception = null;
		URLConnection connection = null;
		
		try
		{
			// Set up the connection
			connection = url.openConnection();
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			
			// Connect
			connection.connect();
			
			HTMLEditorKit kit = new HTMLEditorKit();
			HTMLDocument document = (HTMLDocument) kit.createDefaultDocument();
			document.putProperty("IgnoreCharsetDirective", new Boolean(true));
			kit.read(connection.getInputStream(), document, 0);
			
			System.out.println("Raw-data correctly downloaded: " + url);
			
			// Parse it
			parsed = new ParsedC(document);
		}
		catch (IOException e)
		{
			new IOException(e.getMessage() + " for url: " + url).printStackTrace();
//			e.printStackTrace();
			exception = e;
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
			exception = e;
		}
		finally
		{
			try
			{
				if(connection != null) connection.getInputStream().close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				exception = e;
			}
		}
		
		if(exception != null) System.out.println("RETURNING FAILURE: " + url);
		
		return new LoadResult(url, parsed, exception);
	}
	

	@Override
	public Exception check(URL url) {
		try
		{
			URLConnection connection = url.openConnection();
			connection.connect();
		} catch (IOException e) {
			return e;
		}
		
		return null;
	}
}