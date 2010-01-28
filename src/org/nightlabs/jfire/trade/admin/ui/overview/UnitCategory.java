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
import org.nightlabs.jfire.store.Unit;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.jfire.trade.admin.ui.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.ui.editor.CurrencyCreateWizard;
import org.nightlabs.jfire.trade.admin.ui.editor.UnitEditor;
import org.nightlabs.jfire.trade.admin.ui.editor.UnitEditorInput;

public class UnitCategory 
extends AbstractTradeAdminCategory
{
	private UnitTable unitTable;
	
	public UnitCategory(TradeAdminCategoryFactory tradeAdminCategoryFactory) {
		super(tradeAdminCategoryFactory);
	}

	@Override
	protected Composite _createComposite(Composite parent) {
		unitTable = new UnitTable(parent, SWT.NONE);
		unitTable.addDoubleClickListener(doubleClickListener);
		
		hookContextMenu();
		
		return unitTable;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {

				UnitCategory.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(unitTable);
		unitTable.setMenu(menu);
	}

	private void fillContextMenu(IMenuManager manager) {

		manager.add(new CreateUnitAction());
	}
	
	public class CreateUnitAction  
	extends Action
	{
		public CreateUnitAction()
		{
			super();
			setId(CreateUnitAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					TradeAdminPlugin.getDefault(),
					UnitCategory.class,
					"Create"));
			setToolTipText("Create new unit");
			setText("Create new unit");
		}

		@Override
		public void run() 
		{
			UnitCreateWizard  wizard= new UnitCreateWizard();
			try {
				DynamicPathWizardDialog dialog = 
					new DynamicPathWizardDialog(getComposite().getShell(),wizard);
				int result = dialog.open();
				if(result == Dialog.OK) {

				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}
	
	private IDoubleClickListener doubleClickListener = new IDoubleClickListener(){
		public void doubleClick(org.eclipse.jface.viewers.DoubleClickEvent event) {
			StructuredSelection s = (StructuredSelection)event.getSelection();
			if (s.isEmpty())
				return;

			Unit unit = unitTable.getFirstSelectedElement();
			try {
				UnitID unitID = (UnitID) JDOHelper.getObjectId(unit);
				UnitEditorInput unitEditorInput= new UnitEditorInput(unitID);
				RCPUtil.openEditor(unitEditorInput, UnitEditor.ID_EDITOR);
			}catch(PartInitException e){
				throw new RuntimeException(e);
			}
		}

	};
}