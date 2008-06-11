/**
 * 
 */
package org.nightlabs.jfire.simpletrade.admin.ui.editor;

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

//	private SimpleProductType productType;
//	private boolean doInheritStructLocalScope;
	private String structScope;
	private String structLocalScope;
	private Text structLocalScopeText;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public SimpleProductTypeStructLocalScopeSection(IFormPage page, Composite parent, int style)
	{
		super(page, parent, style, Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypeStructLocalScopeSection.title")); //$NON-NLS-1$
		getToolBarManager().add(new InheritanceAction() {
			@Override
			public void run() {
				// TODO: implement
//				setSelection(!isSelection());
			}
		});
		updateToolBarManager();
		structLocalScopeText = new Text(getContainer(), XComposite.getBorderStyle(getContainer()) | SWT.SINGLE | SWT.READ_ONLY);
		structLocalScopeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	public void setSimpleProductType(SimpleProductType productType) {
		structScope = productType.getStructScope();
		structLocalScope = productType.getStructLocalScope();
		StructLocal sl = StructLocalDAO.sharedInstance().getStructLocal(
				SimpleProductType.class, structScope, structLocalScope, new NullProgressMonitor());
		if (sl != null && sl.getName() != null) {
			structLocalScopeText.setText(sl.getName().getText());
		} else {
			structLocalScopeText.setText(structLocalScope);
		}
	}

}
