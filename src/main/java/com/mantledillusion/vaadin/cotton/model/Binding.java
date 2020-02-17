package com.mantledillusion.vaadin.cotton.model;

import com.mantledillusion.data.epiphy.context.Context;
import org.apache.commons.lang3.ObjectUtils;

import java.util.function.Supplier;

/**
 * Represents the configurable binding of a property.
 */
public abstract class Binding<FieldValueType> {

    /**
     * Represents the modes a {@link Binding} can allow access of a properties data to.
     */
    public enum AccessMode {

        /**
         * Full access to the property, reading and writing.
         */
        READ_WRITE(true),

        /**
         * Reading access on the property only, no writing.
         */
        READ_ONLY(true),

        /**
         * Neither reading nor writing access, but the properties' existence is not denied.
         */
        MASKED(false),

        /**
         * Neither reading nor writing access, even the properties' existence is denied.
         */
        HIDDEN(false);

        private final boolean coupled;

        AccessMode(boolean coupled) {
            this.coupled = coupled;
        }

        static Supplier<AccessMode> chain(Supplier<AccessMode> auditor1, Supplier<AccessMode> auditor2) {
            return () -> or(auditor1.get(), auditor2.get());
        }

        private static AccessMode or(AccessMode mode1, AccessMode mode2) {
            return mode1 == null ? mode2 : (mode2 == null ? mode1 : (mode1.ordinal() < mode2.ordinal() ? mode1 : mode2));
        }
    }

    private AccessMode accessMode;
    private Supplier<AccessMode> bindingAuditor;
    private FieldValueType maskedValue;

    Binding(Supplier<AccessMode> bindingAuditor) {
        this.bindingAuditor = bindingAuditor;
    }

    protected final AccessMode getAccessMode() {
        return this.accessMode;
    }

    protected final void refreshAccessMode() {
        this.accessMode = ObjectUtils.defaultIfNull(this.bindingAuditor.get(), AccessMode.READ_WRITE);
        accessModeChanged(this.accessMode.coupled);
    }

    /**
     * Builder method, adds the given binding auditor to the binding to restrict it.
     * <p>
     * The given {@link Supplier}'s result will be used to determine at which {@link AccessMode} this binding is
     * expected to allow access to the data of its bound property.
     * <p>
     * When this method is never used so no binding auditor is ever specified, a default auditor will allow general
     * {@link AccessMode#READ_WRITE} access to the property.
     * <p>
     * When multiple binding auditors are specified using this method, the most generous {@link AccessMode} determined
     * by the auditors will be used.
     *
     * @param bindingAuditor The binding auditor; might <b>not</b> be null.
     * @return this
     */
    public final Binding<FieldValueType> withRestriction(Supplier<AccessMode> bindingAuditor) {
        this.bindingAuditor = AccessMode.chain(this.bindingAuditor, bindingAuditor);
        refreshAccessMode();
        return this;
    }

    protected FieldValueType getMaskedValue() {
        return maskedValue;
    }

    /**
     * Builder method, sets the value to use when the {@link Binding} is {@link AccessMode#MASKED}.
     * <p>
     * Note that different types of {@link Binding} implementations might handle using this value differently.
     *
     * @param maskedValue The value to use instead of the bound one; might be null.
     * @return this
     */
    public final Binding<FieldValueType> withMaskedValue(FieldValueType maskedValue) {
        this.maskedValue = maskedValue;
        refreshAccessMode();
        return this;
    }

    void accessModeChanged(boolean couple) {};

    abstract void valueChanged(Context context, ModelBinder.UpdateType type);
}
