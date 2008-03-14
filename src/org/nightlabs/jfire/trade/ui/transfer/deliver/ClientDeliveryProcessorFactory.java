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
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.util.Collection;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface ClientDeliveryProcessorFactory
{
	/**
	 * This method is called before {@link #init()}.
	 *
	 * @param id The extension id as specified in the plugin.xml.
	 */
	public void setID(String id);

	/**
	 * @return Returns the value that has been set by {@link #setID(String)}
	 */
	public String getID();

	/**
	 * This method is called before {@link #init()}.
	 *
	 * @param name The name as specified in the plugin.xml.
	 */
	public void setName(String name);

	/**
	 * @return Returns the value that has been set by {@link #setName(String)}
	 */
	public String getName();

	public static class ModeOfDeliveryRef {
		public ModeOfDeliveryRef(String organisationID, String modeOfDeliveryID)
		{
			if (organisationID == null)
				throw new NullPointerException("organisationID"); //$NON-NLS-1$

			if (modeOfDeliveryID == null)
				throw new NullPointerException("modeOfDeliveryID"); //$NON-NLS-1$

			this.organisationID = organisationID;
			this.modeOfDeliveryID = modeOfDeliveryID;
		}
		private String organisationID;
		private String modeOfDeliveryID;

		public String getOrganisationID() {
			return organisationID;
		}
		public String getModeOfDeliveryID() {
			return modeOfDeliveryID;
		}
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof ModeOfDeliveryRef))
				return false;
			
			ModeOfDeliveryRef other = (ModeOfDeliveryRef)obj;

			return
				this.organisationID.equals(other.organisationID) &&
				this.modeOfDeliveryID.equals(other.modeOfDeliveryID);
		}
		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return organisationID.hashCode() ^ modeOfDeliveryID.hashCode();
		}
	}

	public static class ModeOfDeliveryFlavourRef {
		public ModeOfDeliveryFlavourRef(String organisationID, String modeOfDeliveryFlavourID)
		{
			if (organisationID == null)
				throw new NullPointerException("organisationID"); //$NON-NLS-1$

			if (modeOfDeliveryFlavourID == null)
				throw new NullPointerException("modeOfDeliveryFlavourID"); //$NON-NLS-1$

			this.organisationID = organisationID;
			this.modeOfDeliveryFlavourID = modeOfDeliveryFlavourID;
		}
		private String organisationID;
		private String modeOfDeliveryFlavourID;

		public String getOrganisationID() {
			return organisationID;
		}
		public String getModeOfDeliveryFlavourID() {
			return modeOfDeliveryFlavourID;
		}
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof ModeOfDeliveryFlavourRef))
				return false;

			ModeOfDeliveryFlavourRef other = (ModeOfDeliveryFlavourRef)obj;

			return
				this.organisationID.equals(other.organisationID) &&
				this.modeOfDeliveryFlavourID.equals(other.modeOfDeliveryFlavourID);
		}
		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return organisationID.hashCode() ^ modeOfDeliveryFlavourID.hashCode();
		}
	}
	
	/**
	 * This method is called before {@link #init()}.
	 */
	void addModeOfDeliveryRef(ModeOfDeliveryRef modeOfDeliveryRef);

	/**
	 * This method is not called by the framework, but allows programmatic modification
	 * at a later time. You should call {@link #init()} again after you executed
	 * this method!
	 */
	void removeModeOfDeliveryRef(ModeOfDeliveryRef modeOfDeliveryRef);
	
	Collection<ModeOfDeliveryRef> getModeOfDeliveryRefs();

	/**
	 * This method is called before {@link #init()}.
	 */
	void addModeOfDeliveryFlavourRef(ModeOfDeliveryFlavourRef modeOfDeliveryFlavourRef);

	/**
	 * This method is not called by the framework, but allows programmatic modification
	 * at a later time. You should call {@link #init()} again after you executed
	 * this method!
	 */
	void removeModeOfDeliveryFlavourRef(ModeOfDeliveryFlavourRef modeOfDeliveryFlavourRef);
	
	Collection<ModeOfDeliveryFlavourRef> getModeOfDeliveryFlavourRefs();

	/**
	 * This method is called after the parameters are set. It might be called multiple
	 * times, if the same class is registered more than once (and
	 * <tt>modeOfDelivery</tt>s or <tt>modeOfDeliveryFlavour</tt> are added for a
	 * second registration).
	 */
	void init();

	/**
	 * This method is called after {@link #init()}. It will be called multiple times, that
	 * is always when a new delivery process is started.
	 * <p>
	 * In your implementation of this method, you must create an instance of your
	 * implementation of {@link ClientDeliveryProcessor}. You do NOT need to initialize it at all,
	 * because all the setters and the method {@link ClientDeliveryProcessor#init()} is
	 * called by the framework.
	 */
	ClientDeliveryProcessor createClientDeliveryProcessor();

}
