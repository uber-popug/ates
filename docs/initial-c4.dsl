workspace {
    model {
    group "UberPopug Inc." {
        employee = person "Employee"
        
        email = softwareSystem "E-mail System" "The internal UberPopug e-mail system"
        auth = softwareSystem "Auth" "UberPopug OAuth Server"
        
        ates = softwareSystem "Awesome Task Exchange System" "UberPopug aTES" "aTES" {
            tags aTES
            this -> auth "Authorize requests"
            
            spa = container "aTES Web Application" {
                tags Web
            }
        
            taskDb = container "Task DB" {
                tags Database
            }
            accountDb = container "Accounting DB" {
                tags Database
            }
            
            tasks = container "Tasks"
            account = container "Accounting"
            analytics = container "Analytics"
            notifications = container "Notifications"

            summarize = container "Summarize" {
                tags "Robot"
                this -> notifications "Send reports"
                this -> analytics "Update"
            }

            gateway = container "Gateway"
            
            
            gateway -> auth "Authorize requests"
            gateway -> tasks "Assignee"
            gateway -> tasks "List"
            gateway -> tasks "Complete"
            gateway -> account "Balance"
            gateway -> account "Logs"
            gateway -> analytics "Stats"
            
            spa -> gateway "REST API"
        }
        
        
        # employee interactions
        employee -> auth "SSO"
        employee -> spa "Do tasking"
        employee -> email "Read dayly reports"
        notifications -> email "Send dayly reports"
    }
    }

    views {
        systemLandscape {
            include employee auth ates email
            autolayout tb
        }

        container ates {
            include *
            autolayout bt
        }

        styles {
            element "aTES" {
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
            
            element "Database" {
                shape cylinder
            }
        }
    }
}
