package com.mantledillusion.vaadin.cotton.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;
import org.apache.commons.lang3.ObjectUtils;

import com.mantledillusion.data.epiphy.context.PropertyRoute;
import com.mantledillusion.data.epiphy.interfaces.ReadableProperty;
import com.mantledillusion.data.epiphy.interfaces.WriteableProperty;
import com.mantledillusion.data.epiphy.interfaces.function.ContextableProperty;
import com.mantledillusion.data.epiphy.interfaces.function.IdentifyableProperty;
import com.mantledillusion.data.epiphy.interfaces.type.ListedProperty;
import com.mantledillusion.data.epiphy.interfaces.type.NodedProperty;
import com.mantledillusion.vaadin.cotton.exception.http900.Http901IllegalArgumentException;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.shared.Registration;

abstract class ModelBinder<ModelType> implements ModelHandler<ModelType> {

	private interface Procedure {

		void trigger();
	}

	private interface Binding {

		void update(PropertyContext context, UpdateType type);

		void unbind();
	}

	static enum UpdateType {
		EXCHANGE, ADD, REMOVE;
	}

	private final PropertyContext context;
	private final Map<ReadableProperty<ModelType, ?>, List<Binding>> bindings = new IdentityHashMap<>();

	protected ModelBinder(PropertyContext context) {
		this.context = context;
	}

	protected PropertyContext getContext() {
		return this.context;
	}

	// ######################################################################################################################################
	// ########################################################## CONSUMER BINDING ##########################################################
	// ######################################################################################################################################

	private static final class ConsumerBinding implements Binding {

		private final Procedure valueReader;

		private ConsumerBinding(Procedure valueReader) {
			this.valueReader = valueReader;
		}

		@Override
		public void update(PropertyContext context, UpdateType type) {
			this.valueReader.trigger();
		}

		@Override
		public void unbind() {
			// Not required
		}
	}

