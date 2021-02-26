package finance.services

import finance.domain.Account
import finance.domain.Category
import finance.domain.Transaction
import finance.repositories.AccountRepository
import finance.repositories.CategoryRepository
import finance.repositories.TransactionRepository
import spock.lang.Ignore
import spock.lang.Specification

import javax.validation.Validator

class TransactionServiceSpec extends Specification {
    TransactionRepository mockTransactionRepository = Mock(TransactionRepository)
    AccountRepository mockAccountRepository = Mock(AccountRepository)
    Validator mockValidator = Mock(Validator)
    AccountService accountService = new AccountService(mockAccountRepository, mockValidator)
    CategoryRepository mockCategoryRepository = Mock(CategoryRepository)
    CategoryService categoryService = new CategoryService(mockCategoryRepository)
    TransactionService transactionService = new TransactionService(mockTransactionRepository, accountService, categoryService, mockValidator)

    def "test transactionService - deleteByGuid"() {
        given:
        def guid = "123"
        Transaction transaction = new Transaction()
        Optional<Transaction> transactionOptional = Optional.of(transaction)
        when:
        def isDeleted = transactionService.deleteByGuid(guid)
        then:
        isDeleted
        1 * mockTransactionRepository.deleteByGuid(guid)
        //1 * mockTransactionRepository.deleteByIdFromTransactionCategories(transaction.transactionId)
        1 * mockTransactionRepository.findByGuid(guid) >> transactionOptional
        0 * _
    }

    def "test transactionService - deleteByGuid - no record returned because of invalid guid"() {
        given:
        def guid = "123"
        Optional<Transaction> transactionOptional = Optional.empty()
        when:
        def isDeleted = transactionService.deleteByGuid(guid)
        then:
        !isDeleted
        1 * mockTransactionRepository.findByGuid(guid) >> transactionOptional
        0 * _
    }

    def "test transactionService - findByGuid"() {
        given:
        def guid = "123"
        Transaction transaction = new Transaction()
        Optional<Transaction> transactionOptional = Optional.of(transaction)
        when:
        transactionService.findByGuid(guid)
        then:
        1 * mockTransactionRepository.findByGuid(guid) >> transactionOptional
        0 * _
    }

    def "test transactionService - insert valid transaction"() {
        given:
        def categoryName = "my-category"
        def accountName = "my-account-name"
        def guid = "123"
        Transaction transaction = new Transaction()
        Account account = new Account()
        Category category = new Category()
        Optional<Account> accountOptional = Optional.of(account)
        Optional<Category> categoryOptional = Optional.of(category)
        when:
        transaction.guid = guid
        transaction.accountNameOwner = accountName
        transaction.category = categoryName
        def isInserted = transactionService.insertTransaction(transaction)
        then:
        isInserted.is(true)
        1 * mockTransactionRepository.findByGuid(guid) >> Optional.empty()
        1 * mockValidator.validate(transaction) >> new HashSet()
        1 * mockAccountRepository.findByAccountNameOwner(accountName) >> accountOptional
        1 * mockCategoryRepository.findByCategory(categoryName) >> categoryOptional
        1 * mockTransactionRepository.save(transaction) >> true
        0 * _
    }

    @Ignore
    def "test transactionService - attempt to insert duplicate transaction"() {
        given:
        def categoryName = "my-category"
        def accountName = "my-account-name"
        def guid = "123"
        Transaction transaction = new Transaction()
        Account account = new Account()
        Optional<Transaction> transactionOptional = Optional.of(transaction)
        Optional<Account> accountOptional = Optional.of(account)

        when:
        transaction.guid = guid
        transaction.accountNameOwner = accountName
        transaction.category = categoryName
        def isInserted = transactionService.insertTransaction(transaction)
        then:
        isInserted
        1 * mockValidator.validate(transaction) >> new HashSet()
        //1 * mockTransactionRepository.findByGuid('123')
        1 * mockTransactionRepository.findByGuid(guid) >> transactionOptional
        1 * mockAccountRepository.findByAccountNameOwner('my-account-name') >> accountOptional
        0 * _
    }

    def "test transactionService - insert valid transaction where account name does exist"() {
        given:
        def categoryName = "my-category"
        def accountName = "my-account-name"
        def guid = "123"
        Transaction transaction = new Transaction()
        Account account = new Account()
        Category category
        category = new Category()
        Optional<Account> accountOptional = Optional.of(account)
        Optional<Category> categoryOptional = Optional.of(category)
        when:
        transaction.guid = guid
        transaction.accountNameOwner = accountName
        transaction.category = categoryName
        def isInserted = transactionService.insertTransaction(transaction)
        then:
        isInserted.is(true)
        1 * mockTransactionRepository.findByGuid(guid) >> Optional.empty()
        1 * mockAccountRepository.findByAccountNameOwner(accountName) >> accountOptional
        1 * mockValidator.validate(transaction) >> new HashSet()
        1 * mockCategoryRepository.findByCategory(categoryName) >> categoryOptional
        1 * mockTransactionRepository.save(transaction) >> true
        0 * _
    }

    def "test transactionService - insert valid transaction where account name does not exist"() {
        given:
        def categoryName = "my-category"
        def accountName = "my-account-name"
        def guid = "123"
        Transaction transaction = new Transaction()
        Account account = new Account()
        Category category
        category = new Category()
        Optional<Account> accountOptional = Optional.of(account)
        Optional<Category> categoryOptional = Optional.of(category)
        when:
        transaction.guid = guid
        transaction.accountNameOwner = accountName
        transaction.category = categoryName
        def isInserted = transactionService.insertTransaction(transaction)
        then:
        isInserted
        1 * mockTransactionRepository.findByGuid(guid) >> Optional.empty()
        1 * mockAccountRepository.findByAccountNameOwner(accountName) >> Optional.empty()
        1 * mockAccountRepository.save(_) >> true
        1 * mockValidator.validate(transaction) >> new HashSet()
        1 * mockAccountRepository.findByAccountNameOwner(accountName) >> Optional.empty()
        1 * mockAccountRepository.findByAccountNameOwner(accountName) >> accountOptional
        //TODO: fix this validation
        1 * mockValidator.validate(_) >> new HashSet()
        1 * mockCategoryRepository.findByCategory(categoryName) >> categoryOptional
        1 * mockTransactionRepository.save(transaction) >> true
        0 * _
    }

    def "test transactionService - insert a valid transaction where category name does not exist"() {
        given:
        def categoryName = "my-category"
        def accountName = "my-account-name"
        def guid = "123"
        Transaction transaction = new Transaction()
        Account account = new Account()
        //Category category = new Category()
        Optional<Account> accountOptional = Optional.of(account)
        //Optional<Category> categoryOptional = Optional.of(category)
        when:
        transaction.guid = guid
        transaction.accountNameOwner = accountName
        transaction.category = categoryName
        def isInserted = transactionService.insertTransaction(transaction)
        then:
        isInserted
        1 * mockTransactionRepository.findByGuid(guid) >> Optional.empty()
        1 * mockValidator.validate(transaction) >> new HashSet()
        1 * mockAccountRepository.findByAccountNameOwner(accountName) >> accountOptional
        1 * mockCategoryRepository.findByCategory(categoryName) >> Optional.empty()
        1 * mockCategoryRepository.save(_)
        1 * mockTransactionRepository.save(transaction) >> true
        0 * _
    }
}
