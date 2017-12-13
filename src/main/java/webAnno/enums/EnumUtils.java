package webAnno.enums;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class EnumUtils {
    private static final String defaultValueNameReference = "DEFAULT";
    private static final Set<Class> approvedClasses = new HashSet<>();

    public static <E extends Enum<E>> E getValue(Class<E> givenClass, final String identity) {
        optimizedValidation(givenClass);

        E[] enumConstants = givenClass.getEnumConstants();
        E expectedEnum = findEnum(enumConstants, identity);
        Supplier<E> defaultValue = () -> getDefaultValue(enumConstants);

        return expectedEnum != null ? expectedEnum : defaultValue.get();
    }

    private static <E extends Enum<E>> void optimizedValidation(Class<E> givenClass) {
        if (!approvedClasses.contains(givenClass)) {

            if (!givenClass.isEnum())
                throw new IllegalArgumentException("Given class is not enum");

            if (getDefaultValue(givenClass.getEnumConstants()) == null)
                throw new IllegalStateException("Delivered enum class must contain \"DEFAULT\" route");

            approvedClasses.add(givenClass);
        }
    }

    private static <E extends Enum<E>> E getDefaultValue(E[] enumConstants) {
        return findEnum(enumConstants, defaultValueNameReference);
    }

    private static <E extends Enum<E>> E findEnum(E[] enumConstants, final String givenName) {
        BiPredicate<E, String> isEqualByName = (enumConst, identity) -> enumConst.toString()
                                                                                 .equals(identity);

        return Arrays.stream(enumConstants)
                     .filter(e -> isEqualByName.test(e, givenName))
                     .findFirst()
                     .orElse(null);
    }
}
