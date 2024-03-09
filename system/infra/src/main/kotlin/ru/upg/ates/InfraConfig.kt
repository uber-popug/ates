package ru.upg.ates

data class InfraConfig(
    val kafkaUrl: String,
    val schemasPath: String,
    val services: Services,
    val databases: Databases
) {
    data class Service(
        val name: String,
        val port: Int
    )
    
    data class Services(
        val auth: Service,
        val tasks: Service,
        val billing: Service,
        val analytic: Service
    )
    
    data class Db(
        val url: String, 
        val username: String, 
        val password: String
    )
    
    data class Databases(
        val auth: Db,
        val tasks: Db,
        val billing: Db,
        val analytic: Db
    )
    
    companion object {
        val local = InfraConfig(
            kafkaUrl = "https://localhost:9994",
            schemasPath = "C:\\dev\\reps\\uber-popug\\awesome-task-exchange-system\\system\\events\\events-schema-registry\\schemas",
            services = Services(
                auth = Service("ates-auth", 10000),
                tasks = Service("ates-tasks", 20000),
                billing = Service("ates-billing", 30000),
                analytic = Service("ates-analytic", 40000),
            ),
            databases = Databases(
                auth = Db("jdbc:postgresql://localhost:15432/ates-auth", "postgres", "postgres"),
                tasks = Db("jdbc:postgresql://localhost:25432/ates-tasks", "postgres", "postgres"),
                billing = Db("jdbc:postgresql://localhost:35432/ates-billing", "postgres", "postgres"),
                analytic = Db("jdbc:postgresql://localhost:45432/ates-analytic", "postgres", "postgres"),
            )
        )
    }
}
