package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference;

import org.eclipse.jface.text.source.SourceViewer;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite;

public class CellReferenceWizard extends DynamicPathWizard
{
	private AbstractCellReferencePage page = null;
	private SourceViewer sourceViewer = null;
	
	private StringBuffer sourceBuffer = new StringBuffer();
	private PriceConfigComposite priceConfigComposite = null;
	
	public CellReferenceWizard(SourceViewer sourceViewer, PriceConfigComposite priceConfigComposite){
		super();
		this.sourceViewer = sourceViewer;
		this.priceConfigComposite = priceConfigComposite;
		sourceBuffer.append(sourceViewer.getDocument().get());
	}

	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		page = new CellReferencePage(sourceViewer, priceConfigComposite);
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
		sourceBuffer.append(page.getSourcePreviewComposite().getDocument().get());
		sourceViewer.getDocument().set(sourceBuffer.toString());
		return true;
	}
}