/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.viewer.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;

/**
 * Wrapper composite for a SWT {@link Browser} that
 * lazily creates it when it is pointed to an url.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class BrowserWrapperComposite extends XComposite {


	private Browser browser;
	
	/**
	 * @param parent
	 * @param style
	 */
	public BrowserWrapperComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	public void setUrl(String url) {
		if (browser == null) {
			browser = new Browser(this, SWT.NONE);		
			browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
		browser.setUrl(url);
	}

}
