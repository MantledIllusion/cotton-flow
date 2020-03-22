package com.mantledillusion.vaadin.cotton;

import java.util.*;

import com.mantledillusion.vaadin.cotton.event.user.BeforeLogoutEvent;
import com.mantledillusion.vaadin.cotton.event.responsive.BeforeResponsiveRefreshEvent;
import com.mantledillusion.vaadin.cotton.viewpresenter.Responsive;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.QueryParameters;

/**
 * Offers static methods in the web environment context of the current {@link CottonUI} instance.
 */
public final class WebEnv {

    private WebEnv() {
    }

    // #########################################################################################################################################
    // ############################################################## NAVIGATION ###############################################################
    // #########################################################################################################################################

    /**
     * Navigates to the given location.
     *
     * @param location The location to navigate to; might <b>not</b> be null.
     * @see UI#navigate(String)
     */
    public static void navigateTo(String location) {
        CottonUI.current().navigate(location);
    }

    /**
     * Navigates to the given location.
     *
     * @param location        The location to navigate to; might <b>not</b> be null.
     * @param queryParameters The {@link QueryParameters} to set; might <b>not</b> be null.
     * @see UI#navigate(String, QueryParameters)
     */
    public static void navigateTo(String location, QueryParameters queryParameters) {
        CottonUI.current().navigate(location, queryParameters);
    }

    /**
     * Navigates to the given location.
     *
     * @param navigationTarget The view to whose location to navigate to; might <b>not</b> be null.
     * @see UI#navigate(Class)
     */
    public static void navigateTo(Class<? extends Component> navigationTarget) {
        CottonUI.current().navigate(navigationTarget);
    }

    /**
     * Navigates to the given location.
     *
     * @param <T>              url parameter type
     * @param <C>              navigation target type
     * @param navigationTarget The view to whose location to navigate to; might <b>not</b> be null.
     * @param parameter        The navigation parameter to pass; might be null.
     * @see UI#navigate(Class, Object)
     */
    public static <T, C extends Component & HasUrlParameter<T>> void navigateTo(Class<? extends C> navigationTarget,
                                                                                T parameter) {
        CottonUI.current().navigate(navigationTarget, parameter);
    }

    // #########################################################################################################################################
    // ############################################################# LOCALIZATION ##############################################################
    // #########################################################################################################################################

    /**
     * Checks whether there is a localization present for the given msgId in the {@link ResourceBundle} of the current
     * session's locale.
     *
     * @param msgId The message if to check; might be null
     * @return True if there is a localization for the given id, false otherwise
     */
    public static boolean canTranslate(String msgId) {
        return CottonServletService.SessionBean.current(Localizer.class).canTranslate(msgId, Localizer.currentLang());
    }

    /**
     * Localizes the given message identifier with the current session's locale using the {@link ResourceBundle}s
     * configured at the {@link CottonServlet} for that language.
     * <p>
     * No message parameters will be injected.
     * <p>
     * Depending on the current language's {@link Locale}, the given message parameters may also be localized during
     * insertion into the message.
     *
     * @param <T>   The message parameter type
     * @param msgId The message id to localize; might be null or not even a message id.
     * @return A localized and parameter filled message, or the given msgId if localization was not possible
     */
    public static <T> String getTranslation(String msgId) {
        return CottonServletService.SessionBean.current(Localizer.class).getTranslation(msgId, Localizer.currentLang(),
                Collections.emptyMap());
    }

    /**
     * Localizes the given message identifier with the current session's locale using the {@link ResourceBundle}s
     * configured at the {@link CottonServlet} for that language.
     * <p>
     * The given message parameters will be injected by their index, so a <code>{0}</code> block in the message will be
     * replaced with the first given parameter.
     * <p>
     * Depending on the current language's {@link Locale}, the given message parameters may also be localized during
     * insertion into the message.
     *
     * @param <T>                      The message parameter type
     * @param msgId                    The message id to localize; may be null or not even a message id.
     * @param indexedMessageParameters The parameters to inject into the localized message. Will only be used if the
     *                                 message id could be localized.
     * @return A localized and parameter filled message, or the given msgId if localization was not possible
     */
    @SafeVarargs
    public static <T> String getTranslation(String msgId, T... indexedMessageParameters) {
        return CottonServletService.SessionBean.current(Localizer.class).getTranslation(msgId, Localizer.currentLang(),
                Collections.emptyMap(), indexedMessageParameters);
    }

