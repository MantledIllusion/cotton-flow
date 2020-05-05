package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.*;

import java.util.function.Supplier;

/**
 * {@link ComponentBuilder} for {@link HtmlContainer}s.
 */
public class HtmlContainerBuilder<C extends HtmlContainer & ClickNotifier<C>> extends AbstractComponentBuilder<C, HtmlContainerBuilder<C>> implements
        HasElementBuilder<C, HtmlContainerBuilder<C>>,
        HasSizeBuilder<C, HtmlContainerBuilder<C>>,
        HasComponentsBuilder<C, HtmlContainerBuilder<C>>,
        HasTextBuilder<C, HtmlContainerBuilder<C>>,
        ClickableBuilder<C, HtmlContainerBuilder<C>> {

    /**
     * Simplistic {@link HtmlContainer} extension allowing to set HTML code as text.
     */
    @Tag(Tag.LABEL)
    public static class HtmlLabel extends HtmlContainer implements ClickNotifier<HtmlLabel> {

        private HtmlLabel() {}

        @Override
        public void setText(String text) {
            getElement().setProperty("innerHTML", text);
        }
    }

    private final Supplier<C> componentSupplier;

    private HtmlContainerBuilder(Supplier<C> componentSupplier) {
        this.componentSupplier = componentSupplier;
    }

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static HtmlContainerBuilder<HtmlLabel> createLabel() {
        return new HtmlContainerBuilder<>(HtmlLabel::new);
    }

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static HtmlContainerBuilder<H1> createH1() {
        return new HtmlContainerBuilder<>(H1::new);
    }

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static HtmlContainerBuilder<H2> createH2() {
        return new HtmlContainerBuilder<>(H2::new);
    }

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static HtmlContainerBuilder<H3> createH3() {
        return new HtmlContainerBuilder<>(H3::new);
    }

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static HtmlContainerBuilder<H4> createH4() {
        return new HtmlContainerBuilder<>(H4::new);
    }

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static HtmlContainerBuilder<H5> createH5() {
        return new HtmlContainerBuilder<>(H5::new);
    }

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static HtmlContainerBuilder<H6> createH6() {
        return new HtmlContainerBuilder<>(H6::new);
    }

    @Override
    protected C instantiate() {
        return this.componentSupplier.get();
    }
}
