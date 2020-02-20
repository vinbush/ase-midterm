# ase-midterm

To run with tests, go into `midterm` directory, then `mvn clean thorntail:run`.

TeamResource integration tests are completed and working.

To deploy to minishift, go into `midterm` and run `mvn clean fabric8:deploy -Popenshift`.

To undeploy, use `mvn fabric8:undeploy -Popenshift`.
