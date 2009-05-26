package org.nightlabs.jfire.issuetracking.ui.issuelink.person;

import java.util.Collection;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.person.search.PersonEditWizard;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkHandler;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.prop.DataField;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.datafield.II18nTextDataField;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueLinkHandlerPerson 
extends AbstractIssueLinkHandler<PropertySetID, Person>
{
	@Override
	public String getLinkedObjectName(IssueLink issueLink, Person linkedObject) {
		DataField dataField = linkedObject.getPersistentDataFieldByIndex(PersonStruct.PERSONALDATA_NAME, 0);
		return String.format(
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.person.IssueLinkHandlerPerson.linkedObjectName"), //$NON-NLS-1$
				linkedObject.getOrganisationID() 
				+ '/' + linkedObject.getPropertySetID() 
				+ '/' + ((II18nTextDataField) dataField).getI18nText().getText()); // TODO there must be the subject and maybe some other data be shown
	}

	@Override
	public Image getLinkedObjectImage(IssueLink issueLink, Person linkedObject) {
		return SharedImages.getSharedImageDescriptor(
				IssueTrackingPlugin.getDefault(), 
				IssueLinkHandlerPerson.class, 
		"LinkedObject").createImage(); //$NON-NLS-1$
	}

	@Override
	public void openLinkedObject(IssueLink issueLink, PropertySetID linkedObjectID) {
		PersonEditWizard wizard = new PersonEditWizard((Person)issueLink.getLinkedObject());
		new DynamicPathWizardDialog(wizard).open();
	}

	@Override
	protected Collection<Person> _getLinkedObjects(
			Set<IssueLink> issueLinks, Set<PropertySetID> linkedObjectIDs,
			ProgressMonitor monitor)
			{
		return CollectionUtil.castCollection(PropertySetDAO.sharedInstance().getPropertySets(
				linkedObjectIDs,
				new String[] { FetchPlan.DEFAULT, Person.FETCH_GROUP_DATA_FIELDS }, // TODO do we need more?
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor));
			}
}