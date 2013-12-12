Autoscaler App Component
========================

TODO: App Summary

You must include a file called `cf-security.yml` in the location `src/main/resources`. The contents of that file must contain the email and password for the Cloud Foundry user that will be scaling the worker-process via the Cloud Controller API. For example:

    ---
    email: user@domain.com
    password: sup3rs3cur3

You'll also need to check the accuracy of the "org" and "space" included in `src/main/resources/application.yml`.

Once you've verified the correct property values have been set, you can build from source:

    ./gradlew assemble

And then push the application to Cloud Foundry.


