package eu.planets_project.ifr.core.storage.impl.jcr;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManager;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase;
import eu.planets_project.ifr.core.storage.api.query.Query;
import eu.planets_project.ifr.core.storage.api.query.QueryValidationException;
import eu.planets_project.ifr.core.storage.impl.util.PDURI;
import eu.planets_project.services.datatypes.DigitalObject;


/**
 * Implementation of the DigitalObjectManager interface based upon a JCR repository.
 */
public class JcrDigitalObjectManagerBase extends DigitalObjectManagerBase {
	/** The logger instance */
    private static Logger log = Logger.getLogger(JcrDigitalObjectManagerBase.class.getName());

	/** This is the root directory of this particular Data Registry */
	protected File _root = null;
	
	/** Public statics for the property names used to configure an instance */
	public final static String PATH_KEY = "manager.path";
	
	private static JcrDigitalObjectManagerImpl dom = null;

    //=============================================================================================
	// CONSTRUCTORS
	//=============================================================================================
    /**
     * No Arg Constructor, we don't want people to implement this 
     */
    protected JcrDigitalObjectManagerBase() {
    	super(null);
	}

    /**
     * {@inheritDoc}
     * @param config 
     */
    public JcrDigitalObjectManagerBase(Configuration config) {
    	super(config);
    	try {
        	String path = config.getString(PATH_KEY);
        	this.checkConstructorArguments(new File(path));
        	
            dom = (JcrDigitalObjectManagerImpl) JcrDigitalObjectManagerImpl.getInstance();
            log.fine("JcrDigitalObjectManagerBase root: " + ((JcrDigitalObjectManagerImpl)dom).getRootURI().toString());
        	
        	this._root = new File(path);
    	} catch (NoSuchElementException e) {
	    		throw new IllegalArgumentException("Path property with key " + PATH_KEY + " not found in config");
	    	}
	    }

		//=============================================================================================
		// BASE CLASS METHODS
		//=============================================================================================
		/**
		 * {@inheritDoc}
		 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI)
		 */
		@Override
		public List<URI> list(URI pdURI) {
			// The return array of URIs contains the contents for the passed URI
			List<URI> retVal = null;
			
			log.fine("JcrDigitalObjectManagerBase pdURI: " + pdURI);	

			try {
				// First lets look at the passed URI, if it's null then we need to return the root 
//				log.info("JcrDigitalObjectManagerBase list() Testing for null URI");
				if (pdURI == null)
				{
					log.info("JcrDigitalObjectManagerBase list() URI is empty so return root URI only");
					retVal = new ArrayList<URI>();
					retVal.add( this.id ); 
					return retVal; 
				}
//				log.info("JcrDigitalObjectManagerBase list() URI is NOT NULL");
				try {
//			        log.info("Replace HTTP path by registry path: " + pdURI);   
					URI baseUri = new PDURI(pdURI.normalize()).formDataRegistryRootURI();
//					log.info("JcrDigitalObjectManagerBase list() base URI " + baseUri);				

					if (pdURI.equals(baseUri)) {
						retVal = this.listFileLocation(pdURI);
					}

					if (retVal != null) {
//						log.info("Listing URIs");
						for (URI uri : retVal) {
							log.info("URI in list: " + uri);
						}
					} else {
						log.info("RetVal is NULL");
					}
					
				} catch (URISyntaxException e) {
					log.severe("URI Syntax exception");
					log.severe(e.getMessage());
					log.severe(e.getStackTrace().toString());
					e.printStackTrace();
					return null;
				}
            } catch (Exception e) {
                log.fine("JcrDigitalObjectManagerBase dom.list error: " + e.getMessage());
                // set default
	        	ArrayList<URI> _list = new ArrayList<URI>(0);
	        	_list.add(URI.create("empty"));
				retVal = _list;
            }

			return retVal;
		}
		
		
		/**
		 * This code take the actual file location and turns it into a listing.
		 * 
		 * @param pdURI
		 * @param fullPath
		 * @return
		 * @throws URISyntaxException
		 */
		protected List<URI> listFileLocation(URI pdURI) throws URISyntaxException {
	        List<URI> retVal = new ArrayList<URI>();
	        List<URI> tmpRetVal = new ArrayList<URI>();
			URI rootURI = ((JcrDigitalObjectManagerImpl)dom).getRootURI();
	        tmpRetVal = dom.list(rootURI);
//	        log.info("JcrDigitalObjectManagerBase listFileLocation() pdURI " + pdURI);
//	        log.info("JcrDigitalObjectManagerBase listFileLocation() Contents URI array has " + 
//	        		tmpRetVal.size() + " elements");
	        for (URI uri : tmpRetVal) {
	        	String leafname = "";
	            if(uri != null) {
	                leafname = uri.getPath();
//	                log.info("JcrDigitalObjectManagerBase listFileLocation() uri.getPath(): " + leafname);
	            }
//	            log.info("JcrDigitalObjectManagerBase listFileLocation() leafname: " + leafname +
//	            		", pdURI.toString(): " + pdURI.toString() + ", uri.toString(): " + uri.toString());

	           	URI resUri = URI.create(pdURI.toString() +"/"+ leafname).normalize();            
//	        	log.info("JcrDigitalObjectManagerBase listFileLocation() Adding URI " + resUri + " to list");
	            retVal.add( resUri );
	            }
	        return retVal;
		}


