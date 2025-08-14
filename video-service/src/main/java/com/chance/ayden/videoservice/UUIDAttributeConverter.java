package com.chance.ayden.videoservice;

import java.util.UUID;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnhancedAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.string.ByteArrayStringConverter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * A converter between {@link UUID} and {@link AttributeValue}.
 */
public final class UUIDAttributeConverter implements AttributeConverter<UUID> {
  public static UUIDAttributeConverter create() {
	return new UUIDAttributeConverter();
  }

  @Override
  public EnhancedType<UUID> type() {
	return EnhancedType.of(UUID.class);
  }

  @Override
  public AttributeValueType attributeValueType() {
	return AttributeValueType.S;
  }

  @Override
  public AttributeValue transformFrom(UUID input) {
	return input == null ? AttributeValues.nullAttributeValue() : AttributeValue.fromS(input.toString());
  }

  @Override
  public UUID transformTo(AttributeValue input) {
	return Visitor.toUUID(input);
  }

  private static final class Visitor extends TypeConvertingVisitor<UUID> {
	private static final Visitor INSTANCE = new Visitor();

	private Visitor() {
	  super(String.class, UUIDAttributeConverter.class);
	}

	public static UUID toUUID(AttributeValue attributeValue) {
	  return EnhancedAttributeValue.fromAttributeValue(attributeValue).convert(Visitor.INSTANCE);
	}

	@Override
	public UUID convertString(String value) {
	  return UUID.fromString(value);
	}

	@Override
	public UUID convertBytes(SdkBytes value) {
	  return UUID.fromString(ByteArrayStringConverter.create().toString(value.asByteArray()));
	}
  }
}
