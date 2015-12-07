package wsa.web;

public class LoaderFactoryC implements LoaderFactory {

	@Override
	public Loader newInstance() {
		return new LoaderC();
	}

}
