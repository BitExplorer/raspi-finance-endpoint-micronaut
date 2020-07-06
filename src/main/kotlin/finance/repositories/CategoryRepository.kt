package finance.repositories

import finance.domain.Category
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*


@Repository
interface CategoryRepository : CrudRepository<Category, Long> {
    fun findByCategory(category: String): Optional<Category>
    fun deleteByCategory(categoryName: String)
}