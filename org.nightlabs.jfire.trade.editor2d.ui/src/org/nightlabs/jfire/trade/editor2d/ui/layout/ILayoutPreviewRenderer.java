package org.nightlabs.jfire.trade.editor2d.ui.layout;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.jfire.trade.editor2d.ILayout;

public interface ILayoutPreviewRenderer<L extends ILayout> {
	public Image renderPreview(L layout, int maxWidth, int maxHeight);
}