		/**
		 * This method evaluates original HTTP URI from registry URI
		 * @param uri The registry URI
		 * @return The original HTTP URI
		 */
		public static URI getOriginalUri(URI uri) {
			URI res = uri;
			try {
				URI baseUri = new PDURI(uri.normalize()).formDataRegistryRootURI();
//				log.info("JcrDigitalObjectManagerBase getOriginalUri() base URI " + baseUri);
				if (!uri.equals(baseUri)) {
					URI keyUri = URI.create(uri.toString().replaceAll(baseUri.toString(), ""));
//					log.info("JcrDigitalObjectManagerBase getOriginalUri() keyURI: " + keyUri);
					res = keyUri;
				} 
			} catch (Exception e) {
				log.info("JcrDigitalObjectManagerBase getOriginalUri() error: " + e.getMessage());				
			}

			return res;		
		}
		
				
		/**
		 * {@inheritDoc}
		 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#retrieve(java.net.URI)
		 */
		@Override
		public DigitalObject retrieve(URI pdURI)
				throws DigitalObjectNotFoundException {
			DigitalObject retObj = null;
			
			try {
				URI httpURI = getOriginalUri(pdURI);
				log.info("JcrDigitalObjectManagerBase retrieve() httpURI: " + httpURI + ", pdURI: " + pdURI);	
				if (!httpURI.equals(pdURI)) {
					retObj = dom.retrieve(httpURI);
				}
			} catch (Exception e) {
				log.info("JcrDigitalObjectManagerBase retrieve() error: " + e.getMessage());				
			}
			
			return retObj;
		}

	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#getQueryTypes()
	     */
	    @Override
		public List<Class<? extends Query>> getQueryTypes() {
	        return null;
	    }

	    
	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerbASE#storeAsNew()
	     */
	    @Override
		public URI storeAsNew(DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException {
			log.fine("FileSysDOM storeAsNew");
	        URI pdURI;
	        pdURI = dom.storeAsNew(digitalObject);
	        return pdURI;
	    }

	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#storeAsNew(java.net.URI, eu.planets_project.services.datatypes.DigitalObject)
	     */
	    @Override
		public URI storeAsNew(URI pdURI, DigitalObject digitalObject)
	            throws DigitalObjectNotStoredException {
	        this.store(pdURI, digitalObject);
	        return pdURI;
	    }

	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#updateExisting()
	     */
	    @Override
		public URI updateExisting(URI pdURI, DigitalObject digitalObject) throws eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotStoredException, eu.planets_project.ifr.core.storage.api.DigitalObjectManager.DigitalObjectNotFoundException {
	    	// First lets call retrieve to see if this exists
	    	this.retrieve(pdURI);
	        this.store(pdURI, digitalObject);
	        return pdURI;
	    }

	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#isWritable(java.net.URI)
	     */
	    @Override
		public boolean isWritable(URI pdURI) {
	        return true;
	    }

	    /**
	     * {@inheritDoc}
	     * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI, eu.planets_project.ifr.core.storage.api.query.Query)
	     */
	    @Override
		public List<URI> list(URI pdURI, Query q) throws QueryValidationException {
	        return null;
	    }

	    //=============================================================================================
		// FACTORY METHODS
		//=============================================================================================
		/**
		 * @param config
		 * 		A config object with the DOM details 
		 * @return
		 * 		A new FilesystemDigitalObjectManagerImpl instance based upon a root directory
		 * @throws IllegalArgumentException 
		 */
		public static DigitalObjectManager getInstance(Configuration config) throws IllegalArgumentException {
			return new JcrDigitalObjectManagerBase(config);
		}

	    //=============================================================================================
		// PRIVATE & PROTECTED METHODS
		//=============================================================================================
		private void checkConstructorArguments(File root) throws IllegalArgumentException {
			// Ensure root is not null
			if (root == null) {
				String message = "Supplied root dir is null";
				log.severe(message);
				throw new IllegalArgumentException(message);
			// first make sure it exists and is a directory
			} else if (root.exists()) {
				// OK root exists but it MUST be a directory
				if (!root.isDirectory()) {
					String message = root.getPath() + " is not a directory";
					log.severe(message);
					throw new IllegalArgumentException(message);
				}
			// It doesn't exist so lets create the directory
			} else {
				String message = "Directory " + root.getPath() + " doesn't exist";
				log.info(message);
				root.mkdirs();
			}
		}


		/**
		 * @param pdURI the URI
		 * @param digitalObject the object
		 * @throws DigitalObjectNotStoredException
		 */
		private void store(URI pdURI, DigitalObject digitalObject)
				throws DigitalObjectNotStoredException {

			log.fine("testing title");
			       if( digitalObject.getTitle() == null ) {
			          throw new DigitalObjectNotStoredException(
			        		"The DigitalObject titel was not found!");
			       }

			dom.store(pdURI, digitalObject);
		}

		/**
		 * This is just a helper to create a new URI reliably, using an existing one as a template.
		 * 
		 * @param oldPathUri
		 * @param newPath
		 * @return
		 * @throws URISyntaxException
		 */
	    static protected URI createNewPathUri(URI oldPathUri, String newPath)
	            throws URISyntaxException {
	        return new URI(oldPathUri.getScheme(), oldPathUri.getUserInfo(),
	                oldPathUri.getHost(), oldPathUri.getPort(), newPath, null, null);
	    }
	}
