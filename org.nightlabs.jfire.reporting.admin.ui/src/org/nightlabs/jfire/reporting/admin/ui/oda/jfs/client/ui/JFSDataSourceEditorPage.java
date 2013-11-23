/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceEditorPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class JFSDataSourceEditorPage extends DataSourceEditorPage {

	private Composite dummy;
	
	/**
	 * 
	 */
	public JFSDataSourceEditorPage() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceEditorPage#collectCustomProperties(java.util.Properties)
	 */
	@Override
	public Properties collectCustomProperties(Properties properties) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceEditorPage#createAndInitCustomControl(org.eclipse.swt.widgets.Composite, java.util.Properties)
	 */
	@Override
	protected void createAndInitCustomControl(Composite parent, Properties properties) {
		dummy = new Composite(parent, SWT.NONE);
	}

}
