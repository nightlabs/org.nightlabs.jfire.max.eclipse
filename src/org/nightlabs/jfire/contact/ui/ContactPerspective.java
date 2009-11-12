package org.nightlabs.jfire.contact.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.base.ui.util.RCPUtil;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ContactPerspective
implements IPerspectiveFactory
{
	public static final String ID_PERSPECTIVE = ContactPerspective.class.getName();

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		layout.addView(ContactView.VIEW_ID, IPageLayout.TOP, 0.5f, IPageLayout.ID_EDITOR_AREA);

//		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.5f, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
//		bottom.addView(ContactDetailView.VIEW_ID);

		layout.addPerspectiveShortcut(ID_PERSPECTIVE);
//		layout.addShowViewShortcut(ContactDetailView.VIEW_ID);

		RCPUtil.addAllPerspectiveShortcuts(layout);
	}
}
