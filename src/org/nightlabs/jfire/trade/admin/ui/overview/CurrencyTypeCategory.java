package org.nightlabs.jfire.trade.admin.ui.overview;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.trade.admin.ui.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.ui.editor.CurrencyCreateWizard;
import org.nightlabs.jfire.trade.admin.ui.editor.CurrencyEditor;
import org.nightlabs.jfire.trade.admin.ui.editor.CurrencyEditorInput;
import org.nightlabs.jfire.trade.admin.ui.editor.CurrencySection;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 *
 * @author vince
 *
 */

public class CurrencyTypeCategory
extends AbstractTradeAdminCategory{

	private CurrencyTable currencyTable;
	private CreateCurrencyAction menuCreateCurrencyAction;
	public CurrencyTypeCategory(
			TradeAdminCategoryFactory tradeAdminCategoryFactory) {
		super(tradeAdminCategoryFactory);
		menuCreateCurrencyAction = new CreateCurrencyAction();

	}

	@Override
	protected Composite _createComposite(Composite parent) {

		currencyTable = new CurrencyTable(parent, SWT.NONE);
		hookContextMenu();
		currencyTable.addDoubleClickListener(doubleClickListener);


		return currencyTable;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {

				CurrencyTypeCategory.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(currencyTable);
		currencyTable.setMenu(menu);
	}

	private void fillContextMenu(IMenuManager manager) {

		manager.add(menuCreateCurrencyAction);
	}

	private IDoubleClickListener doubleClickListener = new IDoubleClickListener(){
		public void doubleClick(org.eclipse.jface.viewers.DoubleClickEvent event) {
			StructuredSelection s = (StructuredSelection)event.getSelection();
			if (s.isEmpty())
				return;


			Currency item = currencyTable.getFirstSelectedElement();

			if (item instanceof Currency) {

				try{

					CurrencyID currencyID = (CurrencyID) JDOHelper.getObjectId(item);
					CurrencyEditorInput currencyEditorInput= new CurrencyEditorInput(currencyID);
					RCPUtil.openEditor(currencyEditorInput, CurrencyEditor.ID_EDITOR);
				}catch(PartInitException e){
					throw new RuntimeException(e);
				}
			}
		}

	};

	public class   CreateCurrencyAction  extends Action{
		public  CreateCurrencyAction(){
			super();
			setId(CreateCurrencyAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					TradeAdminPlugin.getDefault(),
					CurrencySection.class,
					"Create"));
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.overview.CurrencyTypeCategory.CreateCurrencyAction.toolTipText"));
			setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.overview.CurrencyTypeCategory.CreateCurrencyAction.text"));
		}

		@Override
		public void run() {


			CurrencyCreateWizard  wizard= new CurrencyCreateWizard();
			try {

				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(getComposite().getShell(),wizard);
				int result = dialog.open();
				if(result == Dialog.OK) {


				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

}
