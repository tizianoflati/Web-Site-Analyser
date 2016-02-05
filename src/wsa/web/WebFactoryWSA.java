package wsa.web;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class WebFactoryWSA extends WebFactory{
	
	private static LoaderFactory factory = new LoaderFactoryC();
	
	/** Imposta la factory per creare Loader
     * @param lf  factory per Loader */
    public static void setLoaderFactory(LoaderFactory lf) {
    	factory = lf;
    }

    /** Ritorna un nuovo Loader. Se non è stata impostata una factory tramite il
     * metodo setLoaderFactory, il Loader è creato tramite l'implementazione di
     * default, altrimenti il Loader è creati tramite la factory impostata
     * con setLoaderFactory.
     * @return un nuovo Loader */
    public static Loader getLoader() {
    	return factory.newInstance();
    }

    /** Ritorna un nuovo loader asincrono che per scaricare le pagine usa
     * esclusivamente Loader forniti da getLoader.
     * @return un nuovo loader asincrono. */
    public static AsyncLoader getAsyncLoader() {
        return new AsyncLoaderC(getLoader());
    }

    /** Ritorna un Crawler che inizia con gli specificati insiemi di URI.
     * Per scaricare le pagine usa esclusivamente AsyncLoader fornito da
     * getAsyncLoader.
     * @param loaded  insieme URI scaricati
     * @param toLoad  insieme URI da scaricare
     * @param errs  insieme URI con errori
     * @param pageLink  determina gli URI per i quali i link contenuti nelle
     *                  relative pagine sono usati per continuare il crawling
     * @return un Crawler con le proprietà specificate */
    public static Crawler getCrawler(Collection<URI> loaded,
                                     Collection<URI> toLoad,
                                     Collection<URI> errs,
                                     Predicate<URI> pageLink) {
    	return new CrawlerC(getAsyncLoader(), loaded, toLoad, errs, pageLink);
    }


    /** Ritorna un SiteCrawler. Se dom e dir sono entrambi non null, assume che
     * sia un nuovo web site con dominio dom da archiviare nella directory dir.
     * Se dom non è null e dir è null, l'esplorazione del web site con dominio
     * dom sarà eseguita senza archiviazione. Se dom è null e dir non è null,
     * assume che l'esplorazione del web site sia già archiviata nella
     * directory dir e la apre. Per scaricare le pagine usa esclusivamente un
     * Crawler fornito da getCrawler.
     * @param dom  un dominio o null
     * @param dir  un percorso di una directory o null
     * @throws IllegalArgumentException se dom e dir sono entrambi null o dom è
     * diverso da null e non è un dominio o dir è diverso da null non è una
     * directory o dom è null e dir non contiene l'archivio di un SiteCrawler.
     * @throws IOException se accade un errore durante l'accesso all'archivio
     * del SiteCrawler
     * @return un SiteCrawler */
    public static SiteCrawler getSiteCrawler(URI dom, Path dir)
            throws IOException {
    	
    	if(dom == null && dir == null) throw new IllegalArgumentException();
    	if(dom != null && !SiteCrawlerC.checkDomain(dom)) throw new IllegalArgumentException();
    	if(dir != null && !dir.toFile().isDirectory()) throw new IllegalArgumentException();
    	
    	
    	Predicate<URI> pageLink = (u) -> {return SiteCrawlerC.checkSeed(dom, u);};
    	
    	// Se dom e dir sono entrambi non null, assume che sia un nuovo web site
    	// con dominio dom da archiviare nella directory dir.
    	if(dom != null && dir != null)
    		return new SiteCrawlerC(getCrawler(new HashSet<URI>(), new HashSet<URI>(), new HashSet<URI>(), pageLink), dom, dir);
    	
    	// Se dom non è null e dir è null, l'esplorazione del web site con dominio
        // dom sarà eseguita senza archiviazione.
    	if(dom != null && dir == null)
    		return new SiteCrawlerC(getCrawler(new HashSet<URI>(), new HashSet<URI>(), new HashSet<URI>(), pageLink), dom);
    	
    	// Se dom è null e dir non è null, assume che l'esplorazione del web site
    	// sia già archiviata nella directory dir e la apre.
    	if(dom == null && dir != null)
    	{
    		Set<URI> loaded = new HashSet<URI>();
    		Set<URI> toLoad = new HashSet<URI>();
    		Set<URI> errs = new HashSet<URI>();
    		List<CrawlerResult> crawlerResultsList = new ArrayList<CrawlerResult>();
    		
    		
    		// Load from disk
    		URI domain = WebSiteLoader.load(crawlerResultsList, loaded, toLoad, errs, dir.toFile());
    		
    		return new SiteCrawlerC(getCrawler(loaded, toLoad, errs, pageLink), domain, dir, crawlerResultsList);
    	}
    	
    	return null;
    }
}
