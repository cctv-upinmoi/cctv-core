package init.upinmcse.cctvcore.util.collection;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CollectionUtils {
    public static <T, U> List<U> convertList(T[] from, Function<T, U> func) {
        if (from.length == 0){
            return new ArrayList<>();
        }
        return convertList(Arrays.asList(from), func);
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        if (from.isEmpty()) {
            return new ArrayList<>();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (from.isEmpty()) {
            return new ArrayList<>();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
