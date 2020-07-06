package finance

import io.micronaut.runtime.Micronaut


//fun main(args: Array<String>) {
//	build()
//	    .args(*args)
//		//.packages("example")
//		.start()
//}

//@EnableTransactionManagement
open class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Micronaut.run(Application::class.java)
        }
    }
}
