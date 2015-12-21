package wsa.web;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import wsa.web.html.ParsedC;

public class LoaderC implements Loader {
	private Exception exception = null;

	public LoaderC() {
	}
	
	@Override
	public LoadResult load(URL url) {

		ParsedC parsed = null;
		
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
			kit. read(connection.getInputStream(), document, 0);
			
			System.out.println("Pagina scaricata correttamente: " + url);
			
			// Parse it
			parsed = new ParsedC(document);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.exception = e;
		}
		catch (BadLocationException e) {
			e.printStackTrace();
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
			}
		}
		
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