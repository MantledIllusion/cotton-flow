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

    private TreeGridBuilder() {

    }

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static TreeGridBuilder<Object> create() {
        return new TreeGridBuilder<>();
    }

    /**
     * Factory method for a new instance.
     *
     * @param <E> The element type.
     * @param elementType The class type of the element; might be null.
     * @return A new instance, never null.
     */
    public static <E> TreeGridBuilder<E> create(Class<E> elementType) {
        return new TreeGridBuilder<>();
    }

    @Override
    protected TreeGrid<E> instantiate() {
        return new TreeGrid<>();
    }
}
