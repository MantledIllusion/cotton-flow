package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.WebEnv;
import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.Configurer;
import com.mantledillusion.vaadin.cotton.component.mixin.HasComponentsBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasElementBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasEnabledBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasSizeBuilder;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * {@link ComponentBuilder} for {@link Dialog}s.
 */
public class DialogBuilder extends AbstractComponentBuilder<Dialog, DialogBuilder> implements
        HasElementBuilder<Dialog, DialogBuilder>,
        HasSizeBuilder<Dialog, DialogBuilder>,
        HasComponentsBuilder<Dialog, DialogBuilder>,
        HasEnabledBuilder<Dialog, DialogBuilder> {

    /**
     * {@link ComponentBuilder} for {@link Dialog}s with only a message and 0-&gt;n options for the user to choose from.
     */
    public static class BasicDialogBuilder extends AbstractEntityBuilder<Dialog, BasicDialogBuilder> {

        private final DialogBuilder parent;

        private BasicDialogBuilder(DialogBuilder parent, String msgId, Object... indexedMessageParameters) {
            super(parent);
            this.parent = parent.configure(dialog -> parent.set(ButtonBuilder.class, ButtonBuilder.create().setSizeUndefined()), true);

            configure(dialog -> {
                HorizontalLayout btnLayout = HorizontalLayoutBuilder.create().
                        setSizeUndefined().
                        setPadding(false).
                        setSpacing(true).
                        build();
                BasicDialogBuilder.this.set(HorizontalLayout.class, btnLayout);

                dialog.add(VerticalLayoutBuilder.create().
                        setWidthFull().
                        setHeightUndefined().
                        setPadding(true).
                        setSpacing(true).
                        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER).
                        add(LabelBuilder.create().
                                setWidthFull().
                                setHeightUndefined().
                                setValue(WebEnv.getTranslation(msgId, indexedMessageParameters)).
                                build()).
                        add(btnLayout).
                        build());
            }, true);
        }

        /**
         * Builder method, configures whether the {@link Dialog} closes upon the ESC key being pressed.
         *
         * @see Dialog#setCloseOnEsc(boolean)
         * @param close
         *            True if the {@link Dialog} should close, false otherwise.
         * @return this
         */
        public BasicDialogBuilder setCloseOnEsc(boolean close) {
            return configure(dialog -> dialog.setCloseOnEsc(close));
        }

        /**
         * Builder method, configures whether the {@link Dialog} closes upon a click being registered outside the dialog.
         *
         * @see Dialog#setCloseOnOutsideClick(boolean)
         * @param close
         *            True if the {@link Dialog} should close, false otherwise.
         * @return this
         */
        public BasicDialogBuilder setCloseOnOutsideClick(boolean close) {
            return configure(dialog -> dialog.setCloseOnOutsideClick(close));
        }

        /**
         * Builder method, configures a listener for the {@link Dialog} closing because of ESC being pressed or a click
         * outside being registered.
         *
         * @see Dialog#addDialogCloseActionListener(ComponentEventListener)
         * @param listener
         *            The listener to add; might <b>not</b> be null.
         * @return this
         */
        public BasicDialogBuilder addDialogCloseActionListener(ComponentEventListener<Dialog.DialogCloseActionEvent> listener) {
            return configure(dialog -> dialog.addDialogCloseActionListener(listener));
        }

        /**
         * Builder method, configures the {@link ButtonBuilder} to use when creating the {@link Dialog}'s buttons.
         *
         * @param buttonBuilder The {@link ButtonBuilder} to use; might <b>not</b> be null.
         * @return this;
         */
        public BasicDialogBuilder setButtonBuilder(ButtonBuilder buttonBuilder) {
            if (buttonBuilder == null) {
                throw new IllegalArgumentException("Cannot set a null button builder.");
            }
            return configure(dialog -> set(ButtonBuilder.class, buttonBuilder), true);
        }

        /**
         * Builder method, configures a {@link Button} for the basic {@link Dialog} as an option to close it.
         *
         * @param msgId The text of the {@link Button}, or a message id to translate via {@link WebEnv}; might be null.
         * @return this
         */
        public BasicDialogBuilder addOption(String msgId) {
            return addOption(msgId, null, null);
        }

        /**
         * Builder method, configures a {@link Button} for the basic {@link Dialog} as an option to close it.
         *
         * @param msgId The text of the {@link Button}, or a message id to translate via {@link WebEnv}; might be null.
         * @param listener A listener to call when this option is selected; might be null.
         * @return this
         */
        public BasicDialogBuilder addOption(String msgId, ComponentEventListener<ClickEvent<Button>> listener) {
            return addOption(msgId, null, listener);
        }

        /**
         * Builder method, configures a {@link Button} for the basic {@link Dialog} as an option to close it.
         *
         * @param icon The component to set as icon to the {@link Button}; might be null.
         * @return this
         */
        public BasicDialogBuilder addOption(Component icon) {
            return addOption(null, icon, null);
        }

        /**
         * Builder method, configures a {@link Button} for the basic {@link Dialog} as an option to close it.
         *
         * @param icon The component to set as icon to the {@link Button}; might be null.
         * @param listener A listener to call when this option is selected; might be null.
         * @return this
         */
        public BasicDialogBuilder addOption(Component icon, ComponentEventListener<ClickEvent<Button>> listener) {
            return addOption(null, icon, listener);
        }

        /**
         * Builder method, configures a {@link Button} for the basic {@link Dialog} as an option to close it.
         *
         * @param msgId The text of the {@link Button}, or a message id to translate via {@link WebEnv}; might be null.
         * @param icon The component to set as icon to the {@link Button}; might be null.
         * @return this
         */
        public BasicDialogBuilder addOption(String msgId, Component icon) {
            return addOption(msgId, icon, null);
        }

        /**
         * Builder method, configures a {@link Button} for the basic {@link Dialog} as an option to close it.
         *
         * @param msgId The text of the {@link Button}, or a message id to translate via {@link WebEnv}; might be null.
         * @param icon The component to set as icon to the {@link Button}; might be null.
         * @param listener A listener to call when this option is selected; might be null.
         * @return this
         */
        public BasicDialogBuilder addOption(String msgId, Component icon, ComponentEventListener<ClickEvent<Button>> listener) {
            return configure(dialog -> {
                Button btn = BasicDialogBuilder.this.get(ButtonBuilder.class).
                        setText(msgId).
                        setIcon(icon).
                        build();
                btn.addClickListener(event -> {
                    dialog.close();
                    if (listener != null) {
                        listener.onComponentEvent(event);
                    }
                });
                BasicDialogBuilder.this.get(HorizontalLayout.class).add(btn);
            });
        }

        /**
         * Creates a new {@link Dialog} instance using {@link #instantiate()}, applies all currently contained
         * {@link Configurer}s to it and returns it.
         * <p>
         * Also directly opens the {@link Dialog} once its build.
         *
         * @return A new {@link Dialog} instance, fully configured, never null
         */
        public Dialog build() {
            Dialog dialog = this.parent.build();
            apply(dialog);
            return dialog;
        }

        /**
         * Creates a new {@link Dialog} instance using {@link #instantiate()}, applies all currently contained
         * {@link Configurer}s to it and returns it.
         * <p>
         * Also directly opens the {@link Dialog} once its build.
         *
         * @return A new {@link Dialog} instance, fully configured, never null
         */
        public Dialog buildAndOpen() {
            Dialog dialog = this.parent.build();
            apply(dialog);
            dialog.open();
            return dialog;
        }
    }

    private DialogBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static DialogBuilder create() {
        return new DialogBuilder();
    }

    /**
     * Factory method for a very basic dialog that just contains a text and has 0-&gt;n options to close it with.
     *
     * @param msgId
     *            The basic {@link Dialog}'s text or localizable message to translate using {@link WebEnv}; might
     *            <b>not</b> be null.
     * @param indexedMessageParameters
     *            Additional parameters for {@link WebEnv#getTranslation(String, Object[])}; might be null.
     *
     * @return A new instance, never null.
     */
    public static BasicDialogBuilder createBasic(String msgId, Object... indexedMessageParameters) {
        return new BasicDialogBuilder(new DialogBuilder(), msgId, indexedMessageParameters);
    }

    @Override
    protected Dialog instantiate() {
        return new Dialog();
    }

    /**
     * Builder method, configures whether the {@link Dialog} closes upon the ESC key being pressed.
     *
     * @see Dialog#setCloseOnEsc(boolean)
     * @param close
     *            True if the {@link Dialog} should close, false otherwise.
     * @return this
     */
    public DialogBuilder setCloseOnEsc(boolean close) {
        return configure(dialog -> dialog.setCloseOnEsc(close));
    }

    /**
     * Builder method, configures whether the {@link Dialog} closes upon a click being registered outside the dialog.
     *
     * @see Dialog#setCloseOnOutsideClick(boolean)
     * @param close
     *            True if the {@link Dialog} should close, false otherwise.
     * @return this
     */
    public DialogBuilder setCloseOnOutsideClick(boolean close) {
        return configure(dialog -> dialog.setCloseOnOutsideClick(close));
    }

    /**
     * Builder method, configures the {@link Dialog} to open immediately upon {@link #build()} being called.
     *
     * @see Dialog#open()
     * @return this
     */
    public DialogBuilder open() {
        return configure(dialog -> dialog.open());
    }

    /**
     * Builder method, configures a listener for the {@link Dialog} opening/closing.
     *
     * @see Dialog#addOpenedChangeListener(ComponentEventListener)
     * @param listener
     *            The listener to add; might <b>not</b> be null.
     * @return this
     */
    public DialogBuilder addOpenedChangeListener(ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>> listener) {
        return configure(dialog -> dialog.addOpenedChangeListener(listener));
    }

    /**
     * Builder method, configures a listener for the {@link Dialog} closing because of ESC being pressed or a click
     * outside being registered.
     *
     * @see Dialog#addDialogCloseActionListener(ComponentEventListener)
     * @param listener
     *            The listener to add; might <b>not</b> be null.
     * @return this
     */
    public DialogBuilder addDialogCloseActionListener(ComponentEventListener<Dialog.DialogCloseActionEvent> listener) {
        return configure(dialog -> dialog.addDialogCloseActionListener(listener));
    }
}
