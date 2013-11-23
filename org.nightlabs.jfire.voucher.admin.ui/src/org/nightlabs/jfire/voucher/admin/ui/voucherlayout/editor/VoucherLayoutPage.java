package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeDetailPageController;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;
import org.nightlabs.jfire.voucher.store.VoucherType;

public class VoucherLayoutPage extends EntityEditorPageWithProgress {
	
	public static final String ID_PAGE = VoucherLayoutPage.class.getName();
	private AssignedVoucherLayoutSection assignedVoucherLayoutSection;
	private LocalVoucherLayoutSection localVoucherLayoutSection;
	private RemoteVoucherLayoutSection remoteVoucherLayoutSection;
	
	public VoucherLayoutPage(FormEditor editor) {
		super(editor, ID_PAGE, Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.VoucherLayoutPage.title")); //$NON-NLS-1$
	}

	public static class Factory implements IEntityEditorPageFactory {
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new VoucherLayoutPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return editor.getController().getSinglePageController(VoucherTypeDetailPageController.class);
		}
	}

	@Override
	protected void addSections(Composite parent) {
		assignedVoucherLayoutSection = new AssignedVoucherLayoutSection(this, parent);
		assignedVoucherLayoutSection.getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getManagedForm().addPart(assignedVoucherLayoutSection);
		
		remoteVoucherLayoutSection = new RemoteVoucherLayoutSection(this, parent);
		remoteVoucherLayoutSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getManagedForm().addPart(remoteVoucherLayoutSection);
		
		localVoucherLayoutSection = new LocalVoucherLayoutSection(this, parent);
		localVoucherLayoutSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getManagedForm().addPart(localVoucherLayoutSection);
	}

	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor.VoucherLayoutPage.pageFormTitle"); //$NON-NLS-1$
	}
	
	private VoucherLayout assignedVoucherLayout;
	
	protected void assignVoucherLayout(VoucherLayout voucherLayout) {
		assignedVoucherLayout = voucherLayout;
		
		assignedVoucherLayoutSection.setVoucherLayout(voucherLayout);
		remoteVoucherLayoutSection.selectVoucherLayout(voucherLayout);
		
		assignedVoucherLayoutSection.markDirty();
	}
	
	protected VoucherLayout getAssignedVoucherLayout() {
		return assignedVoucherLayout;
	}
	
	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		super.handleControllerObjectModified(modifyEvent);
		VoucherType voucher = (VoucherType) modifyEvent.getNewObject();
		final VoucherLayout voucherLayout = voucher.getVoucherLayout();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				assignedVoucherLayoutSection.setVoucherLayout(voucherLayout);
				remoteVoucherLayoutSection.selectVoucherLayout(voucherLayout);
				assignedVoucherLayout = voucherLayout;
			}
		});
	}
	
	public RemoteVoucherLayoutSection getRemoteVoucherLayoutSection() {
		return remoteVoucherLayoutSection;
	}
	
	public LocalVoucherLayoutSection getLocalVoucherLayoutSection() {
		return localVoucherLayoutSection;
	}
}
