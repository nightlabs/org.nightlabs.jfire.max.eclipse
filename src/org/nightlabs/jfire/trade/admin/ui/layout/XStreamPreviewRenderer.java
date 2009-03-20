package org.nightlabs.jfire.trade.admin.ui.layout;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.iofilter.XStreamFilter;
import org.nightlabs.editor2d.util.ImageCreator;
import org.nightlabs.editor2d.viewer.ui.util.AWTSWTUtil;
import org.nightlabs.jfire.trade.ILayout;

public class XStreamPreviewRenderer<L extends ILayout> implements ILayoutPreviewRenderer<L> {
	
	private XStreamFilter filter;
	
	public XStreamPreviewRenderer(XStreamFilter filter) {
		this.filter = filter;
	}

	@Override
	public Image renderPreview(L layout, int maxWidth, int maxHeight) {
		DrawComponent firstPageComponent;
		try {
			firstPageComponent = filter.read(new ByteArrayInputStream(layout.getFileData()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// get the first page
		PageDrawComponent pageDrawComponent = (PageDrawComponent) firstPageComponent.getRoot().getDrawComponents().get(0);

		pageDrawComponent.clearBounds();
		Rectangle pageBounds = pageDrawComponent.getPageBounds();
		Rectangle dcBounds = pageDrawComponent.getBounds();
		Rectangle bounds = pageBounds.contains(dcBounds) ? pageBounds : dcBounds;

		double scale = Math.min(maxWidth/bounds.getWidth(), maxHeight/bounds.getHeight());
		int scaledHeight = (int) (bounds.getHeight()*scale);
		int scaledWidth = (int) (bounds.getWidth()*scale);
		ImageCreator ic = new ImageCreator(pageDrawComponent, scaledWidth, scaledHeight);
		ic.setFitPage(true);
		BufferedImage bufferedImage = ic.createImage();
		return AWTSWTUtil.convertToSWTImage(bufferedImage, Display.getDefault());
	}
}
