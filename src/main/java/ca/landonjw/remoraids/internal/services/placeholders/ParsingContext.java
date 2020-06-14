package ca.landonjw.remoraids.internal.services.placeholders;

import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ParsingContext implements IParsingContext {

    private Map<Class, List<Supplier>> contextObjects;

    ParsingContext(Map<Class, List<Supplier>> contextObjects){
        this.contextObjects = contextObjects;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get(@Nonnull Class<T> clazz) {
        if(contextObjects.containsKey(clazz) && !contextObjects.get(clazz).isEmpty()){
            if(clazz.isInstance(contextObjects.get(clazz).get(0).get())) {
                return Optional.of((T) contextObjects.get(clazz).get(0).get());
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<List<T>> getAll(@Nonnull Class<T> clazz) {
        if(contextObjects.containsKey(clazz)){
            //Create new list and populate it. This is to prevent any modification of the internal state.
            List<T> results = Lists.newArrayList();
            //Exclude any null results
            contextObjects.get(clazz).stream()
                    .filter((element) -> element != null && element.get() != null)
                    .forEach((element) -> results.add((T) element.get()));
            return Optional.of(results);
        }
        return Optional.empty();
    }

    @Override
    public Map<Class, List<Supplier>> getAll(){
        //Create new hash map and populate it. This is to prevent any modification of the internal state.
        Map<Class, List<Supplier>> newMap = Maps.newHashMap();
        for(Class clazz : contextObjects.keySet()){
            newMap.put(clazz, Lists.newArrayList(contextObjects.get(clazz)));
        }
        return newMap;
    }

    public static class ParsingContextBuilder implements IParsingContext.Builder {

        private Map<Class, List<Supplier>> contextObjects = new HashMap<>();

        @Override
        public <T> Builder add(@Nonnull Class<T> clazz, @Nonnull Supplier<T> supplier) {
            if(supplier.get() != null){
                if(contextObjects.containsKey(clazz)){
                    contextObjects.get(clazz).add(supplier);
                }
                else{
                    contextObjects.put(clazz, Arrays.asList(supplier));
                }
            }
            return this;
        }

        @Override
        public <T> Builder addAll(@Nonnull Class<T> clazz, @Nonnull List<Supplier<T>> supplierList) {
            if(contextObjects.containsKey(clazz)){
                contextObjects.get(clazz).addAll(supplierList.stream().filter(Objects::nonNull).collect(Collectors.toList()));
            }
            else{
                contextObjects.put(clazz, supplierList.stream().filter(Objects::nonNull).collect(Collectors.toList()));
            }
            return this;
        }

        @Override
        public Builder from(IParsingContext input) {
            this.contextObjects = input.getAll();
            return this;
        }

        @Override
        public ParsingContext build() {
            return new ParsingContext(contextObjects);
        }

    }

}
