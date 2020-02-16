package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.*;
import com.vaadin.flow.component.treegrid.TreeGrid;

/**
 * {@link ComponentBuilder} for {@link TreeGrid}s.
 */
public class TreeGridBuilder<E> extends AbstractComponentBuilder<TreeGrid<E>, TreeGridBuilder<E>>
        implements HasSizeBuilder<TreeGrid<E>, TreeGridBuilder<E>>, HasStyleBuilder<TreeGrid<E>, TreeGridBuilder<E>>,
        HasEnabledBuilder<TreeGrid<E>, TreeGridBuilder<E>>,
        HasItemsBuilder<TreeGrid<E>, E, TreeGridBuilder<E>>,
        HasHierarchicalDataProviderBuilder<TreeGrid<E>, E, TreeGridBuilder<E>> {

    @Override
    public TreeGrid<E> instantiate() {
        return new TreeGrid<>();
    }
}
