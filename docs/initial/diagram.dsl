workspace {
    model {
        properties {
            "structurizr.groupSeparator" "/"
        }

        group "UberPopug Inc." {
            employee = person "Employee"

            email = softwareSystem "E-mail System" "The internal UberPopug e-mail system"

            group "Users management" {
                auth = softwareSystem "Auth" "UberPopug OAuth Server"
            }


            ates = softwareSystem "Awesome Task Exchange System" "UberPopug aTES" {
                tags aTES

                broker = container "Event Broker" "" "Kafka" {
                    tags Broker
                }
                notifications = container "Notifications"

                group "Tasks management" {
                    tasks = container "Tasks Service" {
                        tasksService = component "Tasks Service"
                        taskDb = component "Task DB" "PostgreSQL" {
                            tags Database
                        }
                        tasksService -> taskDb "Update tasks"
                    }
                }

                group "Accounting management" {
                    accounting = container "Accounting Service" {
                        accountDb = component "Accounting DB" "ClickHouse" {
                            tags Database
                        }

                        account = component "Account"
                        analytics = component "Analytics"
                        summarize = component "Summarize" {
                            tags "Robot"
                        }

                        account -> accountDb "Track changes"
                        analytics -> accountDb "Calc stats"
                        summarize -> accountDb "Calc dayly payments"

                        summarize -> broker "Dayly report notification" "Produce"
                    }
                }

                gateway = container "Gateway" {
                    tags Gateway
                }
                spa = container "aTES Web Application" {
                    tags Web
                    this -> gateway "REST API" "HTTPS"
                }


                gateway -> auth "Authorize requests" "HTTPS"
                gateway -> tasks "Assignee/Complete" "HTTPS"
                gateway -> tasks "List" "HTTPS"
                gateway -> accounting "Balance/Logs/Stats" "HTTPS"

                tasks -> broker "Task updates" "Produce"
                accounting -> broker "Task updates" "Consume"
                notifications -> broker "Consume"
            }


            # employee interactions
            employee -> auth "SSO"
            employee -> spa "Do tasking"
            employee -> email "Read dayly reports"
            notifications -> email "Send"
        }
    }

    views {
        systemLandscape {
            include employee auth ates email
            autolayout tb
        }

        container ates {
            default
            include *
            exclude employee
            autolayout tb
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

            element "Broker" {
                shape Pipe
                width 2000
                //height 1000
            }

            element "Gateway" {
                //width 5000
            }

            relationship "Relationship" {
                routing direct
            }
        }
    }
}
