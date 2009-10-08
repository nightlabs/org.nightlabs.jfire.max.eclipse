package org.nightlabs.jfire.reporting.admin.parameter.ui.figure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.swt.graphics.Color;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.AbstractNodeReportEditPart;
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

	@Override
	protected String getOutputString()
	{
		// We remove all packages in order to shorten the displayed types - using simple class-names only. For example:
		//
		// * java.lang.String => String
		// * org.nightlabs.jfire.security.id.UserID => UserID
		// * java.util.Collection<org.nightlabs.jfire.security.id.UserID> => Collection<UserID>
		// * java.util.Map<org.nightlabs.jfire.security.id.UserID, java.util.List<org.nightlabs.jfire.something.Whatever>> => Map<UserID, List<Whatever>>
		//
		String s = valueProvider.getOutputType();
		Pattern pattern = Pattern.compile("([^.<>,\\s]*)([\\.<>,\\s]?)"); //$NON-NLS-1$
		Matcher matcher = pattern.matcher(s);
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			String word = matcher.group(1);
			String symbol = matcher.group(2);
			if (!".".equals(symbol)) {
				sb.append(word);
				sb.append(symbol);
			}
		}
		return sb.toString();
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

	@Override
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
