package com.chance.ayden.videoservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.EnumAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.PrimitiveConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.StringConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.StringConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.AtomicBooleanAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.AtomicIntegerAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.AtomicLongAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.BigDecimalAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.BigIntegerAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.BooleanAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ByteArrayAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ByteAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ByteBufferAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.CharSequenceAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.CharacterArrayAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.CharacterAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.DocumentAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.DoubleAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.DurationAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.FloatAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.InstantAsStringAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.IntegerAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ListAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.LocalDateAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.LocalDateTimeAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.LocalTimeAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.LocaleAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.LongAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.MapAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.MonthDayAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.OffsetDateTimeAsStringAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.OptionalDoubleAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.OptionalIntAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.OptionalLongAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.PeriodAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.SdkBytesAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.SdkNumberAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.SetAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ShortAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.StringAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.StringBufferAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.StringBuilderAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.UriAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.UrlAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.UuidAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ZoneIdAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ZoneOffsetAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ZonedDateTimeAsStringAttributeConverter;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;


public final class MyAttributeConverterProvider implements AttributeConverterProvider {
    private static final MyAttributeConverterProvider INSTANCE = getDefaultBuilder().build();

    private static final Logger log = Logger.loggerFor(MyAttributeConverterProvider.class);

    private final ConcurrentHashMap<EnhancedType<?>, AttributeConverter<?>> converterCache =
        new ConcurrentHashMap<>();

    private MyAttributeConverterProvider(Builder builder) {
        // Converters are used in the REVERSE order of how they were added to the builder.
        for (int i = builder.converters.size() - 1; i >= 0; i--) {
            AttributeConverter<?> converter = builder.converters.get(i);
            converterCache.put(converter.type(), converter);

            if (converter instanceof PrimitiveConverter) {
                PrimitiveConverter<?> primitiveConverter = (PrimitiveConverter<?>) converter;
                converterCache.put(primitiveConverter.primitiveType(), converter);
            }
        }
    }

    /**
     * Returns an attribute converter provider with all default converters set.
     */
    public MyAttributeConverterProvider() {
        this(getDefaultBuilder());
    }

    /**
     * Returns an attribute converter provider with all default converters set.
     */
    public static MyAttributeConverterProvider create() {
        return INSTANCE;
    }

    /**
     * Equivalent to {@code builder(EnhancedType.of(Object.class))}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Find a converter that matches the provided type. If one cannot be found, throw an exception.
     */
    @Override
    public <T> AttributeConverter<T> converterFor(EnhancedType<T> type) {
        if (type.rawClass() == UUID.class) {
		  return (AttributeConverter<T>) UUIDAttributeConverter.create();
		}
	  return null;
	}

    /**
     * Find a converter that matches the provided type. If one cannot be found, return empty.
     */
    @SuppressWarnings("unchecked")
    private <T> Optional<AttributeConverter<T>> findConverter(EnhancedType<T> type) {
        Optional<AttributeConverter<T>> converter = findConverterInternal(type);
        if (converter.isPresent()) {
            log.debug(() -> "Converter for " + type + ": " + converter.get().getClass().getTypeName());
        } else {
            log.debug(() -> "No converter available for " + type);
        }
        return converter;
    }

    private <T> Optional<AttributeConverter<T>> findConverterInternal(EnhancedType<T> type) {
        AttributeConverter<T> converter = (AttributeConverter<T>) converterCache.get(type);
        if (converter != null) {
            return Optional.of(converter);
        }

        if (type.rawClass().isAssignableFrom(Map.class)) {
            converter = createMapConverter(type);
        } else if (type.rawClass().isAssignableFrom(Set.class)) {
            converter = createSetConverter(type);
        } else if (type.rawClass().isAssignableFrom(List.class)) {
            EnhancedType<T> innerType = (EnhancedType<T>) type.rawClassParameters().get(0);
            AttributeConverter<?> innerConverter = findConverter(innerType)
                .orElseThrow(() -> new IllegalStateException("Converter not found for " + type));
            return Optional.of((AttributeConverter<T>) ListAttributeConverter.create(innerConverter));
        } else if (type.rawClass().isEnum()) {
            return Optional.of(EnumAttributeConverter.create(((EnhancedType<? extends Enum>) type).rawClass()));
        }

        if (type.tableSchema().isPresent()) {
            converter = DocumentAttributeConverter.create(type.tableSchema().get(), type);
        }

        if (converter != null && shouldCache(type.rawClass())) {
            this.converterCache.put(type, converter);
        }

        return Optional.ofNullable(converter);
    }

    private boolean shouldCache(Class<?> type) {
        // Do not cache anonymous classes, to prevent memory leaks.
        return !type.isAnonymousClass();
    }

    @SuppressWarnings("unchecked")
    private <T> AttributeConverter<T> createMapConverter(EnhancedType<T> type) {
        EnhancedType<?> keyType = type.rawClassParameters().get(0);
        EnhancedType<T> valueType = (EnhancedType<T>) type.rawClassParameters().get(1);

        StringConverter<?> keyConverter = StringConverterProvider.defaultProvider().converterFor(keyType);
        AttributeConverter<?> valueConverter = findConverter(valueType)
            .orElseThrow(() -> new IllegalStateException("Converter not found for " + type));

        return (AttributeConverter<T>) MapAttributeConverter.mapConverter(keyConverter, valueConverter);
    }

    @SuppressWarnings("unchecked")
    private <T> AttributeConverter<T> createSetConverter(EnhancedType<T> type) {
        EnhancedType<T> innerType = (EnhancedType<T>) type.rawClassParameters().get(0);
        AttributeConverter<?> innerConverter = findConverter(innerType)
            .orElseThrow(() -> new IllegalStateException("Converter not found for " + type));

        return (AttributeConverter<T>) SetAttributeConverter.setConverter(innerConverter);
    }

    private static Builder getDefaultBuilder() {
        return MyAttributeConverterProvider.builder().addConverter(UUIDAttributeConverter.create());
    }

    /**
     * A builder for configuring and creating {@link MyAttributeConverterProvider}s.
     */
    @NotThreadSafe
    public static class Builder {
        private List<AttributeConverter<?>> converters = new ArrayList<>();

        private Builder() {
        }

        public Builder addConverter(AttributeConverter<?> converter) {
            Validate.paramNotNull(converter, "converter");
            this.converters.add(converter);
            return this;
        }

        public MyAttributeConverterProvider build() {
            return new MyAttributeConverterProvider(this);
        }
    }
}
