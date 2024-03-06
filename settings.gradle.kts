rootProject.name = "awesome-task-exchange-system"

/*
fun module(path: String, name: String = path) {
    val moduleName = ":${path.replace("/", ":")}"
    include(moduleName)
    project(moduleName).let { project ->
        project.projectDir = File("system/$path")
        project.name = name
    }
}

module("common")
module("domains/auth", "auth")
module("domains/tasks", "tasks")
module("services/tasks-service", "tasks-service")
*/

include(":system:common")
include(":system:domains:auth")
include(":system:domains:tasks")
include(":system:services:tasks-service")