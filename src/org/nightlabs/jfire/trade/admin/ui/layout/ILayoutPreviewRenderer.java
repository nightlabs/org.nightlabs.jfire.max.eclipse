package org.nightlabs.jfire.trade.admin.ui.layout;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.jfire.trade.ILayout;

public interface ILayoutPreviewRenderer<L extends ILayout> {
	public Image renderPreview(L layout, int maxWidth, int maxHeight);
}
