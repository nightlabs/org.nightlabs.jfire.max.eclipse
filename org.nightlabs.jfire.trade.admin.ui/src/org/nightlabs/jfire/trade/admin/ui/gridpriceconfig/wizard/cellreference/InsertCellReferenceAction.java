package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference;

import org.eclipse.jface.action.Action;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jseditor.ui.IJSEditor;

public class InsertCellReferenceAction extends Action{
		private IJSEditor targetEditor;
		private PriceConfigComposite priceConfigComposite;
		
		public InsertCellReferenceAction(PriceConfigComposite priceConfigComposite, IJSEditor targetEditor){
			this.priceConfigComposite = priceConfigComposite;
			this.targetEditor = targetEditor;
			setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.InsertCellReferenceAction.text")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.InsertCellReferenceAction.toolTipText")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			CellReferenceWizard crWizard = new CellReferenceWizard(targetEditor, priceConfigComposite);
			//Instantiates the wizard container with the wizard and opens it
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(crWizard);
//			WizardDialog dialog = new WizardDialog(priceConfigComposite.getShell(), crWizard);
			dialog.open();
		}


}
