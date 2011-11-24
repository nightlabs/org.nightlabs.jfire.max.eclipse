/**
 *
 */
package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.dynamictrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class DynamicProductTypeStructLocalScopeSection extends ToolBarSectionPart {


	private Text structLocalScopeText;
	private InheritanceAction inheritanceAction;

	/**
	 * @param page
	 * @param parent
	 * @param style
	 */
	public DynamicProductTypeStructLocalScopeSection(IFormPage page, Composite parent, int style)
	{
		super(page, parent, style, Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypeStructLocalScopeSection.title")); //$NON-NLS-1$
		inheritanceAction = new InheritanceAction(){
			@Override
			public void run() {
				inheritPressed();
			}
		};
		inheritanceAction.setEnabled(false);
		getToolBarManager().add(inheritanceAction);
		updateToolBarManager();
		structLocalScopeText = new Text(getContainer(), XComposite.getBorderStyle(getContainer()) | SWT.SINGLE | SWT.READ_ONLY);
		structLocalScopeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void inheritPressed() {
		// TODO implement
	}

	public void setPropertySet(PropertySet propertySet) {
		String structLocalScope = propertySet.getStructLocalScope();
		StructLocal sl = StructLocalDAO.sharedInstance().getStructLocal(
				propertySet.getStructLocalObjectID(),
			new NullProgressMonitor()
		);
		if (sl != null && sl.getName() != null) {
			structLocalScopeText.setText(sl.getName().getText());
		} else {
			structLocalScopeText.setText(structLocalScope);
		}
	}
}