    /**
     * Localizes the given message identifier with the current session's locale using the {@link ResourceBundle}s
     * configured at the {@link CottonServlet} for that language.
     * <p>
     * The given message parameters will be injected by their name, so a <code>{foobar}</code> block in the message
     * will be replaced with the message parameter whose key is 'foobar'.
     * <p>
     * Depending on the current language's {@link Locale}, the given message parameters may also be localized during
     * insertion into the message.
     *
     * @param <T>               The message parameter type
     * @param msgId             The message id to localize; may be null or not even a message id.
     * @param messageParameters The parameters to inject into the localized message. Will only be used if the message
     *                          id could be localized.
     * @return A localized and parameter filled message, or the given msgId if localization was not possible
     */
    @SuppressWarnings("unchecked")
    public static <T> String getTranslation(String msgId, Map<String, T> messageParameters) {
        return CottonServletService.SessionBean.current(Localizer.class).getTranslation(msgId, Localizer.currentLang(),
                (Map<String, Object>) messageParameters);
    }

    // #########################################################################################################################################
    // ################################################################ LOG IN #################################################################
    // #########################################################################################################################################

    /**
     * Will log in the given {@link User}.
     * <p>
     * Another {@link User} possibly logged in at the moment will automatically be attempted to be logged out if its
     * not equal to the given one.
     *
     * @param user The {@link User} to log in; might be null.
     * @return False if there is a {@link User} currently logged in whose {@link BeforeLogoutEvent} has not being
     * accepted, true otherwise
     */
    public static boolean logIn(User user) {
        if (!Objects.equals(user, getLoggedInUser())) {
            if (isLoggedIn() && !logOut()) {
                return false;
            }
            if (user != null) {
                CottonServletService.SessionBean.current(LoginHandler.class).login(user);
            }
        }
        return true;
    }

    /**
     * Will log out the current {@link User} if there is one.
     *
     * @return True if the {@link BeforeLogoutEvent} is accepted and the log out was successful, false otherwise
     */
    public static boolean logOut() {
        if (isLoggedIn()) {
            return CottonServletService.SessionBean.current(LoginHandler.class).logout();
        }
        return true;
    }

    /**
     * Returns whether there is currently a {@link User} logged in.
     *
     * @return True if there is a {@link User} logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return CottonServletService.SessionBean.current(LoginHandler.class).getUser() != null;
    }

    /**
     * Returns the currently logged in {@link User}, if there is one.
     *
     * @param <U> The type of user that is expected to be logged in.
     * @return The {@link User} currently logged in, or null, if there is none
     */
    @SuppressWarnings("unchecked")
    public static <U extends User> U getLoggedInUser() {
        return (U) CottonServletService.SessionBean.current(LoginHandler.class).getUser();
    }

    // #########################################################################################################################################
    // ############################################################## RESPONSIVE ###############################################################
    // #########################################################################################################################################

    /**
     * If there currently is a {@link Responsive} view being displayed, this method will cause that view to adapt to
     * the client browser's current state. If there is no adaption required, nothing will happen.
     * <p>
     * This method should only be called after {@link BeforeResponsiveRefreshEvent#decline()} has been used, so the
     * automatic adaption by the framework was interrupted.
     * <p>
     * In case an adaption needs to take place, the {@link BeforeResponsiveRefreshEvent} caused will have the property
     * {@link BeforeResponsiveRefreshEvent#isForced()}=true set, so listeners to the event will not be able to decline it.
     */
    public static void adaptResponsive() {
        CottonUI.current().getInternals().setExtendedClientDetails(null);
        CottonUI.current().getPage().retrieveExtendedClientDetails(clientDetails -> {
            CottonUI.current().getChildren().
                    filter(child -> CottonServletService.CottonResponsiveWrapper.class.isAssignableFrom(child.getClass())).
                    map(child -> (CottonServletService.CottonResponsiveWrapper) child).
                    forEach(responsiveWrapper -> responsiveWrapper.adaptIfRequired(clientDetails.getWindowInnerWidth(),
                            clientDetails.getWindowInnerHeight(), Responsive.Alternative.AdaptionMode.FORCE));
        });
    }
}
