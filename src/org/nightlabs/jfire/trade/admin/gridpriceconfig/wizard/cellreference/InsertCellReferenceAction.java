package org.nightlabs.jfire.trade.admin.gridpriceconfig.wizard.cellreference;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.source.SourceViewer;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.trade.admin.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jfire.trade.admin.resource.Messages;

public class InsertCellReferenceAction extends Action{
		private SourceViewer sourceViewer;
		private PriceConfigComposite priceConfigComposite;
		
		public InsertCellReferenceAction(PriceConfigComposite priceConfigComposite, SourceViewer sourceViewer){
			this.priceConfigComposite = priceConfigComposite;
			this.sourceViewer = sourceViewer;
			setText(Messages.getString("org.nightlabs.jfire.trade.admin.gridpriceconfig.wizard.cellreference.InsertCellReferenceAction.text")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.gridpriceconfig.wizard.cellreference.InsertCellReferenceAction.toolTipText")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			CellReferenceWizard crWizard = new CellReferenceWizard(sourceViewer, priceConfigComposite);
			//Instantiates the wizard container with the wizard and opens it
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(crWizard);
//			WizardDialog dialog = new WizardDialog(priceConfigComposite.getShell(), crWizard);
			dialog.open();
		}


}