	/**
	 * Binds the given {@link Consumer} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link Consumer} to bind.
	 * @param consumer
	 *            The {@link Consumer} to bind; might <b>not</b> be null.
	 * @param property
	 *            The {@link ReadableProperty} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType> void bind(Consumer<FieldValueType> consumer,
			ReadableProperty<ModelType, FieldValueType> property) {
		if (consumer == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + Consumer.class.getSimpleName());
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> consumer.accept(ModelBinder.this.get(property));

		addBinding(property, new ConsumerBinding(valueReader));
	}

	/**
	 * Binds the given {@link Consumer} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link Consumer} to bind.
	 * @param <PropertyValueType>
	 *            The value type of the property to bind with.
	 * @param consumer
	 *            The {@link Consumer} to bind; might <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use for conversion between the field's
	 *            and the properties' value types; might <b>not</b> be null.
	 * @param property
	 *            The {@link ReadableProperty} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType, PropertyValueType> void bind(Consumer<FieldValueType> consumer,
			Converter<FieldValueType, PropertyValueType> converter,
			ReadableProperty<ModelType, PropertyValueType> property) {
		if (consumer == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + Consumer.class.getSimpleName());
		} else if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null converter");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> consumer.accept(converter.toField(ModelBinder.this.get(property)));

		addBinding(property, new ConsumerBinding(valueReader));
	}

	// ######################################################################################################################################
	// ########################################################## HASVALUE BINDING ##########################################################
	// ######################################################################################################################################

	@SuppressWarnings("rawtypes")
	private static final class HasValueBinding implements Binding, ValueChangeListener {

		private static final long serialVersionUID = 1L;

		private final Procedure valueReader;
		private final Procedure valueWriter;
		private final Procedure enablementUpdater;
		private final Registration registration;
		private boolean synchronizing = false;

		@SuppressWarnings("unchecked")
		private HasValueBinding(HasValue<?, ?> hasValue, Procedure valueReader, Procedure valueWriter,
				Procedure enablementSwitch) {
			this.valueReader = valueReader;
			this.valueWriter = valueWriter;
			this.enablementUpdater = enablementSwitch;
			this.registration = hasValue.addValueChangeListener(this);
		}

		@Override
		public synchronized void valueChanged(ValueChangeEvent event) {
			if (!this.synchronizing) {
				this.synchronizing = true;
				this.valueWriter.trigger();
				this.synchronizing = false;
			}
		}

		@Override
		public synchronized void update(PropertyContext context, UpdateType type) {
			if (!this.synchronizing) {
				this.synchronizing = true;
				this.enablementUpdater.trigger();
				this.valueReader.trigger();
				this.synchronizing = false;
			}
		}

		@Override
		public void unbind() {
			this.registration.remove();
		}
	}

	/**
	 * Binds the given {@link HasValue} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link HasValue} to bind.
	 * @param hasValue
	 *            The {@link HasValue} to bind; might <b>not</b> be null.
	 * @param property
	 *            The {@link ReadableProperty} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType> void bind(HasValue<?, FieldValueType> hasValue,
			ReadableProperty<ModelType, FieldValueType> property) {
		if (hasValue == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + HasValue.class.getSimpleName());
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> hasValue
				.setValue(ObjectUtils.defaultIfNull(ModelBinder.this.get(property), hasValue.getEmptyValue()));
		Procedure valueWriter;
		if (property instanceof WriteableProperty) {
			valueWriter = () -> ModelBinder.this.set((WriteableProperty<ModelType, FieldValueType>) property,
					hasValue.getValue());
		} else {
			valueWriter = () -> {
			};
		}
		createHasValueBinding(property, hasValue, valueReader, valueWriter);
	}

	/**
	 * Binds the given {@link HasValue} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <FieldValueType>
	 *            The value type of the {@link HasValue} to bind.
	 * @param <PropertyValueType>
	 *            The value type of the property to bind with.
	 * @param hasValue
	 *            The {@link HasValue} to bind; might <b>not</b> be null.
	 * @param converter
	 *            The {@link Converter} to use for conversion between the field's
	 *            and the properties' value types; might <b>not</b> be null.
	 * @param property
	 *            The {@link ReadableProperty} to bind to; might <b>not</b> be null.
	 */
	public <FieldValueType, PropertyValueType> void bind(HasValue<?, FieldValueType> hasValue,
			Converter<FieldValueType, PropertyValueType> converter,
			ReadableProperty<ModelType, PropertyValueType> property) {
		if (hasValue == null) {
			throw new Http901IllegalArgumentException("Cannot bind a null " + HasValue.class.getSimpleName());
		} else if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null converter");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot bind using a null property");
		}

		Procedure valueReader = () -> hasValue.setValue(
				ObjectUtils.defaultIfNull(converter.toField(ModelBinder.this.get(property)), hasValue.getEmptyValue()));
		Procedure valueWriter;
		if (property instanceof WriteableProperty) {
			valueWriter = () -> ModelBinder.this.set((WriteableProperty<ModelType, PropertyValueType>) property,
					converter.toProperty(hasValue.getValue()));
		} else {
			valueWriter = () -> {
			};
		}
		createHasValueBinding(property, hasValue, valueReader, valueWriter);
	}

	private synchronized <FieldValueType> void createHasValueBinding(ReadableProperty<ModelType, ?> property,
			HasValue<?, FieldValueType> hasValue, Procedure valueReader, Procedure valueWriter) {
		Consumer<Boolean> enabler;
		if (hasValue instanceof HasEnabled) {
			enabler = enable -> ((HasEnabled) hasValue).setEnabled(enable);
		} else {
			enabler = enable -> {
			};
		}

		boolean writeable = property instanceof WriteableProperty;
		Procedure enablementSwitch = () -> {
			boolean exists = ModelBinder.this.exists(property);
			hasValue.setReadOnly(!writeable || !exists);
			enabler.accept(exists);
		};

		addBinding(property, new HasValueBinding(hasValue, valueReader, valueWriter, enablementSwitch));
	}

	// ######################################################################################################################################
	// ######################################################## DATAPROVIDER BINDING ########################################################
	// ######################################################################################################################################

	private static final class PropertyDataProviderBinding<R> implements Binding {

		private final Consumer<R> updater;
		private final BiConsumer<R, UpdateType> modifier;
		private final Function<PropertyContext, R> indexRetriever;

		private PropertyDataProviderBinding(Consumer<R> updater, BiConsumer<R, UpdateType> modifier,
				Function<PropertyContext, R> indexRetriever) {
			this.updater = updater;
			this.modifier = modifier;
			this.indexRetriever = indexRetriever;
		}

		@Override
		public void update(PropertyContext context, UpdateType type) {
			R propertyReference = this.indexRetriever.apply(context);
			if (type == UpdateType.EXCHANGE) {
				this.updater.accept(propertyReference);
			} else {
				this.modifier.accept(propertyReference, type);
			}
		}

		@Override
		public void unbind() {
			// not required
		}
	}
	
	// ListedProperty

	public static final class ListedPropertyDataProvider<E> extends ListDataProvider<E> {

		private static final long serialVersionUID = 1L;

		private final List<E> elements;

		private ListedPropertyDataProvider() {
			super(new ArrayList<>());
			this.elements = (List<E>) getItems();
		}

		private void exchange(List<E> elements) {
			this.elements.clear();
			if (elements != null) {
				this.elements.addAll(elements);
			}
			refreshAll();
		}

		private void add(E element, int index) {
			this.elements.add(index, element);
			this.refreshItem(element);
		}

		private void remove(int index) {
			E element = this.elements.remove(index);
			this.refreshItem(element);
		}

		private void refresh(E element) {
			this.refreshItem(element);
		}
	}

	/**
	 * Builds and binds a {@link DataProvider} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <ElementType>
	 *            The element type of the {@link DataProvider} to provide.
	 * @param property
	 *            The {@link ListedProperty} to bind to; might <b>not</b> be null.
	 * @return A new {@link DataProvider} that is bound to the given
	 *         {@link ListedProperty}; never null
	 */
	public <ElementType> ListedPropertyDataProvider<ElementType> provide(ListedProperty<ModelType, ElementType> property) {
		if (property == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null property");
		}

		ListedPropertyDataProvider<ElementType> provider = new ListedPropertyDataProvider<>();

		Consumer<Integer> updater = index -> {
			List<ElementType> elements = ModelBinder.this.get(property);
			if (index == null) {
				provider.exchange(elements);
			} else {
				provider.refresh(elements.get(index));
			}
		};
		BiConsumer<Integer, UpdateType> modifier = (index, type) -> {
			if (type == UpdateType.ADD) {
				List<ElementType> elements = ModelBinder.this.get(property);
				ElementType element = elements.get(index);
				provider.add(element, index);
			} else if (type == UpdateType.REMOVE) {
				provider.remove(index);
			}
		};
		Function<PropertyContext, Integer> indexRetriever = context -> context.getKey(property);

		addBinding(property, new PropertyDataProviderBinding<Integer>(updater, modifier, indexRetriever));

		return provider;
	}

	/**
	 * Builds and binds a {@link DataProvider} to the given property of this
	 * {@link ModelHandler}.
	 * 
	 * @param <ElementType>
	 *            The element type of the {@link DataProvider} to provide.
	 * @param <PropertyValueType>
	 *            The value type of the property to bind.
	 * @param converter
	 *            The {@link Converter} to use for conversion between the provider's
	 *            and the properties' element types; might <b>not</b> be null.
	 * @param property
	 *            The {@link ListedProperty} to bind to; might <b>not</b> be null.
	 * @return A new {@link DataProvider} that is bound to the given
	 *         {@link ListedProperty}; never null
	 */
	public <ElementType, PropertyValueType> ListedPropertyDataProvider<ElementType> provide(
			Converter<ElementType, PropertyValueType> converter,
			ListedProperty<ModelType, PropertyValueType> property) {
		if (converter == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null converter");
		} else if (property == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null property");
		}

		ListedPropertyDataProvider<ElementType> provider = new ListedPropertyDataProvider<>();

		Consumer<Integer> updater = index -> {
			@SuppressWarnings("unchecked")
			List<ElementType> elements = ObjectUtils
					.defaultIfNull(ModelBinder.this.get(property), (List<PropertyValueType>) Collections.emptyList())
					.stream().map(converter::toField).collect(Collectors.toList());
			if (index == null) {
				provider.exchange(elements);
			} else {
				provider.refresh(elements.get(index));
			}
		};
		BiConsumer<Integer, UpdateType> modifier = (index, type) -> {
			if (type == UpdateType.ADD) {
				@SuppressWarnings("unchecked")
				List<ElementType> elements = ObjectUtils
						.defaultIfNull(ModelBinder.this.get(property),
								(List<PropertyValueType>) Collections.emptyList())
						.stream().map(converter::toField).collect(Collectors.toList());
				ElementType element = elements.get(index);
				provider.add(element, index);
			} else if (type == UpdateType.REMOVE) {
				provider.remove(index);
			}
		};
		Function<PropertyContext, Integer> indexRetriever = context -> context.getKey(property);

		addBinding(property, new PropertyDataProviderBinding<Integer>(updater, modifier, indexRetriever));

		return provider;
	}
	
	// NodedProperty
	
	public static final class NodePropertyDataProvider<N> extends TreeDataProvider<N> {

		private static final long serialVersionUID = 1L;
		
		private final TreeData<N> treeData;
		
		public NodePropertyDataProvider() {
			super(new TreeData<>());
			this.treeData = getTreeData();
		}
		
		@SuppressWarnings("unchecked")
		private void exchange(N root, NodedProperty<?, N> property) {
			this.treeData.clear();
			if (root != null) {
				this.treeData.addRootItems(root);
				addChildren(root, property);
			}
			refreshAll();
		}
		
		@SuppressWarnings("unchecked")
		private void add(N parent, N node, N sibling, NodedProperty<?, N> property) {
			this.treeData.addItems(parent, node);
			this.treeData.moveAfterSibling(node, sibling);
			addChildren(node, property);
			refreshAll();
		}
		
		private void addChildren(N node, NodedProperty<?, N> property) {
			if (node != null) {
				List<N> leaves = property.getLeaves(node);
				if (leaves != null) {
					for (N leaf: leaves) {
						this.treeData.addItem(node, leaf);
						addChildren(leaf, property);
					}
				}
			}
		}
		
		private void remove(N parent, int index) {
			N node = this.treeData.getChildren(parent).get(index);
			this.treeData.removeItem(node);
			refreshAll();
		}

		private void refresh(N node) {
			this.refreshItem(node);
		}
	}
	
	public <ElementType> NodePropertyDataProvider<ElementType> provide(NodedProperty<ModelType, ElementType> property) {
		if (property == null) {
			throw new Http901IllegalArgumentException("Cannot create a data provider using a null property");
		}

		NodePropertyDataProvider<ElementType> provider = new NodePropertyDataProvider<>();

		Consumer<int[]> updater = indices -> {
			if (indices == null) {
				provider.exchange(ModelBinder.this.get(property), property);
			} else {
				provider.refresh(ModelBinder.this.get(property, PropertyContext.of(PropertyRoute.of(property, indices))));
			}
		};
		BiConsumer<int[], UpdateType> modifier = (indices, type) -> {
			PropertyContext context = null;
			if (indices.length > 1) {
				context = PropertyContext.of(PropertyRoute.of(property, Arrays.copyOf(indices, indices.length-1)));
			}
			ElementType parent = ModelBinder.this.get(property, context);
			
			int index = indices[indices.length-1];
			if (type == UpdateType.ADD) {
				ElementType node = ModelBinder.this.get(property, PropertyContext.of(PropertyRoute.of(property, indices)));
				ElementType sibling = null;
				if (index > 0) {
					indices = indices.clone();
					indices[indices.length-1] = index-1;
					sibling = ModelBinder.this.get(property, PropertyContext.of(PropertyRoute.of(property, indices)));
				}
				provider.add(parent, node, sibling, property);
			} else if (type == UpdateType.REMOVE) {
				provider.remove(parent, index);
			}
		};
		Function<PropertyContext, int[]> indexRetriever = context -> context.getKey(property);

		addBinding(property, new PropertyDataProviderBinding<int[]>(updater, modifier, indexRetriever));

		return provider;
	}

	// ######################################################################################################################################
	// ########################################################## BINDING HANDLING ##########################################################
	// ######################################################################################################################################

	private void addBinding(ReadableProperty<ModelType, ?> property, Binding binding) {
		if (!this.bindings.containsKey(property)) {
			this.bindings.put(property, new ArrayList<>());
		}
		this.bindings.get(property).add(binding);
	}

	synchronized void update(UpdateType type) {
		this.bindings.forEach((property, bindings) -> bindings.forEach(binding -> binding.update(context, type)));
	}

	synchronized void update(IdentifyableProperty<ModelType> property, PropertyContext context, UpdateType type) {
		// RUN OVER ALL BINDINGS
		boundPropertyLoop: for (ReadableProperty<ModelType, ?> boundProperty : this.bindings.keySet()) {
			// IF THE UPDATE'S PROPERTY IS A PARENT OF THE BINDING'S PROPERTY...
			if (boundProperty.isParent(property)) {
				// GO OVER ALL OF THE UPDATE'S CONTEXTABLE PROPERTIES
				for (ContextableProperty<ModelType, ?, ?> contextedProperty : property.getContext()) {
					// CHECK IF THERE IS A KEY FOR THE CONTEXTED PROPERTY THAT IS UNEQUAL TO THE ONE IN THIS BINDER'S CONTEXT
					if (context.containsKey(contextedProperty) && this.context.containsKey(contextedProperty)
							&& !context.getKey(contextedProperty).equals(this.context.getKey(contextedProperty))) {
						// IF THERE IS ONE, THE BINDING IS NOT AFFECTED BY THE UPDATE
						continue boundPropertyLoop;
					}
				}
				this.bindings.get(boundProperty).forEach(binding -> binding.update(context, type));
			}
		}
	}

	@PreDestroy
	private synchronized void destroy() {
		Iterator<Entry<ReadableProperty<ModelType, ?>, List<Binding>>> mapIter = this.bindings.entrySet().iterator();
		while (mapIter.hasNext()) {
			Iterator<Binding> bindingIter = mapIter.next().getValue().iterator();
			while (bindingIter.hasNext()) {
				bindingIter.next().unbind();
				bindingIter.remove();
			}
			mapIter.remove();
		}
	}
}