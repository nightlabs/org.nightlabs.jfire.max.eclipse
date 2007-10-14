package org.nightlabs.jfire.voucher.admin.ui.editor;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController;
import org.nightlabs.jfire.voucher.VoucherManager;
import org.nightlabs.jfire.voucher.VoucherManagerUtil;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeDetailPageController  
extends AbstractProductTypeDetailPageController
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param editor
	 */
	public VoucherTypeDetailPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public VoucherTypeDetailPageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	public static final String[] FETCH_GROUPS_VOUCHER_TYPE = CollectionUtil.mergeArrays(
			FETCH_GROUPS_DEFAULT,
			new String[] {VoucherType.FETCH_GROUP_VOUCHER_LAYOUT}
		);
	
	protected void createVoucherLayout(VoucherTypeDetailPage page) 
	{
		File selectedFile = page.getVoucherLayoutSection().getVoucherLayoutComposite().getSelectedFile();
		VoucherLayout voucherLayout = getVoucherType().getVoucherLayout();
		if (voucherLayout == null) {
			voucherLayout = new VoucherLayout(IDGenerator.getOrganisationID(), 
					IDGenerator.nextID(VoucherLayout.class));			
		}
		try {
			if (selectedFile != null) {
				voucherLayout.loadFile(selectedFile);				
				voucherLayout.saveFile(selectedFile);				
				getVoucherType().setVoucherLayout(voucherLayout);
				getVoucherType().getFieldMetaData("voucherLayout").setValueInherited(false);  //$NON-NLS-1$
				// TODO inheritance should be controllable by UI. Marco.				
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

	@Override
	protected ProductType retrieveProductType(IProgressMonitor monitor) 
	{
		return VoucherTypeDAO.sharedInstance().getVoucherType(
				getProductTypeID(), 
				FETCH_GROUPS_VOUCHER_TYPE, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
				new ProgressMonitorWrapper(monitor));
	}

	@Override
	protected void storeProductType(IFormPage page, IProgressMonitor monitor) 
	{
		VoucherTypeDetailPage detailPage = (VoucherTypeDetailPage) page;
		createVoucherLayout(detailPage);
		try {
			VoucherManager voucherManager = VoucherManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			voucherManager.storeVoucherType(getVoucherType(), false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}		
	}
	
	protected VoucherType getVoucherType() {
		return (VoucherType) getProductType();
	}
}
