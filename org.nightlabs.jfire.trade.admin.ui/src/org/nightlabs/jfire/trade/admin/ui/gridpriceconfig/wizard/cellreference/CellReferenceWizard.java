package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jseditor.ui.IJSEditor;

public class CellReferenceWizard extends DynamicPathWizard
{
	private AbstractCellReferencePage page = null;
	private IJSEditor targetEditor = null;
	
	private StringBuffer sourceBuffer = new StringBuffer();
	private PriceConfigComposite priceConfigComposite = null;
	
	public CellReferenceWizard(IJSEditor targetEditor, PriceConfigComposite priceConfigComposite){
		super();
		this.targetEditor = targetEditor;
		this.priceConfigComposite = priceConfigComposite;
		sourceBuffer.append(targetEditor.getDocumentText());
	}

	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		page = new CellReferencePage(targetEditor, priceConfigComposite);
		getShell().setSize(600, 600);
		addPage(page);
	}

	static final String NEWLINE = "\n"; //$NON-NLS-1$
	static final String DOUBLE_QUOTE = "\""; //$NON-NLS-1$
	static final String R_BRACKET = ")"; //$NON-NLS-1$
	static final String L_BRACKET = "("; //$NON-NLS-1$
	static final String TAB = "\t"; //$NON-NLS-1$
	
	@Override
	public boolean performFinish() {
		sourceBuffer.append(page.getSourcePreviewComposite().getDocumentText());
		targetEditor.setDocumentText(sourceBuffer.toString());
		return true;
	}
}