package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.grid.Grid;

/**
 * {@link ComponentBuilder} for {@link Grid}s.
 */
public class GridBuilder<E> extends AbstractComponentBuilder<Grid<E>, GridBuilder<E>>
        implements HasSizeBuilder<Grid<E>, GridBuilder<E>>, HasStyleBuilder<Grid<E>, GridBuilder<E>>,
        FocusableBuilder<Grid<E>, GridBuilder<E>>, HasEnabledBuilder<Grid<E>, GridBuilder<E>>,
        HasItemsBuilder<Grid<E>, E, GridBuilder<E>>, HasDataProviderBuilder<Grid<E>, E, GridBuilder<E>> {

    @Override
    public Grid<E> instantiate() {
        return new Grid<>();
    }
}
