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

package org.nightlabs.jfire.trade.transfer.pay;

import java.util.Collection;

/**
 * Rather than implementing this interface directly, you should extend
 * {@link org.nightlabs.jfire.trade.transfer.pay.AbstractClientPaymentProcessorFactory}.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface ClientPaymentProcessorFactory
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

	public static class ModeOfPaymentRef {
		public ModeOfPaymentRef(String organisationID, String modeOfPaymentID)
		{
			if (organisationID == null)
				throw new NullPointerException("organisationID"); //$NON-NLS-1$

			if (modeOfPaymentID == null)
				throw new NullPointerException("modeOfPaymentID"); //$NON-NLS-1$

			this.organisationID = organisationID;
			this.modeOfPaymentID = modeOfPaymentID;
		}
		private String organisationID;
		private String modeOfPaymentID;

		public String getOrganisationID() {
			return organisationID;
		}
		public String getModeOfPaymentID() {
			return modeOfPaymentID;
		}
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			if (!(obj instanceof ModeOfPaymentRef))
				return false;
			
			ModeOfPaymentRef other = (ModeOfPaymentRef)obj;

			return
				this.organisationID.equals(other.organisationID) &&
				this.modeOfPaymentID.equals(other.modeOfPaymentID);
		}
		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
			return organisationID.hashCode() ^ modeOfPaymentID.hashCode();
		}
	}

	public static class ModeOfPaymentFlavourRef {
		public ModeOfPaymentFlavourRef(String organisationID, String modeOfPaymentFlavourID)
		{
			if (organisationID == null)
				throw new NullPointerException("organisationID"); //$NON-NLS-1$

			if (modeOfPaymentFlavourID == null)
				throw new NullPointerException("modeOfPaymentFlavourID"); //$NON-NLS-1$

			this.organisationID = organisationID;
			this.modeOfPaymentFlavourID = modeOfPaymentFlavourID;
		}
		private String organisationID;
		private String modeOfPaymentFlavourID;

		public String getOrganisationID() {
			return organisationID;
		}
		public String getModeOfPaymentFlavourID() {
			return modeOfPaymentFlavourID;
		}
		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			if (!(obj instanceof ModeOfPaymentFlavourRef))
				return false;

			ModeOfPaymentFlavourRef other = (ModeOfPaymentFlavourRef)obj;

			return
				this.organisationID.equals(other.organisationID) &&
				this.modeOfPaymentFlavourID.equals(other.modeOfPaymentFlavourID);
		}
		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
			return organisationID.hashCode() ^ modeOfPaymentFlavourID.hashCode();
		}
	}

	/**
	 * This method is called before {@link #init()}.
	 */
	void addModeOfPaymentRef(ModeOfPaymentRef modeOfPaymentRef);

	/**
	 * This method is not called by the framework, but allows programmatic modification
	 * at a later time. You should call {@link #init()} again after you executed
	 * this method!
	 */
	void removeModeOfPaymentRef(ModeOfPaymentRef modeOfPaymentRef);
	Collection getModeOfPaymentRefs();

	/**
	 * This method is called before {@link #init()}.
	 */
	void addModeOfPaymentFlavourRef(ModeOfPaymentFlavourRef modeOfPaymentFlavourRef);

	/**
	 * This method is not called by the framework, but allows programmatic modification
	 * at a later time. You should call {@link #init()} again after you executed
	 * this method!
	 */
	void removeModeOfPaymentFlavourRef(ModeOfPaymentFlavourRef modeOfPaymentFlavourRef);
	Collection getModeOfPaymentFlavourRefs();

	/**
	 * This method is called after the parameters are set. It might be called multiple
	 * times, if the same class is registered more than once (and
	 * <tt>modeOfPayment</tt>s or <tt>modeOfPaymentFlavour</tt> are added for a
	 * second registration).
	 */
	void init();

	/**
	 * This method is called after {@link #init()}. It will be called multiple times, that
	 * is always when a new payment process is started.
	 * <p>
	 * In your implementation of this method, you must create an instance of your
	 * implementation of {@link ClientPaymentProcessor}. You do NOT need to initialize it at all,
	 * because all the setters and the method {@link ClientPaymentProcessor#init()} is
	 * called by the framework.
	 */
	ClientPaymentProcessor createClientPaymentProcessor();
}
