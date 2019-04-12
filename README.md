[![Build Status](https://api.travis-ci.org/spinnaker/kork.svg?branch=master)](https://travis-ci.org/spinnaker/kork)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Farmory-io%2Fkork.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Farmory-io%2Fkork?ref=badge_shield)

Kork provides some basic service building blocks for Spinnaker.

Additionally Kork adapts NetflixOSS platform components to Spring configuration and Spring-Boot autoconfiguration.

This project provides Spring bindings for NetflixOSS components that are typically exposed and configured via the internal Netflix Platform. The exposed Bean bindings are set up with reasonable defaults and limited assumptions about the existence of other infrastructure services. Using Spring-Boot AutoConfiguration, they will only conditionally load in an environment where the internal Netflix platform is not available.



## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Farmory-io%2Fkork.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Farmory-io%2Fkork?ref=badge_large)