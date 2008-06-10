package org.nightlabs.jfire.prop.html.ui;

import org.nightlabs.eclipse.ui.fckeditor.FCKEditor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class PropFCKEditor extends FCKEditor
{
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.FCKEditor#doReallySave()
	 */
	@Override
	public void doReallySave()
	{
		super.doReallySave();
		System.out.println("Save!");
	}
}
