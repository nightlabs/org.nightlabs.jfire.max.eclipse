package org.nightlabs.jfire.auth.ui.ldap;

import java.util.Set;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.auth.ui.ldap.tree.LDAPTree;
import org.nightlabs.jfire.auth.ui.ldap.tree.LDAPTreeEntry;
import org.nightlabs.jfire.auth.ui.ldap.tree.SelectLDAPEntryDialog;
import org.nightlabs.jfire.base.security.integration.ldap.attributes.LDAPAttributeSet;
import org.nightlabs.jfire.base.security.integration.ldap.connection.ILDAPConnectionParamsProvider;
import org.nightlabs.jfire.base.security.integration.ldap.connection.LDAPConnection;

/**
 * This composite is used for editing the name of LDAP entry. The name could be either typed in manually 
 * or (most likely) be selected using popup dialog with {@link LDAPTree}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class LDAPEntrySelectorComposite extends XComposite{

	private static final String SELECT_BUTTON_DEFAULT_CAPTION = "Select...";
	private Text entryNameText;
	
	/**
	 * Parameters for {@link LDAPConnection} which are passed to {@link LDAPTree}.
	 */
	private ILDAPConnectionParamsProvider ldapConnectionParamsProvider;

	/**
	 * Constructs new {@link LDAPEntrySelectorComposite} with default button text and no image.
	 * 
	 * @param parent parent {@link Composite}
	 * @param style {@link SWT} style for this composite
	 */
	public LDAPEntrySelectorComposite(Composite parent, int style) {
		this(parent, style, null, null, null);
	}

	/**
	 * Constructs new {@link LDAPEntrySelectorComposite} with default button text and no image.
	 * 
	 * @param parent parent {@link Composite}
	 * @param style {@link SWT} style for this composite
	 * @param selectionCriteriaAttributes {@link LDAPAttributeSet} which is used as selection criteria in {@link SelectLDAPEntryDialog}, could be <code>null</code>
	 */
	public LDAPEntrySelectorComposite(Composite parent, int style, LDAPAttributeSet selectionCriteriaAttributes) {
		this(parent, style, null, null, selectionCriteriaAttributes);
	}

	/**
	 * Constructs new {@link LDAPEntrySelectorComposite}. This constructor calls to super() in {@link XComposite}
	 * with {@link LayoutMode#LEFT_RIGHT_WRAPPER}, {@link LayoutDataMode#GRID_DATA_HORIZONTAL} and two columns.
	 * 
	 * @param parent parent {@link Composite}
	 * @param style {@link SWT} style for this composite
	 * @param buttonText default button caption ("Select...") will be used if <code>null</code>
	 * @param buttonImage no image will be shown by default if <code>null</code>
	 * @param selectionCriteriaAttributes {@link LDAPAttributeSet} which is used as selection criteria in {@link SelectLDAPEntryDialog}, could be <code>null</code>
	 */
	public LDAPEntrySelectorComposite(Composite parent, int style, String buttonText, Image buttonImage, LDAPAttributeSet selectionCriteriaAttributes) {
		super(parent, style, LayoutMode.LEFT_RIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL, 2);
		createContent(buttonText, buttonImage, selectionCriteriaAttributes);
	}

	/**
	 * Set {@link ILDAPConnectionParamsProvider} which is used when opening {@link SelectLDAPEntryDialog} with {@link LDAPTree}.
	 * {@link IllegalStateException} will be thrown on open if not set or is <code>null</code>.
	 * 
	 * @param ldapConnectionParamsProvider can't be <code>null</code>, {@link IllegalArgumentException} will be thrown otherwise
	 */
	public void setLdapConnectionParamsProvider(ILDAPConnectionParamsProvider ldapConnectionParamsProvider) {
		if (ldapConnectionParamsProvider == null){
			throw new IllegalArgumentException("ILDAPConnectionParamsProvider can't be null!");
		}
		this.ldapConnectionParamsProvider = ldapConnectionParamsProvider;
	}
	
	/**
	 * Get selected LDAP entry name.
	 * 
	 * @return selected LDAP entry name
	 */
	public String getEntryName() {
		return entryNameText.getText();
	}
	
	private void createContent(String selectorButtonText, Image selectorButtonImage, final LDAPAttributeSet selectionCriteriaAttributes){
		
		entryNameText = new Text(this, SWT.BORDER);
		entryNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button openLdapTreeButton = new Button(this, SWT.PUSH);
		openLdapTreeButton.setText(
				selectorButtonText!=null && !selectorButtonText.isEmpty()?selectorButtonText:SELECT_BUTTON_DEFAULT_CAPTION);
		if (selectorButtonImage != null){
			openLdapTreeButton.setImage(selectorButtonImage);
		}
		openLdapTreeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionevent) {
				
				if (ldapConnectionParamsProvider == null){
					throw new IllegalStateException("ILDAPConnectionParamsProvider is not set! Connection to LDAPServer is not possible.");
				}
				
				SelectLDAPEntryDialog dlg = new SelectLDAPEntryDialog(RCPUtil.getActiveShell(), null, ldapConnectionParamsProvider);
				dlg.setSelectionCriteriaAttributes(selectionCriteriaAttributes);
				if (Window.OK == dlg.open()){
					Set<LDAPTreeEntry> selectedElements = dlg.getSelectedElements();
					if (selectedElements != null && selectedElements.iterator().hasNext()){
						Object entry = selectedElements.iterator().next();
						if (entry instanceof LDAPTreeEntry){
							entryNameText.setText(((LDAPTreeEntry) entry).getName());
						}
					}
				}
			}
		});
		
	}
	
}
