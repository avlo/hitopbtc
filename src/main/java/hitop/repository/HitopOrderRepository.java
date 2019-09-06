package hitop.repository;

import org.springframework.data.repository.CrudRepository;
import hitop.entity.HitopOrder;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface HitopOrderRepository extends CrudRepository<HitopOrder, Integer> {
}
