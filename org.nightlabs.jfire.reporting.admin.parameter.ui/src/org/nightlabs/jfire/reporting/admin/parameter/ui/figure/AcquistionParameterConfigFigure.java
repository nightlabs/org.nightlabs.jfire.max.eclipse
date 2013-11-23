package org.nightlabs.jfire.reporting.admin.parameter.ui.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LineBorder;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.AbstractNodeReportEditPart;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;


/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class AcquistionParameterConfigFigure
extends AbstractInputNodeFigure
{
	public AcquistionParameterConfigFigure(AbstractNodeReportEditPart editPart, AcquisitionParameterConfig acquisitionParameterConfig)
	{
		super(editPart);
		this.acquisitionParameterConfig = acquisitionParameterConfig;
		setColorIndex(-1);
		LineBorder lineBorder = new LineBorder(1);
		lineBorder.setColor(ColorConstants.black);
		setBorder(lineBorder);
//		createInputConnectionAnchors(getInputAmount());
	}

	private AcquisitionParameterConfig acquisitionParameterConfig;
	
	@Override
	public int getInputAmount() {
		return 1;
	}

	@Override
	public String getInputString(int index)
	{
		String s = acquisitionParameterConfig.getParameterType();
		return acquisitionParameterConfig.getParameterID() + "(" + s.substring(s.lastIndexOf(".")+1)  + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public String getName() {
		return acquisitionParameterConfig.getParameterID();
	}

	@Override
	protected String getOutputString() {
		return null;
	}
	
}
