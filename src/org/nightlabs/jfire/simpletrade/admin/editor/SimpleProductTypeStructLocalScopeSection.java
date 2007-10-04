/**
 * 
 */
package org.nightlabs.jfire.simpletrade.admin.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.simpletrade.admin.resource.Messages;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SimpleProductTypeStructLocalScopeSection extends ToolBarSectionPart {

	private SimpleProductType productType;
	private boolean doInheritStructLocalScope;
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
		super(page, parent, style, Messages.getString("org.nightlabs.jfire.simpletrade.admin.editor.SimpleProductTypeStructLocalScopeSection.title")); //$NON-NLS-1$
		getToolBarManager().add(new InheritanceAction() {
			@Override
			public void run() {
				// TODO: implement
//				setSelection(!isSelection());
			}
		});
		updateToolBarManager();
		structLocalScopeText = new Text(getContainer(), SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		structLocalScopeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	public void setSimpleProductType(SimpleProductType productType) {
		structLocalScope = productType.getStructLocalScope();
		structLocalScopeText.setText(structLocalScope);
	}

}
