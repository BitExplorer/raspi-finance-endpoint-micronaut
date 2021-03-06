package finance.domain

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import finance.helpers.CategoryBuilder
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class CategorySpec extends Specification {

    protected ValidatorFactory validatorFactory
    protected Validator validator
    protected ObjectMapper mapper = new ObjectMapper()
    protected String jsonPayload = '{"category":"bar", "activeStatus":true}'

    void setup() {
        validatorFactory = Validation.buildDefaultValidatorFactory()
        validator = validatorFactory.getValidator()
    }

    void cleanup() {
        validatorFactory.close()
    }

    void 'test -- JSON serialization to Category'() {

        when:
        Category category = mapper.readValue(jsonPayload, Category)

        then:
        category.category == "bar"
        0 * _
    }

    @Unroll
    void 'test -- JSON deserialize to Category with invalid payload'() {
        when:
        mapper.readValue(payload, Category)

        then:
        Exception ex = thrown(exceptionThrown)
        ex.message.contains(message)
        0 * _

        where:
        payload                   | exceptionThrown          | message
        'non-jsonPayload'         | JsonParseException       | 'Unrecognized token'
        '[]'                      | MismatchedInputException | 'Cannot deserialize value of type'
        '{category: "test"}'      | JsonParseException       | 'was expecting double-quote to start field name'
        '{"activeStatus": "abc"}' | InvalidFormatException   | 'Cannot deserialize value of type'
    }

    void 'test JSON deserialization to Category object - category is empty'() {

        given:
        String jsonPayloadBad = '{"categoryMissing":"bar"}'

        when:
        Category category = mapper.readValue(jsonPayloadBad, Category)

        then:
        category.category.empty
        0 * _
    }

    void 'test validation valid category'() {
        given:
        Category category = CategoryBuilder.builder().build()
        category.category = "foobar"

        when:
        Set<ConstraintViolation<Category>> violations = validator.validate(category)

        then:
        violations.empty
    }
}
