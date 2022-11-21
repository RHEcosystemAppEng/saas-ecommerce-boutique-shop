# Red Hat SaaS Tenant Manager Frontend 🌟✨

<img style="float: right;" src="./doc/img/Logo-Red_Hat-A-Standard-RGB.svg" title="Apache Kafka" width="400" align="right">

This project contains the basic user interfaces where users can request for an online ecommerce web-shop on demand
with customizable resource requirements. This project is intend as a reference implementation example for any customer
who is willing to offer their software solutions as a tenant based subscription model.

### Overview 🛰️

This project only consist the frontend application which has written using React JS framework. 
Project also follows the [patternfly UI framework](https://www.patternfly.org/v4/).

### Demo 📺
<img style="float: right;" src="./doc/img/tenant-manager-ui.gif">


### Configuration ⚙️

Define the saas-manager-backend url as an environment variable.

| Variable Name |        Description         |    Example     |
| :---: |:--------------------------:|:--------------:|
| `REACT_APP_BACKEND_URI` | Tenant Manager Backend URL | localhost:8080 |

Start the application by running:
```shell
npm run start
```

Application will run in [http://localhost:3000/](http://localhost:3000/)
