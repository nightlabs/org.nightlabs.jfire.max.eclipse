package org.nightlabs.jfire.reporting.admin.parameter.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.swt.graphics.Color;
import org.nightlabs.jfire.reporting.admin.parameter.editpart.AbstractNodeReportEditPart;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.ValueProviderInputParameter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueProviderConfigFigure 
extends AbstractInputNodeFigure
{
	public ValueProviderConfigFigure(AbstractNodeReportEditPart editPart, ValueProvider valueProvider) 
	{
		super(editPart);
		this.valueProvider = valueProvider;
		
		LineBorder lineBorder = new LineBorder(1);
		lineBorder.setColor(new Color(null, 114, 114, 114));
		setBorder(lineBorder);
	}
		
	private ValueProvider valueProvider;	
	
	@Override
	protected void paintFigure(Graphics g) 
	{
		super.paintFigure(g);
		drawOutputConnector(g, getBounds());
	}	
		
	protected String getInputString(ValueProviderInputParameter inputParam) 
	{
//		return "ID " + inputParam.getParameterID() + "\n" + "Type " + inputParam.getParameterType(); 
		return inputParam.getParameterID();
	}

	protected String getOutputString() 
	{
		String s = valueProvider.getOutputType();
		int index = s.lastIndexOf("."); //$NON-NLS-1$
		return s.substring(index+1);
	}

	@Override
	public int getInputAmount() 
	{
		return valueProvider.getInputParameters().size();
	}

	@Override
	public String getInputString(int index) 
	{
		String s = valueProvider.getInputParameters().get(index).getParameterType(); 
		return valueProvider.getInputParameters().get(index).getParameterID() + "("+ s.substring(s.lastIndexOf(".")+1) +")";		 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String getName() {
		return valueProvider.getName().getText();
	}
	
//	@Override
//	public String getIputParameterID(int index) {
//		return valueProvider.getInputParameters().get(index).getParameterID();
//	}
//
//	@Override
//	public String getIputParameterType(int index) {
//		return valueProvider.getInputParameters().get(index).getParameterType();
//	}	

}
