/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.entity.EntityEditorRegistry;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageSettings;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * This registry processes the extension-point <code>org.nightlabs.jfire.trade.ui.articleContainerEditorPageFactory</code>.
 * The extension-point allows for the registration of {@link IEntityEditorPageFactory}s on the basis
 * of an editor-id as well the type (class) of the currently edited {@link ArticleContainer}.
 * <p>
 * Additionally to the editorID and articleContainerClass pages are registered with an id-String. 
 * When resolving the page-factories for an editor and the ArticleContainer it edits one factory
 * will be returned for each id. As multiple registrations are allowed for one id (for different
 * articleContainerClasses) the one that first matches when running through the the class/interface-hierarchy
 * will be returned.  
 * </p> 
 *  
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ArticleContainerEditorPageFactoryRegistry extends AbstractEPProcessor {
	
	protected static final String FACTOY_ELEMENT_NAME = "articleContainerEditorPageFactory"; //$NON-NLS-1$
	protected static final String ARTICLE_CONTAINER_ATTRIBUTE_NAME = "articleContainerClass"; //$NON-NLS-1$
	public static final String EXTENSION_POINT_ID = TradePlugin.getDefault().getBundle().getSymbolicName() + "." + FACTOY_ELEMENT_NAME; //$NON-NLS-1$
	

	/**
	 * Maps editorID to a map from page-factory-id the map of
	 * the registrations per class for this id.
	 */
	private Map<String, Map<String, Map<String, EntityEditorPageSettings>>> pageSettings = new HashMap<String, Map<String,Map<String,EntityEditorPageSettings>>>();
	
	/**
	 * 
	 */
	public ArticleContainerEditorPageFactoryRegistry() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element) throws Exception {
		if (element.getName().equals(FACTOY_ELEMENT_NAME)) {
			String editorID = element.getAttribute("editorID"); //$NON-NLS-1$
			String id = element.getAttribute("id"); //$NON-NLS-1$
			String articleContainerClass = element.getAttribute("articleContainerClass"); //$NON-NLS-1$
			addPage(editorID, id, articleContainerClass, new EntityEditorPageSettings(extension, element));
		}
	}
	
	public void addPage(String editorID, String id, String articleContainerClass, EntityEditorPageSettings settings) {
		if (editorID == null || "".equals(editorID)) //$NON-NLS-1$
			throw new IllegalArgumentException("Missing/Invalid editorID for page settings: " + settings); //$NON-NLS-1$
		if (id == null || "".equals(id)) //$NON-NLS-1$
			throw new IllegalArgumentException("Missing/Invalid id for page settings: " + settings); //$NON-NLS-1$
		if (articleContainerClass == null || "".equals(articleContainerClass)) //$NON-NLS-1$
			throw new IllegalArgumentException("Missing/Invalid articleContainerClass for page settings: " + settings); //$NON-NLS-1$
		
		Map<String, Map<String, EntityEditorPageSettings>> editorID2PageSettings = pageSettings.get(editorID);
		if (editorID2PageSettings == null) {
			editorID2PageSettings = new HashMap<String, Map<String,EntityEditorPageSettings>>();
			pageSettings.put(editorID, editorID2PageSettings);
		}
		Map<String, EntityEditorPageSettings> class2PageSetting = editorID2PageSettings.get(id);
		if(class2PageSetting == null) {
			class2PageSetting = new HashMap<String, EntityEditorPageSettings>();
			editorID2PageSettings.put(id, class2PageSetting);
		}
		EntityEditorPageSettings pageSettings = class2PageSetting.get(articleContainerClass);
		if(pageSettings != null)
			throw new IllegalStateException("An entityEditorPageFactory was already registered for: " + //$NON-NLS-1$
					"editorID = " + editorID +  //$NON-NLS-1$
					", id = " + id + //$NON-NLS-1$
					", articleContainerClass = " + articleContainerClass); //$NON-NLS-1$
		class2PageSetting.put(articleContainerClass, settings);
	}
	
	/**
	 * Get the unsorted set of {@link EntityEditorPageSettings} for the registrations 
	 * to the articleContainerEditorPageFactory extension-point.
	 * <p>
	 * For each id the first matching setting will be returned when running through
	 * the class/interface-hierarchy of the given articleContainerClass. 
	 * </p>
	 * 
	 * @param editorID The editorID to search registrations for.
	 * @param articleContainerClass The type (class) of ArticleContainer to search registrations for.
	 */
	public Set<EntityEditorPageSettings> getPageSettings(String editorID, Class<?> articleContainerClass) {
		checkProcessing();
		Class<?> acClass = articleContainerClass; 
		Set<EntityEditorPageSettings> result = null;
		Map<String, Map<String, EntityEditorPageSettings>> editorRegistrations = pageSettings.get(editorID);
		for (String pageId : editorRegistrations.keySet()) {
			Class<?> searchClass = acClass;
			EntityEditorPageSettings settings = null;
			searchClassLoop: while (searchClass != null) {
				settings = getEntityEditorPageSettings(editorID, pageId, searchClass.getName());
				if (settings != null)
					break;

				Class<?>[] interfaces = searchClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					settings = getEntityEditorPageSettings(editorID, pageId, interfaces[i].getName());
					if (settings != null)
						break searchClassLoop;
				}
				
				searchClass = searchClass.getSuperclass();
			}
			if (settings != null) {
				if (result == null)
					result = new HashSet<EntityEditorPageSettings>();
				result.add(settings);
			}
		}
		return result;
	}
	
	private EntityEditorPageSettings getEntityEditorPageSettings(String editorID, String id, String articleContainerClass) {
		Map<String, Map<String, EntityEditorPageSettings>> id2Class2Settings = pageSettings.get(editorID);
		if (id2Class2Settings == null)
			return null;
		Map<String, EntityEditorPageSettings> class2Settings = id2Class2Settings.get(id);
		if (class2Settings == null)
			return null;
		return class2Settings.get(articleContainerClass);
	}
	
	/**
	 * Returns the ordered {@link EntityEditorPageSettings} that are merged from registrations to the
	 * <code>articleContainerEditorPageFactory</code> extension-point as well as to the
	 * <code>entityEditor</code> extension-point.   
	 *  
	 * @param editorID The editorID to search registrations for.
	 * @param articleContainerClass The class of ArticleContainer to search registrations for.
	 */
	public List<EntityEditorPageSettings> getPagesSettingsOrdered(String editorID, Class<?> articleContainerClass) {
		Set<EntityEditorPageSettings> baseSettings = EntityEditorRegistry.sharedInstance().getPageSettings(editorID);
		Set<EntityEditorPageSettings> articleContainerSettings = getPageSettings(editorID, articleContainerClass);
		List<EntityEditorPageSettings> settingsOrdered = new LinkedList<EntityEditorPageSettings>();
		if (articleContainerSettings != null)
			settingsOrdered.addAll(articleContainerSettings);
		if (baseSettings != null)
			settingsOrdered.addAll(baseSettings);
		Collections.sort(settingsOrdered);
		return settingsOrdered;
	}
	
	private static ArticleContainerEditorPageFactoryRegistry sharedInstance;

	/**
	 * Returns and lazily creates a static instance of ArticleContainerEditorPageFactoryRegistry
	 */
	public static ArticleContainerEditorPageFactoryRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (ArticleContainerEditorPageFactoryRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new ArticleContainerEditorPageFactoryRegistry();
					sharedInstance.process();
				}
			}
		}
		return sharedInstance;
	}
	
}
