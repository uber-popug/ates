rootProject.name = "ates"

include(":events")
include(":accounting")
include(":auth")
include(":tasks")

project(":events").projectDir = File("common/events")
project(":accounting").projectDir = File("services/accounting")
project(":auth").projectDir = File("services/auth")
project(":tasks").projectDir = File("services/tasks")
