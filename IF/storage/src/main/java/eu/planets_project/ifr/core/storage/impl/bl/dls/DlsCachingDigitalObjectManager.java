/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.bl.dls;

import java.io.File;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eu.planets_project.ifr.core.common.conf.Configuration;
import eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase;
import eu.planets_project.ifr.core.storage.impl.bl.dls.uitls.DlsItemList;

/**
 * This is a DigitalObjectManager implementation that acts as a caching
 * manager for the British Library's Digital Library System (DLS).
 * 
 * The DLS is a dumb bit store designed to solve the problems of long term
 * byte sequence storage.  It knows little about the semantic meaning of
 * its contents.  Items are stored as two objects:
 * 
 *   - A content object, the byte sequence to be preserved
 *   - A metadata object, a METS file describing the byte sequence
 *   
 * DLS sees both of these as distinct byte sequences, and is unaware of the
 * link between them.
 * 
 * To solve this issue this DOM uses a list of paired IDs, so that it is aware
 * the digital objects it holds.  When an item is requested, the DOM looks to
 * see if it has it in it's list.  If so it will:
 * 
 *   - Deliver it from its internal cache if it exists
 *   
 *   - Request the content and metadata pair from DLS and deliver / cache them.
 * 
 * Initially it's designed to run on a test system populated with a small
 * set of objects.  The contents are driven by an XML resource file:
 * 
 *   - pserv/IF/storage/src/main/resources/eu/planets_project/ifr/core/storage/bl/dls/domItems.xml
 *
 * @author <a href="mailto:carl.wilson@bl.uk">Carl Wilson</a>
 */
public class DlsCachingDigitalObjectManager extends DigitalObjectManagerBase {
	/** The key for the item list location property */
	public static final String LIST_LOC_KEY = "manager.itemList.location";
	private DlsItemList theList = null;

	protected DlsCachingDigitalObjectManager(Configuration config) {
		// Call the base constructor
		super(config);
		// If no list then parse it
		if (this.theList == null)
			this.theList = new DlsItemList(new File(config.getString(LIST_LOC_KEY)));
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see eu.planets_project.ifr.core.storage.api.DigitalObjectManagerBase#list(java.net.URI)
	 */
	@Override
	public List<URI> list(URI pdURI) {
		List<URI> result = new ArrayList<URI>();
		if (pdURI == null) {
			result.add(this.getId());
		} else {
			for (Integer itemId : this.theList.getItemMap().keySet()) {
				result.add(URI.create(this.getId() + "/" + String.valueOf(itemId)));
			}
		}
		for (URI uri : result) {
			System.out.println("item: " + uri);
		}
		return result;
	}
}
