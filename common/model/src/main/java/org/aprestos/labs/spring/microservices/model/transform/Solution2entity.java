package org.aprestos.labs.spring.microservices.model.transform;

import org.aprestos.labs.spring.microservices.model.entities.Item;
import org.aprestos.labs.spring.microservices.model.entities.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Solution2entity implements Function<org.aprestos.labs.spring.microservices.model.dto.Solution, Solution> {

    @Override
    public Solution apply(org.aprestos.labs.spring.microservices.model.dto.Solution solution) {
        Solution result = new Solution();
        List<Item> items = new ArrayList<>();
        result.setItems(items);

        if(null != solution.getItems() && 0 < solution.getItems().length){
            Item2entity mapper = new Item2entity();
            for( org.aprestos.labs.spring.microservices.model.dto.Item item: solution.getItems() )
                items.add(mapper.apply(item));
        }

        result.setIdent(solution.getId());
        result.setTime(solution.getTime());
        result.setValue(solution.getValue());

        return result;
    }
}
