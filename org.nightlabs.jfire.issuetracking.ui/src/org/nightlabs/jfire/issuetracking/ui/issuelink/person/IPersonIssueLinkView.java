package org.nightlabs.jfire.issuetracking.ui.issuelink.person;

import org.eclipse.ui.IViewPart;
import org.nightlabs.jfire.person.Person;

/**
 * Interface for views which show issue links for person and can return the current selected one.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface IPersonIssueLinkView extends IViewPart, IIssueLinkSelection
{
	public Person getPerson();
}
