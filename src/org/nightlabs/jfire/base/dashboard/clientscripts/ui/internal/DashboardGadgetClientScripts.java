package org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.action.RunClientScriptAction;
import org.nightlabs.jfire.base.dashboard.clientscripts.ui.resource.Messages;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadget;
import org.nightlabs.jfire.dashboard.DashboardGadgetClientScriptsConfig;
import org.nightlabs.jfire.dashboard.DashboardGadgetClientScriptsConfig.ClientScript;
import org.nightlabs.jfire.dashboard.DashboardGadgetLayoutEntry;

/**
 * @author sschefczyk
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class DashboardGadgetClientScripts extends AbstractDashboardGadget {

	private Composite scriptsComposite;
	
	@Override
	public Composite createControl(Composite parent) 
	{
		XComposite xComposite = createDefaultWrapper(parent);
		
		ScrolledComposite scrolledScriptsComposite = createWrapper(xComposite);
		scrolledScriptsComposite.setExpandHorizontal(true);
		
		scriptsComposite = new Composite(scrolledScriptsComposite, SWT.NONE);
		scrolledScriptsComposite.setContent(scriptsComposite);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginBottom = 10;
		scriptsComposite.setLayout(layout);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		scriptsComposite.setLayoutData(gridData);
		
		return xComposite;
	}
	
	
	protected ScrolledComposite createWrapper(Composite parent) {
		ScrolledComposite wrapper = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		wrapper.setLayoutData(layoutData);
		return wrapper;
	}
	

	@Override
	public void refresh() {
		getGadgetContainer().setTitle(getGadgetContainer().getLayoutEntry().getName());
		
		for (Control c : scriptsComposite.getChildren()) {
			c.dispose();
		}
		
		DashboardGadgetLayoutEntry<?> layoutEntry = getGadgetContainer().getLayoutEntry();
		if (layoutEntry == null)
			throw new IllegalStateException("layoutEntry==null; this is not allowed!"); //$NON-NLS-1$
		final DashboardGadgetClientScriptsConfig config = (DashboardGadgetClientScriptsConfig) layoutEntry.getConfig();
		
		for (final ClientScript clientScript : config.getClientScripts()) {
			Hyperlink scriptLink = new Hyperlink(scriptsComposite, SWT.NONE);
			scriptLink.setText(clientScript.getName());
			Menu menu = createContextMenu(clientScript, config.isConfirmProcessing());			
			scriptLink.setMenu(menu);
			scriptLink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					new RunClientScriptAction(clientScript, config.isConfirmProcessing()).run();
				}
			});
		}
		scriptsComposite.setSize(scriptsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private Menu createContextMenu(ClientScript clientScript, final boolean confirmProcessing) {
		Menu menu = new Menu(Display.getCurrent().getActiveShell(), SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.NONE);
		item.setData("clientScript", clientScript); //$NON-NLS-1$
		item.setText(Messages.getString("org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal.DashboardGadgetClientScripts.createContextMenu.item1.text")); //$NON-NLS-1$
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource() instanceof MenuItem) {
					MenuItem item = (MenuItem) e.getSource();
					if (item.getData("clientScript") instanceof ClientScript) { //$NON-NLS-1$
						ClientScript clientScript = (ClientScript) (item.getData("clientScript")); //$NON-NLS-1$
						new RunClientScriptAction(clientScript, confirmProcessing).run();
					}
				}
			}
		});
		
		return menu;
	}
}
