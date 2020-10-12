package com.itel.healthadapter.sandbox.cda.parser;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Var;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link CDAParseResult}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableCDAParseResult.builder()}.
 */
@Generated(from = "CDAParseResult", generator = "Immutables")
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
@CheckReturnValue
public final class ImmutableCDAParseResult implements CDAParseResult {
  private final String patientTaxCode;
  private final ImmutableList<Measurement> plannedMeasurements;

  private ImmutableCDAParseResult(
      String patientTaxCode,
      ImmutableList<Measurement> plannedMeasurements) {
    this.patientTaxCode = patientTaxCode;
    this.plannedMeasurements = plannedMeasurements;
  }

  /**
   * @return The value of the {@code patientTaxCode} attribute
   */
  @Override
  public String patientTaxCode() {
    return patientTaxCode;
  }

  /**
   * @return The value of the {@code plannedMeasurements} attribute
   */
  @Override
  public ImmutableList<Measurement> plannedMeasurements() {
    return plannedMeasurements;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link CDAParseResult#patientTaxCode() patientTaxCode} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for patientTaxCode
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableCDAParseResult withPatientTaxCode(String value) {
    String newValue = Objects.requireNonNull(value, "patientTaxCode");
    if (this.patientTaxCode.equals(newValue)) return this;
    return new ImmutableCDAParseResult(newValue, this.plannedMeasurements);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link CDAParseResult#plannedMeasurements() plannedMeasurements}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableCDAParseResult withPlannedMeasurements(Measurement... elements) {
    ImmutableList<Measurement> newValue = ImmutableList.copyOf(elements);
    return new ImmutableCDAParseResult(this.patientTaxCode, newValue);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link CDAParseResult#plannedMeasurements() plannedMeasurements}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of plannedMeasurements elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableCDAParseResult withPlannedMeasurements(Iterable<? extends Measurement> elements) {
    if (this.plannedMeasurements == elements) return this;
    ImmutableList<Measurement> newValue = ImmutableList.copyOf(elements);
    return new ImmutableCDAParseResult(this.patientTaxCode, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableCDAParseResult} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableCDAParseResult
        && equalTo((ImmutableCDAParseResult) another);
  }

  private boolean equalTo(ImmutableCDAParseResult another) {
    return patientTaxCode.equals(another.patientTaxCode)
        && plannedMeasurements.equals(another.plannedMeasurements);
  }

  /**
   * Computes a hash code from attributes: {@code patientTaxCode}, {@code plannedMeasurements}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    @Var int h = 5381;
    h += (h << 5) + patientTaxCode.hashCode();
    h += (h << 5) + plannedMeasurements.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code CDAParseResult} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("CDAParseResult")
        .omitNullValues()
        .add("patientTaxCode", patientTaxCode)
        .add("plannedMeasurements", plannedMeasurements)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link CDAParseResult} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable CDAParseResult instance
   */
  public static ImmutableCDAParseResult copyOf(CDAParseResult instance) {
    if (instance instanceof ImmutableCDAParseResult) {
      return (ImmutableCDAParseResult) instance;
    }
    return ImmutableCDAParseResult.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableCDAParseResult ImmutableCDAParseResult}.
   * <pre>
   * ImmutableCDAParseResult.builder()
   *    .patientTaxCode(String) // required {@link CDAParseResult#patientTaxCode() patientTaxCode}
   *    .addPlannedMeasurements|addAllPlannedMeasurements(com.itel.healthadapter.sandbox.cda.parser.Measurement) // {@link CDAParseResult#plannedMeasurements() plannedMeasurements} elements
   *    .build();
   * </pre>
   * @return A new ImmutableCDAParseResult builder
   */
  public static ImmutableCDAParseResult.Builder builder() {
    return new ImmutableCDAParseResult.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableCDAParseResult ImmutableCDAParseResult}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "CDAParseResult", generator = "Immutables")
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_PATIENT_TAX_CODE = 0x1L;
    private long initBits = 0x1L;

    private @Nullable String patientTaxCode;
    private ImmutableList.Builder<Measurement> plannedMeasurements = ImmutableList.builder();

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code CDAParseResult} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder from(CDAParseResult instance) {
      Objects.requireNonNull(instance, "instance");
      patientTaxCode(instance.patientTaxCode());
      addAllPlannedMeasurements(instance.plannedMeasurements());
      return this;
    }

    /**
     * Initializes the value for the {@link CDAParseResult#patientTaxCode() patientTaxCode} attribute.
     * @param patientTaxCode The value for patientTaxCode 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder patientTaxCode(String patientTaxCode) {
      this.patientTaxCode = Objects.requireNonNull(patientTaxCode, "patientTaxCode");
      initBits &= ~INIT_BIT_PATIENT_TAX_CODE;
      return this;
    }

    /**
     * Adds one element to {@link CDAParseResult#plannedMeasurements() plannedMeasurements} list.
     * @param element A plannedMeasurements element
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder addPlannedMeasurements(Measurement element) {
      this.plannedMeasurements.add(element);
      return this;
    }

    /**
     * Adds elements to {@link CDAParseResult#plannedMeasurements() plannedMeasurements} list.
     * @param elements An array of plannedMeasurements elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder addPlannedMeasurements(Measurement... elements) {
      this.plannedMeasurements.add(elements);
      return this;
    }


    /**
     * Sets or replaces all elements for {@link CDAParseResult#plannedMeasurements() plannedMeasurements} list.
     * @param elements An iterable of plannedMeasurements elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder plannedMeasurements(Iterable<? extends Measurement> elements) {
      this.plannedMeasurements = ImmutableList.builder();
      return addAllPlannedMeasurements(elements);
    }

    /**
     * Adds elements to {@link CDAParseResult#plannedMeasurements() plannedMeasurements} list.
     * @param elements An iterable of plannedMeasurements elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder addAllPlannedMeasurements(Iterable<? extends Measurement> elements) {
      this.plannedMeasurements.addAll(elements);
      return this;
    }

    /**
     * Builds a new {@link ImmutableCDAParseResult ImmutableCDAParseResult}.
     * @return An immutable instance of CDAParseResult
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableCDAParseResult build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableCDAParseResult(patientTaxCode, plannedMeasurements.build());
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_PATIENT_TAX_CODE) != 0) attributes.add("patientTaxCode");
      return "Cannot build CDAParseResult, some of required attributes are not set " + attributes;
    }
  }
}
