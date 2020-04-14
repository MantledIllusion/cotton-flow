package com.mantledillusion.vaadin.cotton.component.builders;

import com.mantledillusion.vaadin.cotton.component.ComponentBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.ClickableBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasComponentsBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasSizeBuilder;
import com.mantledillusion.vaadin.cotton.component.mixin.HasTextBuilder;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;

/**
 * {@link ComponentBuilder} for {@link Image}s.
 */
public class ImageBuilder extends AbstractComponentBuilder<Image, ImageBuilder> implements
        HasSizeBuilder<Image, ImageBuilder>,
        HasComponentsBuilder<Image, ImageBuilder>,
        HasTextBuilder<Image, ImageBuilder>,
        ClickableBuilder<Image, ImageBuilder> {

    private ImageBuilder() {}

    /**
     * Factory method for a new instance.
     *
     * @return A new instance, never null.
     */
    public static ImageBuilder create() {
        return new ImageBuilder();
    }

    @Override
    protected Image instantiate() {
        return new Image();
    }

    /**
     * Builder method, configures the {@link Image}'s source as an image available over the web.
     *
     * @see Image#setSrc(String)
     * @param src
     *            The URL of the image; might <b>not</b> be null.
     * @return this
     */
    public ImageBuilder setSrc(String src) {
        return configure(image -> image.setSrc(src));
    }

    /**
     * Builder method, configures the {@link Image}'s source as a streamable resource.
     *
     * @see Image#setSrc(AbstractStreamResource)
     * @param src
     *            The resource stream of the image; might <b>not</b> be null.
     * @return this
     */
    public ImageBuilder setSrc(AbstractStreamResource src) {
        return configure(image -> image.setSrc(src));
    }

    /**
     * Builder method, configures the {@link Image}'s source as a classpath resource.
     *
     * @see Image#setSrc(AbstractStreamResource)
     * @param resourceClass
     *            The class to retrieve the resource from; might <b>not</b> be null.
     * @param src
     *            The classpath to find the image under; might <b>not</b> be null.
     * @return this
     */
    public ImageBuilder setSrc(Class<?> resourceClass, String src) {
        return configure(image -> image.setSrc(new StreamResource(src.replace('/', '.'), () ->
                resourceClass.getClassLoader().getResourceAsStream(src))));
    }

    /**
     * Builder method, configures the {@link Image}'s alternative text.
     *
     * @see Image#setAlt(String)
     * @param alt
     *            The alternative text; might be null.
     * @return this
     */
    public ImageBuilder setAlt(String alt) {
        return configure(image -> image.setAlt(alt));
    }
}
