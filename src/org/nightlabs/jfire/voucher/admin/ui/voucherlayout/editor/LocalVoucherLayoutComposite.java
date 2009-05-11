package org.nightlabs.jfire.voucher.admin.ui.voucherlayout.editor;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.editor2d.layout.AbstractLocalLayoutListComposite;
import org.nightlabs.jfire.trade.editor2d.layout.ILayoutPreviewRenderer;
import org.nightlabs.jfire.trade.editor2d.layout.XStreamPreviewRenderer;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.editor2d.iofilter.VoucherXStreamFilter;
import org.nightlabs.jfire.voucher.scripting.VoucherLayout;

public class LocalVoucherLayoutComposite extends AbstractLocalLayoutListComposite<VoucherLayout> {

	public LocalVoucherLayoutComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected VoucherLayout createLayoutFromFile(File file) throws IOException {
		VoucherLayout layout = new VoucherLayout(null, -1);
		layout.loadFile(file);

		return layout;
	}

	@Override
	protected String getInitialBaseFolder() {
		IPreferenceStore preferenceStore = VoucherAdminPlugin.getDefault().getPreferenceStore();
		return preferenceStore.getString(VoucherLayoutPreferences.LOCAL_VOUCHER_LAYOUT_BASE_DIRECTORY);
	}

	@Override
	protected void storeInitialBaseFolder(File folder) {
		IPreferenceStore preferenceStore = VoucherAdminPlugin.getDefault().getPreferenceStore();
		preferenceStore.setValue(VoucherLayoutPreferences.LOCAL_VOUCHER_LAYOUT_BASE_DIRECTORY, folder.getAbsolutePath());
	}

	@Override
	protected String getLayoutFileExtension() {
		return VoucherXStreamFilter.FILE_EXTENSION;
	}

	private static ILayoutPreviewRenderer<VoucherLayout> renderer = new XStreamPreviewRenderer<VoucherLayout>(new VoucherXStreamFilter());

	@Override
	protected ILayoutPreviewRenderer<VoucherLayout> getRenderer() {
		return renderer;
	}
}
