package org.nightlabs.jfire.issuetracking.ui.overview;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;

/**
 * @author Chairat Kongarayawetchakun chairatk [at] NightLabs [dot] de
 *
 */
public abstract class AbstractIssueCategoryFactory 
implements IssueCategoryFactory 
{
	private static final Logger logger = Logger.getLogger(AbstractIssueCategoryFactory.class);
	
	public static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ICON = "icon"; //$NON-NLS-1$
	public static final String ATTRIBUTE_INDEX = "index"; //$NON-NLS-1$
	
	private int index = -1;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	private Image image;
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	
	private String name = null;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setInitializationData(IConfigurationElement element, 
			String propertyName, Object data) 
	throws CoreException 
	{
		String name = element.getAttribute(ATTRIBUTE_NAME);
		String iconString = element.getAttribute(ATTRIBUTE_ICON);
		String indexString = element.getAttribute(ATTRIBUTE_INDEX);
		if (AbstractEPProcessor.checkString(name))
			setName(name);
		if (AbstractEPProcessor.checkString(iconString)) {
			ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					element.getNamespaceIdentifier(), iconString);
			if (imageDescriptor != null)
				setImage(imageDescriptor.createImage());										
		}
		if (AbstractEPProcessor.checkString(indexString)) {
			try {
				int index = Integer.valueOf(indexString);
				setIndex(index);
			} catch (NumberFormatException e) {
				logger.error("Attribute index "+indexString+" is not valid number!"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

}
