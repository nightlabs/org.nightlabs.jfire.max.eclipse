/**
 *
 */
package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SimpleProductTypeStructLocalScopeSection extends ToolBarSectionPart {

	private static final Logger LOGGER = Logger.getLogger(SimpleProductTypeStructLocalScopeSection.class);

	private SimpleProductType simpleProductType;
//	private boolean doInheritStructLocalScope;
//	private String structScope;
//	private String structLocalScope;
	private Text structLocalScopeText;
	private InheritanceAction inheritanceAction;

	/**
	 * @param page
	 * @param parent
	 * @param style
	 */
	public SimpleProductTypeStructLocalScopeSection(IFormPage page, Composite parent, int style)
	{
		super(page, parent, style, Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypeStructLocalScopeSection.title")); //$NON-NLS-1$
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

	public void setSimpleProductType(SimpleProductType simpleProductType) {
		this.simpleProductType = simpleProductType;
		String structLocalScope = this.simpleProductType.getPropertySet().getStructLocalScope();
		StructLocal sl = StructLocalDAO.sharedInstance().getStructLocal(
			this.simpleProductType.getPropertySet().getStructLocalObjectID(),
			new NullProgressMonitor()
		);
		if (sl != null && sl.getName() != null) {
			structLocalScopeText.setText(sl.getName().getText());
		} else {
			structLocalScopeText.setText(structLocalScope);
		}
	}
}
