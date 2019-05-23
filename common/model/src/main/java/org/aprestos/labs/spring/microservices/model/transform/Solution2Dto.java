package org.aprestos.labs.spring.microservices.model.transform;

import org.aprestos.labs.spring.microservices.model.dto.Item;
import org.aprestos.labs.spring.microservices.model.dto.Solution;

import java.util.function.Function;

public class Solution2Dto implements Function<org.aprestos.labs.spring.microservices.model.entities.Solution, Solution> {

    @Override
    public Solution apply(org.aprestos.labs.spring.microservices.model.entities.Solution solution) {
        Item[] items;
        if(null != solution.getItems() && !solution.getItems().isEmpty()){
            Item2Dto mapper = new Item2Dto();
            items = new Item[solution.getItems().size()];
            int i = 0;
            for( org.aprestos.labs.spring.microservices.model.entities.Item item: solution.getItems() )
                items[i++] = mapper.apply(item);
        }
        else {
            items = new Item[]{};
        }
        return new Solution(solution.getIdent(), items, solution.getTime(), solution.getValue());
    }
}
