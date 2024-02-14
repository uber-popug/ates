workspace {
    model {
        user = person "Client" "A user of the PUber"
        puber = softwareSystem "PUber" "Uber for the Popugs" {
            user -> this "Order"
        }

        employee = person "Employee"
        auth = softwareSystem "Auth" "UberPopug Auth"
        ates = softwareSystem "Awesome Task Exchange System" "UberPopug aTES" {
            this -> auth "Auth"

            spa = container "Web application" {
                tags Web
                employee -> this "Uses"
            }

            tasks = container "Tasks"
            account = container "Accounting"
            analytics = container "Analytics"
            notifications = container "Notifications" {
                this -> employee "Send dayly reports"
            }

            summarize = container "Summarize" {
                tags "Robot"
                this -> notifications "Send reports"
                this -> analytics "Update"
            }

            gateway = container "Gateway" {
                spa -> this "Rest API"

                this -> auth "Auth"

                this -> tasks "Assignee"
                this -> tasks "List"
                this -> tasks "Complete"

                this -> account "Balance"
                this -> account "Logs"
                this -> account "Stats"
            }
        }
    }

    views {
        systemLandscape {
            include *
            autoLayout
        }

        container ates {
            include *
            autolayout lr
        }

        styles {
            element "Software System" {
                background #1168bd
                color #ffffff
            }

            element "Person" {
                shape person
                background #08427b
                color #ffffff
            }

            element "Robot" {
                shape robot
            }

            element "Web" {
                shape WebBrowser
            }
        }
    }
}