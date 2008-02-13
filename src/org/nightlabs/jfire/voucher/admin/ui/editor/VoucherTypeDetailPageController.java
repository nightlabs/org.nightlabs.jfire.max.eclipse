package org.nightlabs.jfire.voucher.admin.ui.editor;

import java.io.File;

import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.priceconfig.FetchGroupsPriceConfig;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController;
import org.nightlabs.jfire.voucher.VoucherManager;
import org.nightlabs.jfire.voucher.VoucherManagerUtil;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeDetailPageController
extends AbstractProductTypeDetailPageController<VoucherType>
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
			new String[] {FetchGroupsPriceConfig.FETCH_GROUP_EDIT,
					VoucherType.FETCH_GROUP_VOUCHER_LAYOUT
				}
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
				// TODO: inheritance should be controllable by UI. Marco.
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS_VOUCHER_TYPE;
	}
	
	@Override
	protected VoucherType retrieveProductType(ProgressMonitor monitor)
	{
		return VoucherTypeDAO.sharedInstance().getVoucherType(
				getProductTypeID(),
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				monitor);
	}

	@Override
	protected VoucherType storeProductType(VoucherType voucherType, ProgressMonitor monitor)
	{
		// TODO: WORKAROUND: Why is the access to the page here ?!? Alex
		for (IFormPage page : getPages()) {
			if (page instanceof VoucherTypeDetailPage) {
				createVoucherLayout((VoucherTypeDetailPage) page);
			}
		}
		try {
			VoucherManager voucherManager = VoucherManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			return voucherManager.storeVoucherType(voucherType, true, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	protected VoucherType getVoucherType() {
		return getProductType();
	}
}
