package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.trade.editor2d.ui.layout.AbstractRemoteLayoutListComposite;
import org.nightlabs.jfire.voucher.dao.VoucherLayoutDAO;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;
import org.nightlabs.jfire.voucher.scripting.id.VoucherLayoutID;
import org.nightlabs.progress.ProgressMonitor;

public class RemoteVoucherLayoutComposite extends AbstractRemoteLayoutListComposite<VoucherLayoutID, VoucherLayout> {

	public RemoteVoucherLayoutComposite(Composite parent, int style, boolean load) {
		super(parent, style, load);
	}

	@Override
	protected ActiveJDOObjectController<VoucherLayoutID, VoucherLayout> createActiveJDOObjectController() {
		return new ActiveJDOObjectController<VoucherLayoutID, VoucherLayout>() {
			@Override
			protected void sortJDOObjects(List<VoucherLayout> objects) {
			}

			@Override
			protected Collection<VoucherLayout> retrieveJDOObjects(ProgressMonitor monitor) {
				return VoucherLayoutDAO.sharedInstance().getAllVoucherLayouts(new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			}

			@Override
			protected Collection<VoucherLayout> retrieveJDOObjects(Set<VoucherLayoutID> objectIDs, ProgressMonitor monitor) {
				return VoucherLayoutDAO.sharedInstance().getVoucherLayouts(objectIDs, new String[] {FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			}

			@Override
			protected Class<? extends VoucherLayout> getJDOObjectClass() {
				return VoucherLayout.class;
			}
		};
	}
}
