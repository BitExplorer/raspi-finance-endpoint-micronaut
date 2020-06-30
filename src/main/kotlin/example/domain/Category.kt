package example.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.hibernate.annotations.Proxy
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@Entity
@Proxy(lazy = false)
//@Table(name = "t_category")
data class Category(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Min(value = 0L)
        @JsonProperty
        var categoryId: Long,

        @Size(min = 1, max = 50)
        @Column(unique = true)
        @JsonProperty
        var category: String

) {
    constructor() : this(0L, "")

    override fun toString(): String = mapper.writeValueAsString(this)

    companion object {
        @JsonIgnore
        private val mapper = ObjectMapper()
    }
}

//TODO: add active_status field
//    @JsonProperty
//    var dateUpdated
//
//    @JsonProperty
//    var dateAdded