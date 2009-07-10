package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.graphics.Image;


public abstract class AbstractIssueLinkHandlerFactory 
implements IssueLinkHandlerFactory
{
	private String name;
	private String categoryId;
	private Image image;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	
	public void setInitializationData(IConfigurationElement element, String elementName, Object obj) throws CoreException {
		setCategoryId(element.getAttribute("categoryId")); //$NON-NLS-1$
		setName(element.getAttribute("name")); //$NON-NLS-1$
	}
	
	public Image getImage() {
		return image;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}
}
