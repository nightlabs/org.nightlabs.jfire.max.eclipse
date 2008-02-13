/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/
package org.nightlabs.jfire.scripting.editor2d.ui.dialog;

import java.awt.Font;
import java.util.Map;

import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.editor2d.ui.AbstractScriptRegistryItemTreeComposite;
import org.nightlabs.jfire.scripting.editor2d.ui.ScriptRegistryTreeLabelProvider;
import org.nightlabs.jfire.scripting.editor2d.ui.request.TextScriptCreateRequest;
import org.nightlabs.jfire.scripting.editor2d.ui.resource.Messages;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemNode;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryItemTree;
import org.nightlabs.util.FontUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class CreateTextScriptDialog
extends CenteredDialog
{
	public static final Logger logger = Logger.getLogger(CreateTextScriptDialog.class);
	
	public CreateTextScriptDialog(Shell parentShell,
			TextScriptCreateRequest request)
	{
		super(parentShell);
		init(request);
	}

	public CreateTextScriptDialog(IShellProvider parentShell,
			TextScriptCreateRequest request)
	{
		super(parentShell);
		init(request);
	}

	protected void init(TextScriptCreateRequest request)
	{
		if (request == null)
			throw new IllegalArgumentException("request must not be null!"); //$NON-NLS-1$
		
		this.request = request;
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MIN | SWT.MAX);
	}
		
	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.title")); //$NON-NLS-1$
		newShell.setSize(700, 500);
	}

	private TextScriptCreateRequest request;
	private AbstractScriptRegistryItemTreeComposite scriptTreeComp = null;
	protected ScriptRegistryItemTree getScriptTree() {
		return scriptTreeComp.getScriptTree();
	}
		
	protected abstract AbstractScriptRegistryItemTreeComposite createScriptTreeComposite(Composite parent);
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp.setLayout(new GridLayout(2, false));
		
		SashForm sash = new SashForm(comp, SWT.HORIZONTAL);
		sash.setLayout(new GridLayout());
		sash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// ScriptTree
		Group scriptGroup = new Group(sash, SWT.NONE);
		scriptGroup.setLayout(new GridLayout());
		scriptGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		scriptGroup.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.group.script")); //$NON-NLS-1$
		
		scriptTreeComp = createScriptTreeComposite(scriptGroup);
		initScriptTree();
		getScriptTree().getTreeViewer().expandToLevel(2);
		getScriptTree().getTreeViewer().addDoubleClickListener(doubleClickListener);
		
		// Text Modifcations
		Group textGroup = new Group(sash, SWT.NONE);
		textGroup.setLayout(new GridLayout());
		textGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		textGroup.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.group.text")); //$NON-NLS-1$
		Composite textComp = new XComposite(textGroup, SWT.NONE);
		
		createEntry(textComp, Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.label.font"), ENTRY_FONT); //$NON-NLS-1$
		createEntry(textComp, Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.label.size"), ENTRY_SIZE); //$NON-NLS-1$
		createEntry(textComp, Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.label.bold"), ENTRY_BOLD);		 //$NON-NLS-1$
		createEntry(textComp, Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.label.italic"), ENTRY_ITALIC);				 //$NON-NLS-1$
		createEntry(textComp, Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.label.rotation"), ENTRY_ROTATION);		 //$NON-NLS-1$
		createEntry(textComp, Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.label.x"), ENTRY_X);		 //$NON-NLS-1$
		createEntry(textComp, Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.label.y"), ENTRY_Y);		 //$NON-NLS-1$
		
		return comp;
	}
	
	private IDoubleClickListener doubleClickListener = new IDoubleClickListener(){
		public void doubleClick(DoubleClickEvent event) {
			if (!event.getSelection().isEmpty() && event.getSelection() instanceof StructuredSelection) {
				StructuredSelection structuredSelection = (StructuredSelection) event.getSelection();
				if (structuredSelection.getFirstElement() instanceof ScriptRegistryItemNode) {
					okPressed();
				}
			}
		}
	};
	
	private Combo fontCombo = null;
	private Combo rotationCombo = null;
	private Combo sizeCombo = null;
	private Spinner xSpinner = null;
	private Spinner ySpinner = null;
	private Button italicButton;
  private Button boldButton;

	private static final int ENTRY_FONT = 1;
	private static final int ENTRY_ROTATION = 2;
	private static final int ENTRY_X = 3;
	private static final int ENTRY_Y = 4;
	private static final int ENTRY_SIZE = 5;
	private static final int ENTRY_ITALIC = 6;
	private static final int ENTRY_BOLD = 7;
	
		
	protected void createEntry(Composite parent, String label, int entryMode)
	{
		parent.setLayout(new GridLayout(2, false));
		Composite entry = parent;
		Label l = new Label(entry, SWT.NONE);
		l.setText(label);
		l.setSize(150, l.getSize().y);
		
		Control widget = null;
		switch(entryMode)
		{
			case(ENTRY_FONT):
				createFontCombo(parent);
				widget = fontCombo;
				break;
			case(ENTRY_ROTATION):
				createRotationCombo(parent);
				widget = rotationCombo;
				break;
			case(ENTRY_X):
				createXSpinner(parent);
				widget = xSpinner;
				break;
			case(ENTRY_Y):
				createYSpinner(parent);
				widget = xSpinner;
				break;
			case(ENTRY_SIZE):
				createSizeCombo(parent);
				widget = sizeCombo;
				break;
			case(ENTRY_BOLD):
				createBoldEntry(parent);
				widget = boldButton;
				break;
			case(ENTRY_ITALIC):
				createItalicEntry(parent);
				widget = boldButton;
				break;
		}
		widget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
//	private String defaultFontName = "Arial";
//	private String defaultFontName = FontUtil.getDefaultFont().getName();
	private String defaultFontName = Display.getDefault().getSystemFont().getFontData()[0].getName();
	public void setDefaultFontName(String fontName) {
		this.defaultFontName = fontName;
	}

	protected void initScriptTree()
	{
		TreeColumn tc = new TreeColumn(getScriptTree().getTree(), SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.treeColumn.name"));		 //$NON-NLS-1$
		tc = new TreeColumn(getScriptTree().getTree(), SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.treeColumn.value"));		 //$NON-NLS-1$
		
		getScriptTree().getTreeViewer().setLabelProvider(
				new ScriptRegistryTreeLabelProvider(getScriptRegistryItemID2Result()));
	
		getScriptTree().getTree().setLayout(new WeightedTableLayout(new int[] {3, 2}));
		getScriptTree().getTreeViewer().refresh(true);
		getScriptTree().getTreeViewer().getTree().redraw();

		getScriptTree().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				getButton(OK).setEnabled(isComplete());
			}
		});
	}

	// TODO: get fonts which are supported by printer
	protected void createFontCombo(Composite parent)
	{
		String[] systemFonts = FontUtil.getSystemFonts();
		fontCombo = new Combo(parent, SWT.READ_ONLY);
		fontCombo.setItems(systemFonts);
		int index = fontCombo.indexOf(defaultFontName);
		if (index != -1)
			fontCombo.select(index);
		else
			fontCombo.select(0);
		
		fontCombo.addSelectionListener(fontListener);
	}

	protected void createRotationCombo(Composite parent)
	{
		String[] rotations = new String[] {"0", "90", "180", "270"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		rotationCombo = new Combo(parent, SWT.READ_ONLY);
		rotationCombo.setItems(rotations);
		rotationCombo.select(0);
		rotationCombo.addSelectionListener(rotationListener);
	}
	
	protected void createXSpinner(Composite parent)
	{
		xSpinner = new Spinner(parent, SWT.BORDER);
		xSpinner.setMaximum(Integer.MAX_VALUE);
		xSpinner.setMinimum(Integer.MIN_VALUE);
		x = request.getLocation().x;
		xSpinner.setSelection(x);
		xSpinner.addSelectionListener(xListener);
	}
	
	protected void createYSpinner(Composite parent)
	{
		ySpinner = new Spinner(parent, SWT.BORDER);
		ySpinner.setMaximum(Integer.MAX_VALUE);
		ySpinner.setMinimum(Integer.MIN_VALUE);
		y = request.getLocation().y;
		ySpinner.setSelection(y);
		ySpinner.addSelectionListener(yListener);
		ySpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	protected void createBoldEntry(Composite parent)
	{
		boldButton = new Button(parent, SWT.CHECK);
//		boldButton.addSelectionListener(boldListener);
	}
	protected boolean isBold() {
		return boldButton.getSelection();
	}
	
	protected void createItalicEntry(Composite parent)
	{
		italicButton = new Button(parent, SWT.CHECK);
//		italicButton.addSelectionListener(italicListener);
	}
	protected boolean isItalic() {
		return italicButton.getSelection();
	}
		
	private int defaultFontSize = 24;
	public int getDefaultFontSize() {
		return defaultFontSize;
	}
	
	//TODO: get fontSizes which are supported by printer
  protected void createSizeCombo(Composite parent)
  {
    sizeCombo = new Combo(parent, SWT.READ_ONLY);
    String[] sizes = FontUtil.getFontSizes();
    sizeCombo.setItems(sizes);
    
    for (int i=0; i<sizes.length; i++) {
      String f = sizes[i];
      if (f.equals(Integer.toString(defaultFontSize))) {
        sizeCombo.select(i);
      }
    }
    sizeCombo.addSelectionListener(sizeListener);
  }
	
  private String fontName = defaultFontName;
  private SelectionListener fontListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		public void widgetSelected(SelectionEvent e) {
			fontName = fontCombo.getItem(fontCombo.getSelectionIndex());
		}
	};
	
	private int rotation = 0;
	private SelectionListener rotationListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		public void widgetSelected(SelectionEvent e) {
			rotation = Integer.parseInt(rotationCombo.getItem(rotationCombo.getSelectionIndex()));
		}
	};
	
	private int x = 0;
	private SelectionListener xListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		public void widgetSelected(SelectionEvent e) {
			x = xSpinner.getSelection();
		}
	};

	private int y = 0;
	private SelectionListener yListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		public void widgetSelected(SelectionEvent e) {
			y = ySpinner.getSelection();
		}
	};
		
	private int fontSize = defaultFontSize;
	private SelectionListener sizeListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		public void widgetSelected(SelectionEvent e) {
			fontSize = Integer.parseInt(sizeCombo.getItem(sizeCombo.getSelectionIndex()));
		}
	};
			
	private int fontStyle = Font.PLAIN;
	private int getFontStyle()
	{
		if (isBold())
			fontStyle = fontStyle | Font.BOLD;
		if (isItalic())
			fontStyle = fontStyle | Font.ITALIC;
		
		return fontStyle;
	}

	protected boolean isComplete()
	{
		if (getScriptRegistryItemID() == null)
			return false;

		return getScriptTree().getSelectedRegistryItem() instanceof Script;
	}

	@Override
	protected void okPressed()
	{
		if (!isComplete())
			return;

		request.setFontName(fontName);
		request.setLocation(new Point(x, y));
		request.setRotation(rotation);
		request.setText(getText());
		request.setScriptRegistryItemID(getScriptRegistryItemID());
		request.setFontSize(fontSize);
		request.setFontStyle(getFontStyle());
		super.okPressed();
	}
	
	private String text = Messages.getString("org.nightlabs.jfire.scripting.editor2d.ui.dialog.CreateTextScriptDialog.text"); //$NON-NLS-1$
		
	protected abstract Map<ScriptRegistryItemID, Object> getScriptRegistryItemID2Result();
	
	public String getText()
	{
		Object o = getScriptRegistryItemID2Result().get(getScriptRegistryItemID());
		if (o == null)
			text = ""; //$NON-NLS-1$
		else
			text = String.valueOf(o);
		logger.debug("text = "+text); //$NON-NLS-1$
		return text;
	}
	
	protected ScriptRegistryItemID getScriptRegistryItemID()
	{
		if (!getScriptTree().isDisposed()) {
			return (ScriptRegistryItemID)JDOHelper.getObjectId(
					getScriptTree().getSelectedRegistryItem());
		}
		return null;
	}
}

