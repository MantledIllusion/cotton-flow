package com.mantledillusion.vaadin.cotton;

import java.util.Set;

import com.mantledillusion.vaadin.cotton.viewpresenter.Restricted;

/**
 * Interface for types that represent a single {@link User} of an
 * {@link CottonUI}.
 */
public interface User {

	/**
	 * Has to return whether this {@link User} instance owns the rights of the given
	 * rightIds.
	 * <P>
	 * This method will be called whenever the {@link User} tries to navigate to a
	 * URL whose view is @{@link Restricted} to {@link User}s with certain rights.
	 * 
	 * @param rightIds The IDs of the rights this {@link User} is asked to have;
	 *                 never null, might <b>not</b> be empty.
	 * @return True if the {@link User} owns <b>all</b> of the rights behind the
	 *         given IDs, false otherwise.
	 */
	boolean hasRights(Set<String> rightIds);
}
